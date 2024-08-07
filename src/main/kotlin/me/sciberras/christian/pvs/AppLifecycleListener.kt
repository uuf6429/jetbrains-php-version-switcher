package me.sciberras.christian.pvs

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.util.Disposer


class AppLifecycleListener : AppLifecycleListener {
    init {
        val disposable: Disposable = Disposer.newDisposable()
        EditorFactory.getInstance().addEditorFactoryListener(EditorFactoryListener(), disposable)
    }
}
