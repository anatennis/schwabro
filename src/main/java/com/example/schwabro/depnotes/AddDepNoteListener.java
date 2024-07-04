package com.example.schwabro.depnotes;

import com.example.schwabro.util.GitUtils;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vcs.BranchChangeListener;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.schwabro.depnotes.DepNoteWindowContent.updateToolWindowContent;

public class AddDepNoteListener implements DocumentListener, FileDocumentManagerListener, BranchChangeListener {
    private final Map<String, Set<ChangeInfo>> changedFiles = new LinkedHashMap<>(); //todo remove

    private Project project;
    private String currentBranchName;

    public AddDepNoteListener() {
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
            Map<String, Change> changeMap = DepNoteToolWindowFactory.getChanges(project);
            changedFiles.compute(currentBranchName, (k, v) -> {
                if (v == null)
                    v = new HashSet<>();
                boolean fileWasAdded = false;
                for (Map.Entry<String, Change> changeEntry : changeMap.entrySet()) {
                    if (file.getName().equals(changeEntry.getKey())) {
                        fileWasAdded = true;
                        v.add(new ChangeInfo(file, file.getName(), changeEntry.getValue(), document.getText()));
                    } else {
                        v.add(new ChangeInfo(changeEntry.getValue().getVirtualFile(), changeEntry.getKey(), changeEntry.getValue(), null));
                    }
                }
                if (!fileWasAdded)
                    v.add(new ChangeInfo(file, file.getName(), null, document.getText()));
                return v;
            });
            updateToolWindowContent(changedFiles, currentBranchName, project);
        }
    }

    private boolean wasAlreadyChanged(VirtualFile file) {
        if (currentBranchName == null || changedFiles.get(currentBranchName).isEmpty()) {
            return false;
        }
        Set<ChangeInfo> files = changedFiles.get(currentBranchName);
        Set<ChangeInfo> changedFiles = files.stream().filter(f -> f.getFileName().equals(file.getName()))
                .collect(Collectors.toSet());
        if (changedFiles.isEmpty()) {
            return false;
        }
        for (ChangeInfo changeInfo : changedFiles) {
            changeInfo.setFirstTimeChanged(false);
        }
        return true;
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
            updateToolWindowContent(changedFiles, currentBranchName, project);
        else {
            ToolWindow depNote = ToolWindowManager.getInstance(project).getToolWindow("DepNote");
            ContentManager contentManager = depNote.getContentManager();
            Content content = contentManager.getContents()[0];
            contentManager.removeContent(content, false);
            DepNoteToolWindowFactory.createDNWindow(project, depNote, null);
            changedFiles.put(branchName, new HashSet<>());
        }
        currentBranchName = branchName;
    }

}
