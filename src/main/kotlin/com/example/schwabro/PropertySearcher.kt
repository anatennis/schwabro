package com.example.schwabro

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import java.nio.file.Paths

class PropertySearcher(private val project: Project) {

    fun searchProperties(files: Collection<VirtualFile>, propertyName: String): List<FileListItem> {
        val results = mutableListOf<FileListItem>()
        val psiManager = PsiManager.getInstance(project)

        files.forEach { file ->
            val psiFile = psiManager.findFile(file)
            if (psiFile != null) {
                results.add(searchInFile(psiFile, propertyName))
            } else {
                results.add(FileListItem(file.path, "<not_specified>", -1))
            }
        }

        return results
    }

    private fun searchInFile(file: PsiFile, propertyName: String): FileListItem {
        val lines = file.text.lines()
        lines.forEachIndexed { index, line ->
            if (line.trimStart().startsWith(prefix = "$propertyName=", ignoreCase = true)) {
                val value = line.split("=")[1].ifEmpty { "<empty>" }
                return FileListItem(getRelativePath(file), value, index)
            }
        }
        return FileListItem(getRelativePath(file), "<not_specified>", -1)
    }

    private fun getRelativePath(file: PsiFile) =
        Paths.get(project.basePath!!).relativize(Paths.get(file.virtualFile.path)).toString()
}
