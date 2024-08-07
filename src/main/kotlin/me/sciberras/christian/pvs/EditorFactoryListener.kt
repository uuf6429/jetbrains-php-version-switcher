package me.sciberras.christian.pvs

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.jetbrains.php.lang.PhpFileType

class EditorFactoryListener : EditorFactoryListener, Disposable {
    override fun editorCreated(event: EditorFactoryEvent) {
        val phpFile = FileDocumentManager.getInstance().getFile(event.editor.document)
        if (phpFile == null || phpFile.fileType != PhpFileType.INSTANCE) {
            return
        }

        val listener = FocusChangeListener(phpFile)
        (event.editor as? EditorEx)?.addFocusListener(listener, this)
    }

    override fun dispose() {
    }
}
