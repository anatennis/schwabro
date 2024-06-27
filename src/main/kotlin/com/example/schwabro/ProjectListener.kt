package com.example.schwabro

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.search.scope.packageSet.NamedScope
import com.intellij.psi.search.scope.packageSet.NamedScopeManager
import com.intellij.psi.search.scope.packageSet.PackageSetFactory

class ProjectListener  : ProjectActivity {
    override suspend fun execute(project: Project) {
        createAndSetScope(project)
    }

    private fun createAndSetScope(project: Project) {
        val customPackageSet = PackageSetFactory.getInstance()
            .compile("file:*default_configs/*.properties||file:ode_configs/*.properties||file:system//*.properties")
        val namedScope = NamedScope("Default configs", AllIcons.Ide.LocalScope, customPackageSet)
        if (!NamedScopeManager.getInstance(project).scopes.map { it.scopeId }.contains("Default configs")) {
            NamedScopeManager.getInstance(project).addScope(namedScope)
        }
    }
}