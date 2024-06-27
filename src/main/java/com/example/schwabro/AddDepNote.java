package com.example.schwabro;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.UIBundle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

public class AddDepNote extends AnAction {
    static String FILE_TEMPLATE = "src/main/resources/templates/template.yaml";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(editor.getProject()).getContentRoots();
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
        FileSystemTreeImpl fileSystemTree = new FileSystemTreeImpl(editor.getProject(), fileChooserDescriptor);

        createNewFile(contentRoots[0], fileSystemTree, editor.getProject());
    }

    public static void createNewFile(final VirtualFile file, final FileSystemTreeImpl fileSystemTree, Project project) {
        String newFileName = generateDNName();
        String folder = Messages.showInputDialog("Please enter release branch name",
                UIBundle.message("new.file.dialog.title"), Messages.getQuestionIcon());
        if (folder == null) {
            return;
        }
        folder = folder.strip();
        if (folder.isEmpty()) {
            Messages.showMessageDialog(UIBundle.message("create.new.file.file.name.cannot.be.empty.error.message"),
                    UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
        }
        Exception failReason = fileSystemTree.createNewFolder(file, folder);
        if (failReason != null) {
            if (!failReason.getMessage().contains("already exists"))
                Messages.showMessageDialog(UIBundle.message("create.new.file.could.not.create.file.error.message", newFileName),
                        UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
        }
        Collection<VirtualFile> virtualFilesByName = FilenameIndex.getVirtualFilesByName(folder, GlobalSearchScope.allScope(project));
        failReason = fileSystemTree.createNewFile(virtualFilesByName.iterator().next(), newFileName, YAMLFileType.YML, createDNTemplate());
        if (failReason != null) {
            if (!failReason.getMessage().contains("already exists"))
                Messages.showMessageDialog(UIBundle.message("create.new.file.could.not.create.file.error.message", newFileName),
                        UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
        }
    }

    private static String generateDNName() {
        String branchName = getCurrentGitBranch();
        return branchName.substring(0, branchName.indexOf("-", 5)) + ".yml";
    }

    private static String createDNTemplate() {
        String branchName = getCurrentGitBranch();
        String ticketName = branchName.substring(0, branchName.indexOf("-", 5));
        String result = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_TEMPLATE))) {
            String text;
            while ((text = reader.readLine()) != null) {
                if (text.contains("TOSX")) {
                    text = ticketName + ":";
                }

                if (text.contains("INFO")) {
                    text = "    " + branchName.substring(
                            branchName.indexOf("-", 5)).replace("-", " ");
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

    private static String getCurrentGitBranch() {
        try {
            Process process = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD");
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            return reader.readLine();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "TOSX-0000";
        }
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
