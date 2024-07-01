package com.example.schwabro.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "SchwaBroSettings", storages = [Storage("SchwaBroSettings.xml")])
@Service
class SchwaBroSettings : PersistentStateComponent<SchwaBroSettings.State> {
    private var state = State()

    data class State(
        var directories: MutableList<String> = mutableListOf()
    )

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    fun setDirectories(directories: List<String>) {
        state.directories.clear()
        state.directories.addAll(directories)
    }

    companion object {
        fun getInstance(): SchwaBroSettings {
            return ApplicationManager.getApplication().getService(SchwaBroSettings::class.java)
        }
    }
}