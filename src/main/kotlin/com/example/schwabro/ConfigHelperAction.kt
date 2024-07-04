package com.example.schwabro

import com.example.schwabro.Constants.MODULES_ALL
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.example.schwabro.Utils.Companion.lookForDefaultConfigsDir
import com.example.schwabro.settings.SchwaBroSettings
import com.intellij.openapi.project.modules

class ConfigHelperAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val dialogModel = DialogModel(
            getModulesWithDefaults(project),
            SchwaBroSettings.getInstance().state.directories.keys,
            mutableSetOf()
        )
        val dialog = ConfigHelperDialog(project, dialogModel)
        dialog.show()
    }

    private fun getModulesWithDefaults(project: Project): List<String> {
        val modules = project.modules

        val res = mutableListOf<String>()
        modules.drop(1).forEach { module ->
            val moduleRootManager = ModuleRootManager.getInstance(module)
            val contentRoots = moduleRootManager.contentRoots

            for (root in contentRoots) {
                if (lookForDefaultConfigsDir(root) != null) {
                    res.add(module.name)
                    break
                }
            }
        }
        return res.apply {
            add(0, MODULES_ALL)
        }
    }
}
