package com.example.schwabro;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
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
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AddDepNoteListener implements DocumentListener {
    private final Set<String> changedFiles = new HashSet<>();

    public AddDepNoteListener() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(() -> {
            if (!changedFiles.isEmpty())
                addPopUpConfirmation();
        }, 1, TimeUnit.MINUTES); //todo change
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(this);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        DocumentImpl document = (DocumentImpl) event.getSource();
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (file == null)
            return;
        if (file.getName().contains(".properties")) {
            changedFiles.add(file.getName());
            updateToolWindowContent();
        }
    }

    private void addPopUpConfirmation() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length == 0) {
            return;
        }
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(openProjects[0]);
        JBPopupFactory.getInstance()
                .createConfirmation(
                        "Would You Like to Add Deployer Note for Changed Property?",
                        () -> {
                            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
                            FileSystemTreeImpl fileSystemTree = new FileSystemTreeImpl(openProjects[0], fileChooserDescriptor);
                            AddDepNoteAction.createNewFile(fileSystemTree, openProjects[0]);
                        }, 0)
                .show(RelativePoint.getNorthWestOf(statusBar.getComponent()));
    }

    public void updateToolWindowContent() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length == 0) {
            return;
        }
        Project project = openProjects[0];
        ToolWindow depNote = ToolWindowManager.getInstance(project).getToolWindow("DepNote");
        depNote.setIcon(AllIcons.General.Warning);
        ContentManager contentManager = depNote.getContentManager();
        //update content
        Content content = contentManager.getContents()[0];
        contentManager.removeContent(content, false);
        Content buttonContent = ContentFactory.SERVICE.getInstance().createContent(
                createControlsPanel(depNote), "Modified .properties Files", false);
        contentManager.addContent(buttonContent, 0);
    }

    private JPanel createControlsPanel(ToolWindow toolWindow) {
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel jLabel1 = new JLabel("These .properties files were changed");
        jLabel1.setSize(250, 20);
        controlsPanel.add(jLabel1);
        controlsPanel.add(Box.createHorizontalStrut(200));
        controlsPanel.add(Box.createVerticalStrut(10));

        DefaultListModel<String> dlm = JBList.createDefaultListModel(changedFiles);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());

        JBList<String> list = new JBList<>(dlm);
        //list.setFixedCellWidth(300);
        jPanel.add(list);
        list.setCellRenderer(new DNRenderer());

        controlsPanel.add(jPanel);
        controlsPanel.add(Box.createHorizontalStrut(200));
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(DepNoteWindowContent.createControlsPanel(toolWindow));
        return controlsPanel;
    }

    public static class DNRenderer extends JLabel implements ListCellRenderer<String> {
        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            setIcon(AllIcons.FileTypes.Yaml);
            setHorizontalAlignment(SwingConstants.CENTER);
            setSize(new Dimension(200, 20));
            setText(value);
            return this;
        }
    }

}
