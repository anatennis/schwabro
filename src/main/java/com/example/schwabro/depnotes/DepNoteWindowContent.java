package com.example.schwabro.depnotes;

import com.example.schwabro.util.DepNoteUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.UIBundle;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DepNoteWindowContent implements ToolWindowContent {
    private final JPanel contentPanel = new JPanel();
    private static final JLabel noDNLabel = new JLabel("It seems no properties files were modified",
            AllIcons.General.Information, SwingConstants.LEADING);
    private static final JLabel changedLabel = new JLabel("These .properties files were changed",
            AllIcons.FileTypes.Yaml, SwingConstants.LEADING);

    public DepNoteWindowContent(ToolWindow toolWindow) {
        this(toolWindow, null);
        toolWindow.setIcon(AllIcons.General.Note);
    }

    public DepNoteWindowContent(ToolWindow toolWindow, Map<String, Change> changes) {
        boolean hasChanges = changes != null && !changes.isEmpty();
        toolWindow.setIcon(hasChanges ? AllIcons.General.Warning : AllIcons.General.Note);
        contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
        contentPanel.add(createInfoDNPanel(hasChanges), BorderLayout.PAGE_START);

        contentPanel.add(Box.createHorizontalStrut(200));
        contentPanel.add(Box.createVerticalStrut(10));
        Set<ChangeInfo> changeInfos = null;
        if (hasChanges) {
            contentPanel.add(createFilesTable(
                    changes.values().stream().map(Change::getVirtualFile).collect(Collectors.toSet()),
                    checkBoxes, toolWindow.getProject()
            ));
            changeInfos = changes.entrySet().stream()
                    .map(e -> new ChangeInfo(e.getValue().getVirtualFile(), e.getKey(), e.getValue(), null))
                    .collect(Collectors.toSet());
        }
        contentPanel.add(createControlsPanel(toolWindow,
                        hasChanges ? new AddPropertiesChangesAction(changeInfos, checkBoxes, toolWindow) : null),
                BorderLayout.CENTER
        );
    }

    private static JPanel createInfoDNPanel(Boolean hasChanges) {
        JPanel infoPanel = new JPanel();
        infoPanel.add(hasChanges ? changedLabel : noDNLabel);
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

    public static void updateToolWindowContent(Map<String, Set<ChangeInfo>> changedFiles, String currentBranchName, Project project) {
        ToolWindow depNote = ToolWindowManager.getInstance(project).getToolWindow("DepNote");
        depNote.setIcon(AllIcons.General.Warning);
        ContentManager contentManager = depNote.getContentManager();

        Content content = contentManager.getContents()[0];
        contentManager.removeContent(content, false);
        Content buttonContent = ContentFactory.getInstance().createContent(
                createControlsPanel(changedFiles, currentBranchName, depNote),
                "Modified .properties Files", false);
        contentManager.addContent(buttonContent, 0);
    }

    private static JPanel createControlsPanel(Map<String, Set<ChangeInfo>> changedFiles, String currentBranchName,
                                              ToolWindow toolWindow) {
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlsPanel.add(changedLabel);
        controlsPanel.add(Box.createHorizontalStrut(200));
        controlsPanel.add(Box.createVerticalStrut(10));

        Set<VirtualFile> filesNames = changedFiles.get(currentBranchName).stream()
                .map(ChangeInfo::getFile)
                .collect(Collectors.toSet());
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
        controlsPanel.add(createFilesTable(filesNames, checkBoxes, toolWindow.getProject()));
        controlsPanel.add(Box.createHorizontalStrut(200));
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(DepNoteWindowContent.createControlsPanel(toolWindow,
                new AddPropertiesChangesAction(changedFiles.get(currentBranchName), checkBoxes, toolWindow)));
        return controlsPanel;
    }

    public static JPanel createFilesTable(Set<VirtualFile> filesNames, List<JCheckBox> checkboxes, Project project) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        for (VirtualFile file : filesNames) {
            JCheckBox jCheckBox = new JCheckBox(file.getName());
            jCheckBox.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        if (project != null) {
                            FileEditorManager.getInstance(project).openFile(file, true);
                        }
                    }
                }
            });
            checkboxes.add(jCheckBox);
            jPanel.add(jCheckBox);
        }
        return jPanel;
    }

    private static void addPopUpConfirmation(Project project) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        JBPopupFactory.getInstance()
                .createConfirmation(
                        "Would You Like to Add Deployer Note for Changed Property?",
                        () -> {
                            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
                            FileSystemTreeImpl fileSystemTree = new FileSystemTreeImpl(project, fileChooserDescriptor);
                            AddDepNoteAction.createNewFile(fileSystemTree, project);
                        }, 0)
                .show(RelativePoint.getNorthWestOf(statusBar.getComponent()));
    }

    public static class AddPropertiesChangesAction implements ActionListener {
        public final Set<ChangeInfo> changedFiles;
        public final List<JCheckBox> checkBoxes;
        public final Project project;
        private final ToolWindow toolWindow;
        private final FileSystemTreeImpl fileSystemTree;

        public AddPropertiesChangesAction(Set<ChangeInfo> changedFiles, List<JCheckBox> checkBoxes, ToolWindow toolWindow) {
            this.changedFiles = changedFiles;
            this.project = toolWindow.getProject();
            this.toolWindow = toolWindow;
            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
            fileSystemTree = new FileSystemTreeImpl(project, fileChooserDescriptor);
            this.checkBoxes = checkBoxes;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Set<String>  checkedFiles = new HashSet<>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    checkedFiles.add(checkBox.getText());
                }
            }
            if (!checkedFiles.isEmpty()) {
                Collection<VirtualFile> newFile = DepNoteUtils.createNewFile(fileSystemTree, project, changedFiles, checkedFiles);
                toolWindow.hide();
                if (newFile != null && !newFile.isEmpty()) {
                    FileEditorManager.getInstance(project).openFile(newFile.iterator().next(), true);
                }
            } else {
                Messages.showMessageDialog(changedFiles == null ? "No .properties files were changed" : "Please, choose .properties file",
                        UIBundle.message("error.dialog.title"), Messages.getErrorIcon());
            }
        }
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}
