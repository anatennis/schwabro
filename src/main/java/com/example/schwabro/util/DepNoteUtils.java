package com.example.schwabro.util;

import com.example.schwabro.depnotes.AddDepNoteAction;
import com.example.schwabro.depnotes.ChangeInfo;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.UIBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class DepNoteUtils {

    private static String FILE_TEMPLATE = "/src/main/resources/templates/template.yaml";

    public static String addInfo(Set<ChangeInfo> files, Set<String> checkedFiles, Project project) {
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        StringBuilder info = new StringBuilder();
        for (ChangeInfo doc : files) {
            if (!checkedFiles.contains(doc.getFileName()))
                continue;
            Change change = doc.getChange() == null ? changeListManager.getChange(doc.getFile()) : doc.getChange();
            if (change == null && doc.isFirstTimeChanged()) {
                String changesString = doc.getContent().replace('\n', ',');
                return info.append("    New properties: ").append(changesString).toString();
            }
            if (change == null) {
                return info.toString();
            }
            ContentRevision beforeRevision = change.getBeforeRevision();
            ContentRevision afterRevision = change.getAfterRevision();
            if (beforeRevision != null && afterRevision != null) {
                try {
                    String beforeContent = beforeRevision.getContent();
                    String afterContent = afterRevision.getContent();
                    if (beforeContent != null && afterContent != null) {
                        calculateDifferences(beforeContent, afterContent, info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return info.toString();
    }

    private static void calculateDifferences(String beforeContent, String afterContent, StringBuilder info)
            throws DiffException {
        List<String> beforeLines = Arrays.asList(beforeContent.split("\n"));
        List<String> afterLines = Arrays.asList(afterContent.split("\n"));
        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .inlineDiffByWord(true)
                .build();
        List<DiffRow> rows = generator.generateDiffRows(beforeLines, afterLines);
        for (DiffRow row : rows) {
            if (!row.getTag().equals(DiffRow.Tag.EQUAL)) {
                if (row.getTag() == DiffRow.Tag.INSERT || row.getTag() == DiffRow.Tag.CHANGE && row.getOldLine().isEmpty())
                    info.append("    New property was added: ")
                            .append(removeSpanTags(row.getNewLine()))
                            .append('\n');
                else if (row.getTag() == DiffRow.Tag.CHANGE && !row.getNewLine().isEmpty())
                    info.append("    This property ")
                            .append(removeSpanTags(row.getOldLine()))
                            .append(" was changed to ")
                            .append(removeSpanTags(row.getNewLine()))
                            .append('\n');
                else
                    info.append("    This property was removed: ")
                            .append(removeSpanTags(row.getOldLine()))
                            .append('\n');
            }
        }
    }

    public static String removeSpanTags(String text) {
        String spanTagPattern = "</?span[^>]*>";
        return text.replaceAll(spanTagPattern, "");
    }

    public static String createDNTemplate(Project project, Set<ChangeInfo> files, Set<String> checkedFiles) {
        String ticketName = GitUtils.getTicketName(project);
        String result = "";
        InputStream is = DepNoteUtils.class.getResourceAsStream("/templates/template.yaml");
        if (is == null)
            throw new IllegalStateException("template wasn't found");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
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

    public static void createNewFile(final FileSystemTreeImpl fileSystemTree, Project project,
                                     Set<ChangeInfo> changedFiles, Set<String> checkedFiles) {
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

        Collection<VirtualFile> release = FilenameIndex.getVirtualFilesByName(AddDepNoteAction.RELEASE, GlobalSearchScope.allScope(project));
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
