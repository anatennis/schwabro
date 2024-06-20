package com.example.schwabro.util;

import com.example.schwabro.AddDepNote;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class ButtonContent implements ToolWindowContent {
    private final JPanel contentPanel = new JPanel();
    private final JLabel noDNLabel = new JLabel();

    public ButtonContent(ToolWindow toolWindow) {
        contentPanel.setLayout(new BorderLayout(0, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        contentPanel.add(createInfoNoDNPanel(), BorderLayout.PAGE_START);
        contentPanel.add(createControlsPanel(toolWindow), BorderLayout.CENTER);
    }

    @NotNull
    private JPanel createInfoNoDNPanel() {
        JPanel infoPanel = new JPanel();
        noDNLabel.setText("It seems no properties files were modified");
        infoPanel.add(noDNLabel);
        return infoPanel;
    }

    @NotNull
    public static JPanel createControlsPanel(ToolWindow toolWindow) {
        JPanel controlsPanel = new JPanel();
        JButton addDeployerNote = new JButton("Add Deployer Note");
        Project openProject = ProjectManager.getInstance().getOpenProjects()[0];
        addDeployerNote.addActionListener(e -> addDN(openProject, toolWindow));
        controlsPanel.add(addDeployerNote);
        JButton hideToolWindowButton = new JButton("Hide");
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        controlsPanel.add(hideToolWindowButton);
        return controlsPanel;
    }

    private static void addDN(Project project, ToolWindow toolWindow) {
        VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
        FileSystemTreeImpl fileSystemTree = new FileSystemTreeImpl(project, fileChooserDescriptor);
        AddDepNote.createNewFile(contentRoots[0], fileSystemTree, project);
        toolWindow.hide();
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}
