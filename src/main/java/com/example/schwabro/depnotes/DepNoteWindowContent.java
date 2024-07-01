package com.example.schwabro.depnotes;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DepNoteWindowContent implements ToolWindowContent {
    private final JPanel contentPanel = new JPanel();
    private final JLabel noDNLabel = new JLabel("It seems no properties files were modified",
            AllIcons.General.Information, SwingConstants.LEADING);

    public DepNoteWindowContent(ToolWindow toolWindow) {
        contentPanel.setLayout(new BorderLayout(0, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        contentPanel.add(createInfoNoDNPanel(), BorderLayout.PAGE_START);
        contentPanel.add(createControlsPanel(toolWindow, null), BorderLayout.CENTER);
    }

    @NotNull
    private JPanel createInfoNoDNPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.add(noDNLabel);
        return infoPanel;
    }

    @NotNull
    public static JPanel createControlsPanel(ToolWindow toolWindow, ActionListener actionListener) {
        JPanel controlsPanel = new JPanel();
        JButton addDeployerNote = new JButton("Add Deployer Note");
        Project openProject = ProjectManager.getInstance().getOpenProjects()[0];
        addDeployerNote.addActionListener(actionListener == null ? e -> addDN(openProject, toolWindow) : actionListener);
        controlsPanel.add(addDeployerNote);
        JButton hideToolWindowButton = new JButton("Hide");
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        controlsPanel.add(hideToolWindowButton);
        return controlsPanel;
    }

    private static void addDN(Project project, ToolWindow toolWindow) {
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
        FileSystemTreeImpl fileSystemTree = new FileSystemTreeImpl(project, fileChooserDescriptor);
        AddDepNoteAction.createNewFile(fileSystemTree, project);
        toolWindow.hide();
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}
