package me.sciberras.christian.pvs

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.listCellRenderer.textListCellRenderer
import javax.swing.JComponent

class ProjectSettingsConfigurable(
    project: Project
) : Configurable {
    private val stateService = project.service<ProjectSettings>()
    private var enabledField: Cell<ComboBox<TriState>>? = null

    override fun getDisplayName(): String {
        return "My Project Settings 2" // TODO this seems to be unused
    }

    override fun createComponent(): JComponent {
        return panel {
            row("Refresh language level") {
                enabledField = comboBox(
                    TriState.entries,
                    textListCellRenderer {
                        when (it) {
                            // TODO for localisation, use ApplicationBundle.message("combobox.insert.imports.ask")
                            TriState.ENABLED -> "Yes"
                            TriState.DISABLED -> "No"
                            else -> ""
                        }
                    }
                )
            }
        }
    }

    override fun isModified(): Boolean {
        return this.enabledField!!.component.selectedItem as TriState? != stateService.state.enabled
    }

    override fun apply() {
        stateService.state.enabled = this.enabledField!!.component.selectedItem as TriState? ?: TriState.UNDEFINED
    }

    override fun reset() {
        this.enabledField!!.component.selectedItem = stateService.state.enabled

        super.reset()
    }
}
