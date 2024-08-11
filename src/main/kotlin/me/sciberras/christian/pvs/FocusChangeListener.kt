package me.sciberras.christian.pvs

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.php.config.PhpLanguageLevel
import com.jetbrains.php.config.PhpProjectConfigurationFacade
import com.jetbrains.php.lang.PhpFileType

internal class FocusChangeListener : FocusChangeListener {
    override fun focusGained(editor: Editor) {
        if (editor.project?.service<ProjectSettings>()?.state?.enabled != true) {
            return
        }

        val phpFile = FileDocumentManager.getInstance().getFile(editor.document)
        if (phpFile == null || phpFile.fileType != PhpFileType.INSTANCE) {
            return
        }

        val project = editor.project ?: return

        if (phpFile == lastPhpFile) {
            return
        }
        lastPhpFile = phpFile

        val composerFile = detectNearestComposeFile(phpFile)
        if (composerFile == null || composerFile == lastComposerFile) {
            return
        }
        lastComposerFile = composerFile

        val phpVersion = composerFile.findPhpVersion(project)
        if (phpVersion == null || phpVersion == lastPhpVersion) {
            return
        }
        lastPhpVersion = phpVersion

        setPhpVersion(project, phpVersion)
    }

    private fun detectNearestComposeFile(phpFile: VirtualFile): VirtualFile? {
        val segments = phpFile.path.split('/').count();
        for (i in 0..segments) {
            val composerFile = phpFile.findFileByRelativePath("../".repeat(i) + "composer.json")
            if (composerFile !== null) {
                return composerFile
            }
        }
        return null
    }

    private fun setPhpVersion(project: Project, phpVersion: PhpLanguageLevel) {
        PhpProjectConfigurationFacade.getInstance(project).languageLevel = phpVersion
    }

    companion object {
        private var lastPhpFile: VirtualFile? = null
        private var lastComposerFile: VirtualFile? = null
        private var lastPhpVersion: PhpLanguageLevel? = null
    }
}
