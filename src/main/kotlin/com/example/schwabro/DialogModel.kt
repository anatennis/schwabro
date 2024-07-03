package com.example.schwabro

data class DialogModel(
    val modules: List<String>,
    val userDefinedModules: Set<String>,
    val profiles: MutableSet<String>
)