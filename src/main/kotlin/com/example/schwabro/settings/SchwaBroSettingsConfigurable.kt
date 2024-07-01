package com.example.schwabro.settings

import com.example.schwabro.Constants
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.maximumHeight
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class SchwaBroSettingsConfigurable : Configurable {
    private val settings = SchwaBroSettings.getInstance()
    private lateinit var mainPanel: JPanel
    private lateinit var directoryField: JTextField
    private lateinit var directoriesPanel: JPanel

    private var modifiedDirectories: MutableList<String> = mutableListOf()

    override fun createComponent(): JComponent {
        mainPanel = JPanel(BorderLayout())

        val inputPanel = JPanel(BorderLayout())
        directoryField = JTextField()
        val addButton = JButton(Constants.ADD).apply {
            addActionListener { this@SchwaBroSettingsConfigurable.addDirectory() }
        }
        inputPanel.add(directoryField, BorderLayout.CENTER)
        inputPanel.add(addButton, BorderLayout.EAST)

        directoriesPanel = JPanel()
        directoriesPanel.layout = BoxLayout(directoriesPanel, BoxLayout.Y_AXIS)
        val scrollPane = JBScrollPane(directoriesPanel).apply {
            preferredSize = Dimension(400, 200)
        }

        mainPanel.add(inputPanel, BorderLayout.NORTH)
        mainPanel.add(scrollPane, BorderLayout.CENTER)

        modifiedDirectories = settings.state.directories.toMutableList()
        updateUI()

        return mainPanel
    }

    private fun addDirectory() {
        val directory = directoryField.text
        if (directory.isNotEmpty()) {
            modifiedDirectories.add(directory)
            directoryField.text = ""
            updateUI()
        }
    }

    private fun removeDirectory(directory: String) {
        modifiedDirectories.remove(directory)
        updateUI()
    }

    private fun updateUI() {
        directoriesPanel.removeAll()
        modifiedDirectories.forEach { directory ->
            val directoryPanel = JPanel(BorderLayout()).apply {
                maximumHeight = 40
            }
            val directoryLabel = JLabel(directory).apply {
                border = BorderFactory.createEmptyBorder(0, 20, 0, 0)
            }
            val deleteButton = JButton(Constants.DELETE).apply {
                addActionListener { removeDirectory(directory) }
            }
            directoryPanel.add(directoryLabel, BorderLayout.CENTER)
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
    }

    override fun getDisplayName(): String = Constants.SCHWABRO_SETTINGS
}