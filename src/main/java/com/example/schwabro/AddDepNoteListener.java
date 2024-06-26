package com.example.schwabro;

import com.example.schwabro.util.ButtonContent;
import com.intellij.icons.AllIcons;
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
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBList;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class AddDepNoteListener implements BulkFileListener, ApplicationComponent {
    private final AtomicBoolean wasTriggered = new AtomicBoolean(false);
    private final Set<String> changedFiles = new HashSet<>();

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        BulkFileListener.super.after(events);
        List<String> propFilesEvents = events.stream()
                .map(e -> e.getFile().getName())
                .filter(file -> file.contains(".properties"))
                .collect(Collectors.toList());
        changedFiles.addAll(propFilesEvents);
        if (!propFilesEvents.isEmpty() && wasTriggered.compareAndSet(false, true))
            addPopUpConfirmation();
        else if (!propFilesEvents.isEmpty())
            createToolWindowContent(ProjectManager.getInstance().getOpenProjects()[0]);
    }

    private void addPopUpConfirmation() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length == 0) {
            return;
        }
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(openProjects[0]);
        AtomicBoolean wasAdded = new AtomicBoolean(false);
        JBPopupFactory.getInstance()
                .createConfirmation(
                        "Would You Like to Add Deployer Note for Changed Property?",
                        () -> {
                            VirtualFile[] contentRoots = ProjectRootManager.getInstance(openProjects[0]).getContentRoots();
                            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
                            FileSystemTreeImpl fileSystemTree = new FileSystemTreeImpl(openProjects[0], fileChooserDescriptor);
                            AddDepNote.createNewFile(contentRoots[0], fileSystemTree, openProjects[0]);
                            wasAdded.set(true);
                        }, 0)
                .show(RelativePoint.getNorthWestOf(statusBar.getComponent()));
        if (!wasAdded.get())
            createToolWindowContent(openProjects[0]);
    }

    public void createToolWindowContent(@NotNull Project project) {
        ToolWindow depNote = ToolWindowManager.getInstance(project).getToolWindow("DepNote");
        depNote.setIcon(AllIcons.General.Warning);
        ContentManager contentManager = depNote.getContentManager();
        //update content
        Content content = contentManager.getContents()[0];
        contentManager.removeContent(content, false);
        Content buttonContent = ContentFactory.SERVICE.getInstance().createContent(
                createControlsPanel(depNote, project), "Modified .properties Files", false);
        contentManager.addContent(buttonContent, 0);
    }

    private JPanel createControlsPanel(ToolWindow toolWindow, @NotNull Project project) {
        JPanel controlsPanel = new JPanel();
        JLabel jLabel = new JLabel("Don't forget to add Deployer Note,");
        jLabel.setSize(250, 20);
        controlsPanel.add(jLabel);
        JLabel jLabel1 = new JLabel("these .properties files were changed");
        jLabel1.setSize(250, 20);
        controlsPanel.add(jLabel1);

        DefaultListModel<String> dlm = JBList.createDefaultListModel(changedFiles);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());

        JBList<String> list = new JBList<>(dlm);
        list.setFixedCellWidth(200);

        jPanel.add(list);
        list.setCellRenderer(new DNRenderer());
        controlsPanel.add(jPanel);
        controlsPanel.add(Box.createHorizontalStrut(200));
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(ButtonContent.createControlsPanel(toolWindow));
        return controlsPanel;
    }
    public static class DNRenderer extends JLabel implements ListCellRenderer<String> {
        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            setIcon(AllIcons.FileTypes.Yaml);
            setHorizontalAlignment(SwingConstants.LEFT);
            Font currentFont = getFont();
            Font newFont = currentFont.deriveFont(14.0f);
            setFont(newFont);
            setSize(new Dimension(300, 20));
            setText(value);
            return this;
        }

    }

}
