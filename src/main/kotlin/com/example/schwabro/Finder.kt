package com.example.schwabro

import com.example.schwabro.Constants.DEFAULT_CONFIG_SCOPE
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.DefaultSearchScopeProviders
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.scope.packageSet.NamedScopeManager

class Finder(val project: Project) {
    companion object {
        const val PROPERTIES_EXT = "properties"
    }

    private val propertySearcher = PropertySearcher(project)

    fun performSearch(profiles: Set<String>, propertyName: String, directory: String): List<FileListItem> {
        var files: Collection<VirtualFile> = getFilesInDefaultsScope()
        files = filterFilesByDirectory(files, directory)
        files = filterFilesByProfiles(files, profiles)
        return lookForPropertyInFiles(files, propertyName)
    }

    private fun filterFilesByDirectory(files: Collection<VirtualFile>, directory: String): Collection<VirtualFile> {
        if (directory == Constants.MODULES_ALL) return files
        return files.filter { file ->
            file.path.contains(directory)
        }
    }


    private fun getFilesInDefaultsScope(): Collection<VirtualFile> {
        val searchScope = DefaultSearchScopeProviders.wrapNamedScope(
            project,
            NamedScopeManager.getScope(project, DEFAULT_CONFIG_SCOPE)!!,
            false
        )
        return FilenameIndex.getAllFilesByExt(project, PROPERTIES_EXT, searchScope)
    }

    private fun filterFilesByProfiles(files: Collection<VirtualFile>, profiles: Set<String>): Collection<VirtualFile> {
        if (profiles.isEmpty()) {
            return files
        }
        return files.filter { file ->
            val profilePattern = profiles.joinToString("|", "(", ")")
            val regex = Regex(profilePattern)

            val foundProfiles = regex.findAll(file.name).map { it.value }.toSet()

            foundProfiles.ifEmpty { return@filter false }
            foundProfiles.all { it in profiles }
        }
    }

    private fun lookForPropertyInFiles(files: Collection<VirtualFile>, propertyName: String): List<FileListItem> {
        return propertySearcher.searchProperties(files, propertyName)
    }
}