package com.example.schwabro.terminology

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class GlossaryUploader : StartupActivity {
    override fun runActivity(project: Project) {
        AllTermsMap.firstUploadGlossary();
    }
}