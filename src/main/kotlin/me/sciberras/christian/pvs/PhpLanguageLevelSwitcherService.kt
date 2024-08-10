package me.sciberras.christian.pvs

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEventMulticasterEx
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
internal class PhpLanguageLevelSwitcherService(private val project: Project) : Disposable {
    init {
        val eventMulticaster = EditorFactory.getInstance().eventMulticaster as? EditorEventMulticasterEx
        eventMulticaster?.addFocusChangeListener(FocusChangeListener(), this)
    }

    override fun dispose() {}
}
