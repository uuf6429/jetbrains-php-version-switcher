package me.sciberras.christian.pvs

import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class BackgroundPostProjectStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        if (!this.pluginAlreadyEnabled(project) && this.projectDependsOnMultiplePhpVersions()) {
            this.showEnablePluginRecommendation(project)
        }
    }

    private fun pluginAlreadyEnabled(project: Project): Boolean {
        return project.service<ProjectSettings>().state.enabled
    }

    private fun projectDependsOnMultiplePhpVersions(): Boolean {
        // TODO Return true if:
        //      - there must be multiple composer.json files (that are not in a vendor/ dir)
        //      - at least 2 composer files have a different minimum php language level
        return true
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
