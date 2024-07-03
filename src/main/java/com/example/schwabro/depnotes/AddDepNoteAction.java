package com.example.schwabro.depnotes;

import com.example.schwabro.util.GitUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.UIBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import static com.example.schwabro.util.DepNoteUtils.addInfo;

public class AddDepNoteAction extends AnAction {
    public static final String RELEASE = "release";
    private FileSystemTreeImpl fileSystemTree;
    private Project project;
    static String FILE_TEMPLATE = "src/main/resources/templates/template.yaml";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (project == null) {
            Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
            project = editor.getProject();
            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
            fileSystemTree = new FileSystemTreeImpl(project, fileChooserDescriptor);
        }
        createNewFile(fileSystemTree, project);
    }

    public static void createNewFile(final FileSystemTreeImpl fileSystemTree, Project project) {
        createNewFile(fileSystemTree, project, null, null);
    }

    public static void createNewFile(final FileSystemTreeImpl fileSystemTree, Project project,
                                     Set<AddDepNoteListener.ChangeInfo> changedFiles, Set<String> checkedFiles) {
        String newFileName = GitUtils.getTicketName(project) + ".yml";
        String releaseBranchFolder = Messages.showInputDialog("Please enter release branch name",
                UIBundle.message("new.file.dialog.title"), null);
        if (releaseBranchFolder == null) {
            return;
        }
        releaseBranchFolder = releaseBranchFolder.strip();
        while (true) {
            if (releaseBranchFolder.isEmpty()) {
                Messages.showMessageDialog(UIBundle.message("create.new.file.file.name.cannot.be.empty.error.message"),
                        UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
            } else {
                break;
            }
        }

        Collection<VirtualFile> release = FilenameIndex.getVirtualFilesByName(RELEASE, GlobalSearchScope.allScope(project));
        VirtualFile releaseFolder = release.iterator().next();
        Exception failReason = fileSystemTree.createNewFolder(releaseFolder, releaseBranchFolder);
        if (failReason != null) {
            if (!failReason.getMessage().contains("already exists"))
                Messages.showMessageDialog(UIBundle.message("create.new.file.could.not.create.file.error.message", newFileName),
                        UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
        }
        Collection<VirtualFile> branchFolder = FilenameIndex.getVirtualFilesByName(releaseBranchFolder, GlobalSearchScope.allScope(project));
        failReason = fileSystemTree.createNewFile(branchFolder.iterator().next(), newFileName, YAMLFileType.YML,
                createDNTemplate(project, changedFiles, checkedFiles));
        if (failReason != null) {
            if (!failReason.getMessage().contains("already exists"))
                Messages.showMessageDialog(UIBundle.message("create.new.file.could.not.create.file.error.message", newFileName),
                        UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
        }
    }

    private static String createDNTemplate(Project project, Set<AddDepNoteListener.ChangeInfo> files, Set<String> checkedFiles) {
        String ticketName = GitUtils.getTicketName(project);
        String result = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_TEMPLATE))) {
            String text;
            while ((text = reader.readLine()) != null) {
                if (text.contains("TOSX")) {
                    text = ticketName + ":";
                }
                if (text.contains("INFO") && (files != null && !files.isEmpty())) {
                    text = text.replace("    INFO", addInfo(files, checkedFiles, project));
                }
                if (text.contains("INSTRUCTIONS")) {
                    text = "      Add parameter";
                }

                result = result.concat(text + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static final class YAMLFileType implements FileType {
        public static final YAMLFileType YML = new YAMLFileType();
        @NonNls
        public static final String DEFAULT_EXTENSION = "yml";

        private YAMLFileType() {
        }

        @Override
        @NotNull
        public String getName() {
            return "YAML";
        }

        @Override
        @NotNull
        public String getDescription() {
            return "Filetype.yaml.description";
        }

        @Override
        @NotNull
        public String getDefaultExtension() {
            return DEFAULT_EXTENSION;
        }

        @Override
        @NotNull
        public Icon getIcon() {
            return AllIcons.FileTypes.Yaml;
        }

        @Override
        public boolean isBinary() {
            return false;
        }
    }
}
