package me.sciberras.christian.pvs

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "PhpVersionSwitcher", storages = [Storage("pvs.xml")])
class ProjectSettings(
    private val project: Project,
) : SimplePersistentStateComponent<ProjectSettingsState>(ProjectSettingsState())

class ProjectSettingsState : BaseState() {
    var enabled: TriState by enum<TriState>(TriState.UNDEFINED)
}

enum class TriState {
    ENABLED,
    DISABLED,
    UNDEFINED,
}
