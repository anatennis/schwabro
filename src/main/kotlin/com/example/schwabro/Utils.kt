package com.example.schwabro

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.scope.packageSet.NamedScope
import com.intellij.psi.search.scope.packageSet.NamedScopeManager
import com.intellij.psi.search.scope.packageSet.PackageSetFactory
import javax.swing.JTextField

class Utils {
    companion object {
        fun JTextField.clear() {
            text = ""
        }

        fun lookForDefaultConfigsDir(root: VirtualFile): VirtualFile? {
            if (!root.isDirectory)
                return null
            var file: VirtualFile? = null
            for (child in root.children) {
                if (child.isDirectory && child.name == Constants.DEFAULT_CONFIGS) {
                    file = child
                    break
                } else {
                    val found = lookForDefaultConfigsDir(child)
                    if (found != null) {
                        file = found
                        break
                    }
                }
            }
            return file
        }

        private fun removeDefaultConfigScope(scopeManager: NamedScopeManager) {
            val scopeToRemove = scopeManager.scopes.find { it.scopeId == Constants.DEFAULT_CONFIG_SCOPE }
            if (scopeToRemove == null)
                return
            val scopes = scopeManager.editableScopes.toMutableList()
            scopes.remove(scopeToRemove)
            scopeManager.scopes = scopes.toTypedArray()
        }

        fun createDefaultConfigScope(customDirs: Set<String>): NamedScope {
            val customDirsStr = customDirs.joinToString(separator = "||") { "file:$it/*.properties" }.run {
                if (isNotEmpty()) "||$this" else this
            }
            val customPackageSet = PackageSetFactory.getInstance()
                .compile("file:*default_configs/*.properties||file:ode_configs/*.properties||file:system/*.properties$customDirsStr")
            return NamedScope(Constants.DEFAULT_CONFIG_SCOPE, AllIcons.Ide.LocalScope, customPackageSet)
        }

        fun setDefaultConfigScope(project: Project, namedScope: NamedScope) {
            val scopeManager = NamedScopeManager.getInstance(project)
            if (scopeManager.scopes.map { it.scopeId }.contains(Constants.DEFAULT_CONFIG_SCOPE)) {
                removeDefaultConfigScope(scopeManager)
            }
            scopeManager.addScope(namedScope)
        }
    }
}