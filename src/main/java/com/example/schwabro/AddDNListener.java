package com.example.schwabro;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddDNListener implements BulkFileListener, ApplicationComponent {
    private final AtomicBoolean wasTriggered = new AtomicBoolean(false);

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        BulkFileListener.super.after(events);
        //add check in events if it's property file
        if (wasTriggered.compareAndSet(false, true))
            addPopUpConfirmation();
    }

    private void addPopUpConfirmation() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length == 0) {
            wasTriggered.set(false);//todo will be removed after check adding
            return;
        }
        JFrame frame = WindowManager.getInstance().getFrame(openProjects[0]);
        JBPopupFactory.getInstance()
                .createConfirmation(
                        "Looks Like New Property Was Added. Would You Like to Add Deployer Note for It?",
                        () -> {
                            VirtualFile[] contentRoots = ProjectRootManager.getInstance(openProjects[0]).getContentRoots();
                            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
                            FileSystemTreeImpl fileSystemTree = new FileSystemTreeImpl(openProjects[0], fileChooserDescriptor);
                            AddDepNote.createNewFile(contentRoots[0], fileSystemTree);
                        }, 0)
                .show(RelativePoint.getCenterOf(frame.getLayeredPane()));
    }
}
