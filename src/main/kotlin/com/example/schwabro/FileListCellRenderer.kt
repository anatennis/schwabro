package com.example.schwabro

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.nio.file.Paths
import javax.swing.*

class FileListCellRenderer : ListCellRenderer<FileListItem> {
    private lateinit var fileList: JBList<FileListItem>
    lateinit var items: List<FileListItem>
    override fun getListCellRendererComponent(
        list: JList<out FileListItem>,
        value: FileListItem,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)

        val fileNameLabel = JLabel(value.fileName)
        val scrollPaneFile = JBScrollPane(fileNameLabel)
        scrollPaneFile.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        scrollPaneFile.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER
//        fileNameLabel.preferredSize = Dimension(100, 20)
        panel.add(scrollPaneFile, BorderLayout.WEST)

        val stringLabel = JLabel(value.longString)
        val scrollPane = JBScrollPane(stringLabel)
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER
        panel.add(scrollPane, BorderLayout.EAST)

        if (isSelected) {
            panel.background = list.selectionBackground
            panel.foreground = list.selectionForeground
        } else {
            panel.background = list.background
            panel.foreground = list.foreground
        }

        return panel
    }

    fun createFileList(project: Project): JComponent {
        val listModel = DefaultListModel<FileListItem>()
        fileList = JBList(listModel)

        fileList.cellRenderer = FileListCellRenderer()

        fileList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    val selectedItem = fileList.selectedValue
                    openFileInEditor(project, selectedItem.fileName)
                }
            }
        })

        val scrollPane = JBScrollPane(fileList)
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED

        return scrollPane
    }

    fun openFileInEditor(project: Project, filePath: String) {
        val virtualFile =
            LocalFileSystem.getInstance().findFileByPath(Paths.get(project.basePath!!).resolve(filePath).toString())
        if (virtualFile != null) {
            ApplicationManager.getApplication().invokeLater {
                FileEditorManager.getInstance(project).openFile(virtualFile, true)
            }
        } else {
            JOptionPane.showMessageDialog(null, "File not found: $filePath", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }

    fun setItemsInList(newItems: List<FileListItem>) {
        (fileList.model as DefaultListModel<FileListItem>).clear()
        newItems.forEach { (fileList.model as DefaultListModel<FileListItem>).addElement(it) }
        items = newItems.toList()
    }
}

