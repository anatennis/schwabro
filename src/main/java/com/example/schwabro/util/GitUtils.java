package com.example.schwabro.util;

import com.example.schwabro.WorkTimerWidget;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.BranchChangeListener;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.wm.WindowManager;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;

public class GitUtils {
    public static final String NO_GIT_INFO = "Main Task";

    public static String getCurrentGitBranch(@NotNull Project project) {
        try {
            GitRepositoryManager gitRepositoryManager = GitRepositoryManager.getInstance(project);
            GitRepository gitRepository = gitRepositoryManager
                    .getRepositoryForRootQuick(ProjectLevelVcsManager.getInstance(project).getAllVersionedRoots()[0]);

            if (gitRepository != null && gitRepository.getCurrentBranch() != null) {
                return gitRepository.getCurrentBranch().getName();
            }
        } catch (Exception e) {
            //do nothing
        }
        return NO_GIT_INFO;
    }

    public static String getTicketName(Project project) {
        String branchName = getCurrentGitBranch(project);
        int start = branchName.indexOf("TOSX-");
        if (start < 0)
            return branchName;
        branchName = branchName.substring(start);
        return branchName.substring(0, branchName.indexOf("-", 5));
    }

    public static class GitCheckoutListener implements ProjectComponent {
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
}
