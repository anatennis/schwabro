package com.example.schwabro

import com.example.schwabro.Constants.DEFAULT_CONFIG_SCOPE
import com.intellij.find.FindManager
import com.intellij.find.FindModel
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel

class SearchPanel(
    private val project: Project,
    private val onSearchAction: Runnable
) : JPanel(BorderLayout()) {

    init {
        val innerPanel = JPanel(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        }
        val searchField = JBTextField()
        val searchButton = JButton(Constants.SEARCH).apply {
            addActionListener {
                val searchText = searchField.text
                if (searchText.isNotEmpty()) {
                    onSearchAction.run()
                    val findModel = FindModel().apply {
                        isCustomScope = true
                        customScopeName = DEFAULT_CONFIG_SCOPE
                        stringToFind = searchText
                        isCaseSensitive = false
                        isRegularExpressions = false
                    }
                    FindManager.getInstance(project).showFindDialog(findModel) {}
                }
            }
        }

        innerPanel.add(searchField, BorderLayout.CENTER)
        innerPanel.add(searchButton, BorderLayout.EAST)

        add(innerPanel, BorderLayout.PAGE_START)
    }

}