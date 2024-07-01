package com.example.schwabro.depnotes;

import com.example.schwabro.util.GitUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vcs.BranchChangeListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AddDepNoteListener implements DocumentListener, FileDocumentManagerListener, BranchChangeListener {
    private final Map<String, Set<DocumentImpl>> changedFiles = new LinkedHashMap<>(); //todo remove

    private Project project;
    private String currentBranchName;

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
        if (file.getName().contains(".properties") && file instanceof VirtualFileImpl && !wasAlreadyChanged(file)) {
            Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
            project = openProjects[0];
            if (currentBranchName == null) {
                openProjects[0].getMessageBus().connect().
                        subscribe(BranchChangeListener.VCS_BRANCH_CHANGED, this);
                currentBranchName = GitUtils.getCurrentGitBranch(openProjects[0]);
            }
            changedFiles.compute(currentBranchName, (k, v) -> {
                if (v == null)
                    v = new HashSet<>();
                v.add(document);
                return v;
            });
            updateToolWindowContent();
        }
    }

    private boolean wasAlreadyChanged(VirtualFile file) {
        if (currentBranchName == null || changedFiles.get(currentBranchName).isEmpty()) {
            return false;
        }
        Set<DocumentImpl> files = changedFiles.get(currentBranchName);
        return files.stream().anyMatch(f -> FileDocumentManager.getInstance().getFile(f).getName().equals(file.getName()));
    }

    @Override
    public void branchWillChange(@NotNull String branchName) {}

    @Override
    public void branchHasChanged(@NotNull String branchName) {
        if (changedFiles.get(currentBranchName) == null || changedFiles.get(currentBranchName).isEmpty()) {
            currentBranchName = branchName;
            return;
        }
        if (changedFiles.get(branchName) != null && !changedFiles.get(branchName).isEmpty())
            updateToolWindowContent();
        else {
            ToolWindow depNote = ToolWindowManager.getInstance(project).getToolWindow("DepNote");
            ContentManager contentManager = depNote.getContentManager();
            Content content = contentManager.getContents()[0];
            contentManager.removeContent(content, false);
            DepNoteToolWindowFactory.createNotModifiedWindow(project, depNote);
            changedFiles.put(branchName, new HashSet<>());
        }
        currentBranchName = branchName;
    }

    public void updateToolWindowContent() {
        ToolWindow depNote = ToolWindowManager.getInstance(project).getToolWindow("DepNote");
        depNote.setIcon(AllIcons.General.Warning);
        ContentManager contentManager = depNote.getContentManager();

        Content content = contentManager.getContents()[0];
        contentManager.removeContent(content, false);
        Content buttonContent = ContentFactory.getInstance().createContent(
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

        DefaultListModel<String> dlm = JBList.createDefaultListModel(
                changedFiles.get(currentBranchName).stream()
                        .map(d -> FileDocumentManager.getInstance().getFile(d).getName())
                        .collect(Collectors.toList())
        );
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());

        JBList<String> list = new JBList<>(dlm);
        jPanel.add(list);
        list.setCellRenderer(new DNRenderer());

        controlsPanel.add(jPanel);
        controlsPanel.add(Box.createHorizontalStrut(200));
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(DepNoteWindowContent.createControlsPanel(toolWindow,
                new AddPropertiesChangesAction(changedFiles.get(currentBranchName), project)));
        return controlsPanel;
    }

    private void addPopUpConfirmation() {
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

    public static class AddPropertiesChangesAction implements ActionListener {
        public final Set<DocumentImpl> changedFiles;
        public final Project project;
        private final FileSystemTreeImpl fileSystemTree;

        public AddPropertiesChangesAction(Set<DocumentImpl> changedFiles, Project project) {
            this.changedFiles = changedFiles;
            this.project = project;
            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
            fileSystemTree = new FileSystemTreeImpl(project, fileChooserDescriptor);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AddDepNoteAction.createNewFile(fileSystemTree, project, changedFiles);
        }
    }
}
