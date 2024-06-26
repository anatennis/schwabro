package com.example.schwabro.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
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
}
