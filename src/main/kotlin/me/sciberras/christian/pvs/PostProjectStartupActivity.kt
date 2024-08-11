package me.sciberras.christian.pvs

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

internal class PostProjectStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        project.getService(PhpLanguageLevelSwitcherService::class.java)
    }
}
