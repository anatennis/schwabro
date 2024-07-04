package com.example.schwabro

import com.example.schwabro.settings.SchwaBroSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class ProjectListener : ProjectActivity {
    override suspend fun execute(project: Project) {
        val customDirs = SchwaBroSettings.getInstance().state.directories.values.toSet()
        val defaultConfigScope = Utils.createDefaultConfigScope(customDirs)
        Utils.setDefaultConfigScope(project, defaultConfigScope)
    }
}