package com.example.schwabro.depnotes;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ChangesUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

final class DepNoteToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        createDNWindow(project, toolWindow, getChanges(project));
        setToolWindowWidth(project, toolWindow.getId());
    }

    public static Map<String, Change> getChanges(Project project) {
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        Collection<Change> allChanges = changeListManager.getAllChanges();
        return allChanges.stream()
                .filter(c -> {
                    FilePath filePath = ChangesUtil.getFilePath(c);
                    return filePath.getName().contains(".properties");
                })
                .collect(Collectors.toMap(c -> ChangesUtil.getFilePath(c).getName(), c -> c));
    }

    public static void createDNWindow(Project project, ToolWindow toolWindow, Map<String, Change> changes) {
        DepNoteWindowContent toolWindowContent = changes == null ?
                new DepNoteWindowContent(toolWindow) :
                new DepNoteWindowContent(toolWindow, changes);
        Content content = ContentFactory.getInstance().createContent(
                toolWindowContent.getContentPanel(), "Modified .properties Files", false);
        toolWindow.getContentManager().addContent(content);
        setToolWindowWidth(project, toolWindow.getId());
    }

    static void setToolWindowWidth(Project project, String toolWindowID) {
        ToolWindowManager instance = ToolWindowManager.getInstance(project);
        ToolWindowEx tw = (ToolWindowEx) instance.getToolWindow(toolWindowID);
        int width = tw.getComponent().getWidth();
        tw.stretchWidth(300 - width);
    }

}