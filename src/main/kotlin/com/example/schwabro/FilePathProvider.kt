package com.example.schwabro

import com.example.schwabro.settings.SchwaBroSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.roots.ModuleRootManager
import java.nio.file.Paths

class FilePathProvider(val project: Project) {

    fun getFilePath(moduleName: String): String {
        if (moduleName == Constants.MODULES_ALL)
            return Constants.MODULES_ALL
        val userDefinedDirs = SchwaBroSettings.getInstance().state.directories
        return userDefinedDirs[moduleName] ?: run {
            val module = project.modules.find { it.name == moduleName }!!
            val modulePath = ModuleRootManager.getInstance(module).contentRoots[0].toNioPath()
            Paths.get(project.basePath!!).relativize(modulePath).toString()
        }
    }
}