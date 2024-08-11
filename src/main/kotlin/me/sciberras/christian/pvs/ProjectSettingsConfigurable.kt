package me.sciberras.christian.pvs

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class ProjectSettingsConfigurable(
    project: Project
) : Configurable {
    private val stateService = project.service<ProjectSettings>()
    private var enabledField: Cell<JBCheckBox>? = null

    override fun getDisplayName(): String {
        return Bundle.message("settings.title")
    }

    override fun createComponent(): JComponent {
        return panel {
            row {
                enabledField = checkBox(Bundle.message("settings.enable.label"))
            }
        }
    }

    override fun isModified(): Boolean {
        return this.enabledField!!.component.isSelected != stateService.state.enabled
    }

    override fun apply() {
        stateService.state.enabled = this.enabledField!!.component.isSelected
    }

    override fun reset() {
        this.enabledField!!.component.isSelected = stateService.state.enabled

        super.reset()
    }
}
