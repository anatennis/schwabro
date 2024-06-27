package com.example.schwabro.util;

import com.example.schwabro.DepNoteWindowContent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

final class DepNoteToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DepNoteWindowContent toolWindowContent = new DepNoteWindowContent(toolWindow);
        Content content = ContentFactory.SERVICE.getInstance().createContent(
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