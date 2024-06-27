package com.example.schwabro

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.example.schwabro.Utils.Companion.lookForDefaultConfigsDir

class ConfigHelperAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val dialogModel = DialogModel(
            getModulesWithDefaults(project),
            mutableSetOf()
        )
        val dialog = ConfigHelperDialog(project, dialogModel)
        dialog.show()
    }

    private fun getModulesWithDefaults(project: Project): List<String> {
        val moduleManager = ModuleManager.getInstance(project)
        val modules = moduleManager.modules

        val res = mutableListOf<String>()

        for (module in modules) {
            val moduleRootManager = ModuleRootManager.getInstance(module)
            val contentRoots = moduleRootManager.contentRoots

            for (root in contentRoots) {
                if (lookForDefaultConfigsDir(root) != null) {
                    res.add(module.name)
                    break
                }
            }
        }
        return res.apply { add(0, "All") }
    }
}
