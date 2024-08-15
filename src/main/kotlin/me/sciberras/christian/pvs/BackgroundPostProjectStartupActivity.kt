package me.sciberras.christian.pvs

import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.php.config.PhpLanguageLevel

class BackgroundPostProjectStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        ApplicationManager.getApplication().runReadAction {
            if (!this.pluginAlreadyEnabled(project) && this.projectDependsOnMultiplePhpVersions(project)) {
                this.showEnablePluginRecommendation(project)
            }
        }
    }

    private fun pluginAlreadyEnabled(project: Project): Boolean {
        return project.service<ProjectSettings>().state.enabled
    }

    private fun projectDependsOnMultiplePhpVersions(project: Project): Boolean {
        val vendorPathRegex = Regex("""[/\\]vendor[/\\]""")
        var lastPhpVersion: PhpLanguageLevel? = null
        var foundDifferentVersions = false

        FilenameIndex.processFilesByName(
            "composer.json",
            true,
            GlobalSearchScope.projectScope(project)
        ) { file: VirtualFile ->
            // if the composer file is in some vendor directory, skip it
            if (vendorPathRegex.containsMatchIn(file.path)) {
                return@processFilesByName true
            }

            val currentPhpVersion: PhpLanguageLevel? = file.findPhpVersion(project)

            // if there is no php version, skip this file
            if (currentPhpVersion == null) {
                return@processFilesByName true
            }

            // if we haven't found a php version yet, take this one and continue
            if (lastPhpVersion == null) {
                lastPhpVersion = currentPhpVersion
                return@processFilesByName true
            }

            // if the current php version does not match a previous one, stop here
            if (lastPhpVersion?.name != currentPhpVersion.name) {
                foundDifferentVersions = true
                return@processFilesByName false
            }

            return@processFilesByName true
        }

        return foundDifferentVersions
    }

    private fun showEnablePluginRecommendation(project: Project) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("PHP Version Switcher")
            .createNotification(
                Bundle.message("notifications.enable.title"),
                Bundle.message("notifications.enable.content"),
                NotificationType.INFORMATION
            )
            .setSuggestionType(true)
            .addActions(
                setOf(
                    NotificationAction.createSimpleExpiring(Bundle.message("notifications.enable.enable")) {
                        project.service<ProjectSettings>().state.enabled = true
                    },
                    NotificationAction.createSimpleExpiring(Bundle.message("notifications.enable.ignore")) {
                    },
                )
            )
            .notify(project);
    }
}
