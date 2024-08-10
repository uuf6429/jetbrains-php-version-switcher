package me.sciberras.christian.pvs

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toNullableProperty
import com.intellij.ui.dsl.listCellRenderer.textListCellRenderer
import javax.swing.JComponent

class ProjectSettingsConfigurable(
    private val project: Project
) : Configurable {
    private val stateService = project.service<ProjectSettings>()
    private var initialState = this.stateService.state
    private var currentState = ProjectSettingsState()

    override fun getDisplayName(): String {
        return "My Project Settings 2" // TODO this seems to be unused
    }

    override fun createComponent(): JComponent {
        return panel {
            row("Refresh language level") {
                comboBox(
                    TriState.entries,
                    textListCellRenderer {
                        when (it) {
                            // TODO for localisation, use ApplicationBundle.message("combobox.insert.imports.ask")
                            TriState.ENABLED -> "Yes"
                            TriState.DISABLED -> "No"
                            else -> ""
                        }
                    }
                ).bindItem(currentState::enabled.toNullableProperty())
            }
        }
    }

    override fun isModified(): Boolean {
        // TODO forced to true, so that the "Apply" button is clickable so we can see trigger the method below
        return true; //this.currentState != this.initialState
    }

    override fun apply() {
        // FIXME at this point, this.currentState.enabled should be what the user has selected, but it is never so
        this.stateService.loadState(this.currentState)
        this.initialState.copyFrom(this.currentState)
    }

    override fun reset() {
        this.currentState.copyFrom(this.initialState)
        super.reset()
    }
}
