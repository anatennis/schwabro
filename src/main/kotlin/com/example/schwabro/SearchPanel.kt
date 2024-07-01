package com.example.schwabro

import com.example.schwabro.Constants.DEFAULT_CONFIG_SCOPE
import com.intellij.find.FindManager
import com.intellij.find.FindModel
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import com.intellij.ui.util.maximumHeight
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JPanel

class SearchPanel(
    private val project: Project,
    private val onSearchAction: Runnable
) : JPanel(BorderLayout()) {

    init {
        val searchField = JBTextField()
        add(searchField, BorderLayout.NORTH)
        val searchButton = JButton(Constants.SEARCH)
        searchButton.apply {
            maximumHeight = 20
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
        add(searchButton, BorderLayout.PAGE_END)
    }

}