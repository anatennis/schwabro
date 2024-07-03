package com.example.schwabro.settings

import com.example.schwabro.Constants
import com.example.schwabro.Utils
import com.example.schwabro.Utils.Companion.clear
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.psi.search.scope.packageSet.NamedScopeManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.maximumHeight
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class SchwaBroSettingsConfigurable(private val project: Project) : Configurable {
    private val settings = SchwaBroSettings.getInstance()
    private lateinit var mainPanel: JPanel
    private lateinit var pathField: JTextField
    private lateinit var dirNameField: JTextField
    private lateinit var directoriesPanel: JPanel

    private var modifiedDirectories: MutableMap<String, String> = mutableMapOf()

    override fun createComponent(): JComponent {
        mainPanel = JPanel(BorderLayout())

        val inputPanel = JPanel(BorderLayout())
        pathField = JTextField()
        dirNameField = JTextField()
        val addButton = JButton(Constants.ADD).apply {
            addActionListener { this@SchwaBroSettingsConfigurable.addDirectory() }
        }
        inputPanel.add(dirNameField, BorderLayout.WEST)
        inputPanel.add(pathField, BorderLayout.CENTER)
        inputPanel.add(addButton, BorderLayout.EAST)

        directoriesPanel = JPanel()
        directoriesPanel.layout = BoxLayout(directoriesPanel, BoxLayout.Y_AXIS)
        val scrollPane = JBScrollPane(directoriesPanel).apply {
            preferredSize = Dimension(400, 200)
        }

        mainPanel.add(inputPanel, BorderLayout.NORTH)
        mainPanel.add(scrollPane, BorderLayout.CENTER)

        modifiedDirectories = settings.state.directories.toMutableMap()
        updateUI()

        return mainPanel
    }

    private fun addDirectory() {
        val path = pathField.text
        val dirName = dirNameField.text
        if (dirName.isNotEmpty() && path.isNotEmpty()) {
            modifiedDirectories[dirName] = path
            pathField.clear()
            dirNameField.clear()
            updateUI()
        }
    }

    private fun removeDirectory(dirName: String) {
        modifiedDirectories.remove(dirName)
        updateUI()
    }

    private fun updateUI() {
        directoriesPanel.removeAll()
        modifiedDirectories.forEach { dir ->
            val directoryPanel = JPanel(BorderLayout()).apply {
                maximumHeight = 40
            }
            val nameLabel = JLabel(dir.key).apply {
                border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
            }
            val pathLabel = JLabel(dir.value).apply {
                border = BorderFactory.createEmptyBorder(0, 20, 0, 0)
            }
            val deleteButton = JButton(Constants.DELETE).apply {
                addActionListener { removeDirectory(dir.key) }
            }
            directoryPanel.add(nameLabel, BorderLayout.WEST)
            directoryPanel.add(pathLabel, BorderLayout.CENTER)
            directoryPanel.add(deleteButton, BorderLayout.EAST)
            directoriesPanel.add(directoryPanel)
        }
        directoriesPanel.revalidate()
        directoriesPanel.repaint()
    }

    override fun isModified(): Boolean {
        return modifiedDirectories != settings.state.directories
    }

    override fun apply() {
        settings.setDirectories(modifiedDirectories)
        val defaultConfigScope = Utils.createDefaultConfigScope(project, settings.state.directories.values.toSet())
        Utils.setDefaultConfigScope(project, defaultConfigScope)
    }

    override fun getDisplayName(): String = Constants.SCHWABRO_SETTINGS
}