package com.example.schwabro

import com.example.schwabro.settings.SchwaBroSettings
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path

class Utils {
    companion object {
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

        fun getDirectoryByName(projectPath: Path): VirtualFile? {
            val settings = SchwaBroSettings.getInstance()
            val directoryPath = settings.state.directories[0]

            return LocalFileSystem.getInstance().findFileByNioFile(projectPath.resolve(directoryPath))
        }
    }
}