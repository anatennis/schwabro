package com.example.schwabro;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.UIBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AddDepNote extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(editor.getProject()).getContentRoots();
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
        FileSystemTreeImpl fileSystemTree = new FileSystemTreeImpl(editor.getProject(), fileChooserDescriptor);
        createNewFile(contentRoots[0], fileSystemTree);
    }

    static void createNewFile(final VirtualFile file, final FileSystemTreeImpl fileSystemTree) {
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
        //todo create file tree to folder
        Exception failReason = fileSystemTree.createNewFile(file, newFileName, YAMLFileType.YML, createDNTemplate());
        if (failReason != null) {
            Messages.showMessageDialog(UIBundle.message("create.new.file.could.not.create.file.error.message", newFileName),
                    UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
        }
    }

    private static String generateDNName() {
        //todo
        return "TOSX-0000";
    }

    private static String createDNTemplate() {
        //todo add DN template
        return "content";
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
