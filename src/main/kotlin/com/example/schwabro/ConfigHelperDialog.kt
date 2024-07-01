package com.example.schwabro

import com.example.schwabro.Constants.DEFAULT_CONFIG_SCOPE
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.search.*
import com.intellij.psi.search.scope.packageSet.NamedScopeManager
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.util.maximumHeight
import com.jetbrains.rd.util.ConcurrentHashMap
import io.ktor.util.*
import java.awt.BorderLayout
import java.awt.Dimension
import java.nio.file.Paths
import javax.swing.*


class ConfigHelperDialog(
    private val project: Project,
    private val dialogModel: DialogModel
) : DialogWrapper(project) {

    companion object {
        private val PROPERTY_FILE_NAME_PATTERN = Regex("[a-zA-Z0-9]*-(.*?)\\.properties")
    }

    private lateinit var modulesComboBox: JComboBox<String>
    private lateinit var propertyNameTextField: JTextField
    private lateinit var propertyValueTextField: JTextField
    private lateinit var profilesPanel: JPanel
    private lateinit var profilesAncestorPanel: JPanel
    private lateinit var fileListRenderer: FileListCellRenderer

    init {
        title = Constants.HELPER_DIALOG_TITLE
        init()
    }

    override fun createCenterPanel(): JComponent {
        dialogModel.profiles.clear()
        dialogModel.profiles.addAll(readProfilesFromModule(dialogModel.modules[0]))
        return JPanel(BorderLayout()).apply {
            preferredSize = Dimension(900, 600)
            add(JBTabbedPane().apply {
                addTab("Modify", createModifyTab())
                addTab("Search", SearchPanel(project) { this@ConfigHelperDialog.close(0, false) })
            }, BorderLayout.CENTER)
        }
    }

    override fun doOKAction() {
        replace(propertyValueTextField.text)
        super.doOKAction()
    }

    private fun readProfilesFromModule(moduleName: String): Set<String> {
        val moduleManager = ModuleManager.getInstance(project)
        val uniqueProfiles = mutableSetOf<String>()

        when (moduleName) {
            Constants.MODULES_ALL -> {
                for (module in moduleManager.modules) {
                    val moduleRootManager = ModuleRootManager.getInstance(module)
                    for (root in moduleRootManager.contentRoots) {
                        val defaultConfigsDir = Utils.lookForDefaultConfigsDir(root)
                        if (defaultConfigsDir != null) {
                            uniqueProfiles.addAll(retrieveConfigProfilesFromDir(defaultConfigsDir))
                        }
                    }
                }
            }
            //todo dopilit'
            Constants.TOP_LEVEL_MODULE -> {
                val topProfiles =
                    retrieveConfigProfilesFromDir(Utils.getDirectoryByName(Paths.get(project.basePath!!))!!)
                uniqueProfiles.addAll(topProfiles)
            }

            else -> {
                val module = moduleManager.findModuleByName(moduleName) ?: return emptySet()
                val moduleRootManager = ModuleRootManager.getInstance(module)
                for (root in moduleRootManager.contentRoots) {
                    val defaultConfigsDir = Utils.lookForDefaultConfigsDir(root)
                    if (defaultConfigsDir != null) {
                        uniqueProfiles.addAll(retrieveConfigProfilesFromDir(defaultConfigsDir))
                    }
                }
            }
        }
        return uniqueProfiles
    }

    private fun retrieveConfigProfilesFromDir(directory: VirtualFile): Set<String> {
        val profiles = mutableSetOf<String>()
        for (file in directory.children) {
            if (file.isDirectory) continue
            val matchResult = PROPERTY_FILE_NAME_PATTERN.find(file.name)
            if (matchResult != null) {
                val profilesString = matchResult.groupValues[1]
                profiles.addAll(profilesString.split("."))
            }
        }
        return profiles
    }

    private fun replace(newValue: String) {
        fileListRenderer.items.forEach { item ->
            val tfn = Paths.get(project.basePath ?: "").resolve(item.fileName).toString()
            val virtualFile = LocalFileSystem.getInstance().findFileByPath(tfn)
            if (virtualFile != null) {
                val document = FileDocumentManager.getInstance().getDocument(virtualFile)
                if (document != null) {
                    ApplicationManager.getApplication().runWriteAction {
                        WriteCommandAction.runWriteCommandAction(project) {
                            replaceInDocument(document, item, newValue)
                        }
                    }
                }
            }
        }
    }

    private fun replaceInDocument(document: Document, item: FileListItem, newValue: String) {
        val lines = document.text.split("\n").toMutableList()
        val pn = propertyNameTextField.text

        if (item.lineNumber in lines.indices) {
            lines[item.lineNumber] = "${pn.toUpperCasePreservingASCIIRules()}=$newValue"
        } else {
            lines.add("${pn.toUpperCasePreservingASCIIRules()}=$newValue")
        }


        val newText = lines.joinToString("\n")
        document.setText(newText)
        FileDocumentManager.getInstance().saveDocument(document)
    }

    private fun search(selectedItem: String): List<FileListItem> {
        val results = ConcurrentHashMap<String, FileListItem>()

        if (propertyNameTextField.text.isEmpty()) return listOf()

        ApplicationManager.getApplication().runReadAction {
            val searchHelper = PsiSearchHelper.getInstance(project)
            val processor = TextOccurenceProcessor { element, off ->


                val module = ModuleUtilCore.findModuleForPsiElement(element) ?: return@TextOccurenceProcessor true
                if (module.name != selectedItem && selectedItem != Constants.MODULES_ALL) return@TextOccurenceProcessor true
                val containingFile = element.containingFile

                var hasProfiles = false
                val selectedProfiles = getSelectedProfiles()
                if (selectedProfiles.isNotEmpty()) {
                    selectedProfiles.forEach {
                        hasProfiles = hasProfiles || (containingFile.name.contains(it))
                    }
                } else {
                    hasProfiles = true
                }
                if (!hasProfiles) return@TextOccurenceProcessor true
                if (!isOffsetAtLineStart(containingFile, off)) return@TextOccurenceProcessor true
                val filePath = getRelativePath(containingFile.virtualFile)
                val value = element.text.split("=")[1].ifEmpty { "<empty>" }
                results[filePath] = FileListItem(filePath, value, getLineNumber(containingFile, off))

                true
            }

            val searchScope = DefaultSearchScopeProviders.wrapNamedScope(
                project,
                NamedScopeManager.getScope(project, DEFAULT_CONFIG_SCOPE)!!,
                false
            )
            searchHelper.processElementsWithWord(
                processor,
                searchScope,
                "${propertyNameTextField.text}=",
                UsageSearchContext.ANY,
                false
            )
        }

        return results.map { it.value }
    }

    private fun isOffsetAtLineStart(psiFile: PsiFile, offset: Int): Boolean {
        val document: Document = psiFile.viewProvider.document ?: return false
        val lineNumber = getLineNumber(psiFile, offset)
        val lineStartOffset = document.getLineStartOffset(lineNumber)
        return lineStartOffset == offset
    }

    private fun getLineNumber(psiFile: PsiFile, offset: Int) = psiFile.viewProvider.document.getLineNumber(offset)

    private fun getRelativePath(file: VirtualFile): String {
        val projectPath = project.basePath ?: return file.path
        return file.path.removePrefix("$projectPath/")
    }

    private fun createModifyTab(): JComponent {
        val panel = JPanel(BorderLayout())

        val leftPanel = JPanel()
        leftPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        leftPanel.layout = BoxLayout(leftPanel, BoxLayout.Y_AXIS)

        val modulesPanel = createModulesPanel()
        modulesPanel.maximumHeight = 10
        leftPanel.add(modulesPanel)
        leftPanel.add(Box.createVerticalStrut(10))
        val propertyNamePanel = createPropertyNamePanel()
        propertyNamePanel.maximumHeight = 10
        leftPanel.add(propertyNamePanel)
        leftPanel.add(Box.createVerticalStrut(10))
        val propertyValuePanel = createPropertyValuePanel()
        propertyValuePanel.maximumHeight = 10
        leftPanel.add(propertyValuePanel)
        leftPanel.add(Box.createVerticalStrut(10))
        val btnsPanel = JPanel(BorderLayout())

        val searchBtn = JButton(Constants.SEARCH).apply {
            addActionListener {
                fileListRenderer.setItemsInList(search(modulesComboBox.selectedItem?.toString() ?: "All"))
            }
            maximumHeight = 10
        }
        val apply = JButton(Constants.APPLY).apply {
            addActionListener {
                replace(propertyValueTextField.text)
            }
            maximumHeight = 10
        }
        btnsPanel.add(searchBtn, BorderLayout.LINE_START)
        btnsPanel.add(apply, BorderLayout.LINE_END)
        btnsPanel.maximumHeight = 20
        leftPanel.add(btnsPanel)
        leftPanel.add(Box.createVerticalStrut(10))
        fileListRenderer = FileListCellRenderer()
        leftPanel.add(fileListRenderer.createFileList(project))

        panel.add(leftPanel, BorderLayout.CENTER)

        profilesAncestorPanel = JPanel()
        profilesAncestorPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        profilesAncestorPanel.layout = BoxLayout(profilesAncestorPanel, BoxLayout.Y_AXIS)

        profilesPanel = createProfilesPanel(dialogModel.profiles)

        profilesAncestorPanel.add(Box.createVerticalStrut(10))
        profilesAncestorPanel.add(profilesPanel)

        panel.add(profilesAncestorPanel, BorderLayout.EAST)

        return panel
    }

    private fun createProfilesPanel(strings: Set<String>): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(Constants.PROFILES),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )

        val checkBoxPanel = JPanel()
        checkBoxPanel.layout = BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS)
        panel.add(checkBoxPanel, BorderLayout.CENTER)

        val addButton = JButton(Constants.ADD)
        val textField = JTextField(20)
        strings.forEach {
            checkBoxPanel.add(JCheckBox(it))
        }
        addButton.addActionListener {
            val itemName = textField.text.trim()
            if (itemName.isNotEmpty()) {
                checkBoxPanel.add(JCheckBox(itemName))
                checkBoxPanel.revalidate()
                checkBoxPanel.repaint()
                textField.text = ""
            }
        }
        panel.add(addButton, BorderLayout.SOUTH)
        panel.add(textField, BorderLayout.NORTH)

        return panel
    }

    private fun createModulesPanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createTitledBorder(Constants.MODULES)

        modulesComboBox = ComboBox(dialogModel.modules.toTypedArray())
        modulesComboBox.addActionListener {
            val selectedItem = modulesComboBox.selectedItem as String
            dialogModel.profiles.clear()
            dialogModel.profiles.addAll(readProfilesFromModule(selectedItem))
            profilesAncestorPanel.remove(profilesPanel)
            profilesPanel = createProfilesPanel(dialogModel.profiles)
            profilesAncestorPanel.add(profilesPanel)
        }
        panel.add(modulesComboBox, BorderLayout.CENTER)

        return panel
    }

    private fun createPropertyNamePanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createTitledBorder(Constants.PROPERTY_NAME)

        propertyNameTextField = JTextField(20)
        panel.add(propertyNameTextField, BorderLayout.CENTER)

        return panel
    }

    private fun createPropertyValuePanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createTitledBorder(Constants.PROPERTY_VALUE)

        propertyValueTextField = JTextField(20)
        panel.add(propertyValueTextField, BorderLayout.CENTER)

        return panel
    }

    private fun getSelectedProfiles(): List<String> {
        return (profilesPanel.components[0] as JPanel).components.filter { (it as JCheckBox).isSelected }
            .map { (it as JCheckBox).text }
    }
}
