package com.example.schwabro

import com.intellij.openapi.vfs.VirtualFile

class Utils {
    companion object {
        fun lookForDefaultConfigsDir(root: VirtualFile): VirtualFile? {
            if (root.isDirectory) {
                for (child in root.children) {
                    if (child.isDirectory && child.name == "default_configs") {
                        return child
                    } else {
                        val found = lookForDefaultConfigsDir(child)
                        if (found != null) return found
                    }
                }
            }
            return null
        }
    }
}