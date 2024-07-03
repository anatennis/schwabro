package com.example.schwabro

import com.example.schwabro.settings.SchwaBroSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.search.scope.packageSet.NamedScope
import com.intellij.psi.search.scope.packageSet.NamedScopeManager

class ProjectListener : ProjectActivity {
    override suspend fun execute(project: Project) {
        val customDirs = SchwaBroSettings.getInstance().state.directories.values.toSet()
        val defaultConfigScope = Utils.createDefaultConfigScope(project, customDirs)
        Utils.setDefaultConfigScope(project, defaultConfigScope)
    }
}