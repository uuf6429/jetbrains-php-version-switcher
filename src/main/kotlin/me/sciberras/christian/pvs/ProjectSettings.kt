package me.sciberras.christian.pvs

import com.intellij.openapi.components.*

@Service(Service.Level.PROJECT)
@State(name = "PhpVersionSwitcher", storages = [Storage("pvs.xml")])
class ProjectSettings : SimplePersistentStateComponent<ProjectSettingsState>(ProjectSettingsState())

class ProjectSettingsState : BaseState() {
    var enabled: Boolean by property(false)
}
