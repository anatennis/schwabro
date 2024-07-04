package com.example.schwabro.depnotes;

import com.example.schwabro.util.DepNoteUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AddDepNoteAction extends AnAction {
    public static final String RELEASE = "release";
    private FileSystemTreeImpl fileSystemTree;
    private Project project;
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
        DepNoteUtils.createNewFile(fileSystemTree, project, null, null);
    }
}
