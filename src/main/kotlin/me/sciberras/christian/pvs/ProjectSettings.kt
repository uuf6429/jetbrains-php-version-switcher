package me.sciberras.christian.pvs

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "PVS.Settings", storages = [Storage(StoragePathMacros.PRODUCT_WORKSPACE_FILE)])
class ProjectSettings(
    private val project: Project,
) : SimplePersistentStateComponent<ProjectSettingsState>(ProjectSettingsState())

class ProjectSettingsState : BaseState() {
    var enabled: TriState = TriState.UNDEFINED
}

enum class TriState {
    ENABLED,
    DISABLED,
    UNDEFINED,
}
