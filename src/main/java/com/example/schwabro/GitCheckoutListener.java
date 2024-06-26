package com.example.schwabro;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.BranchChangeListener;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

public class GitCheckoutListener implements ProjectComponent {
    private final Project project;

    public GitCheckoutListener(Project project) {
        this.project = project;
    }
    @Override
    public void projectOpened() {
        project.getMessageBus().connect().subscribe(BranchChangeListener.VCS_BRANCH_CHANGED, new MyBranchChangeListener(project));
    }
    @Override
    public void projectClosed() {
        project.getMessageBus().dispose();
    }

    private static class MyBranchChangeListener implements BranchChangeListener {
        private final Project project;

        public MyBranchChangeListener(Project project) {
            this.project = project;
        }

        @Override
        public void branchWillChange(@NotNull String branchName) {}

        @Override
        public void branchHasChanged(@NotNull String branchName) {
            WorkTimerWidget widget = (WorkTimerWidget) WindowManager.getInstance().getStatusBar(project).getWidget("Timer");
            if (widget != null)
                widget.resetTimer(branchName);
        }
    }
}
