package me.sciberras.christian.pvs

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
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
        val project = editor.project ?: return
        val projectSettings = project.service<ProjectSettings>().state
        if (!projectSettings.enabled) return

        val phpFile = FileDocumentManager.getInstance().getFile(editor.document)
        if (phpFile == null || phpFile.fileType != PhpFileType.INSTANCE) return
        if (phpFile == lastPhpFile) return
        lastPhpFile = phpFile

        val (composerFile, phpVersion) = ReadAction.compute<Pair<VirtualFile?, PhpLanguageLevel?>, RuntimeException> {
            val cFile = phpFile.findNearestFile("composer.json")
            val pVersion = cFile?.findPhpVersion(project)
            cFile to pVersion
        }

        ApplicationManager.getApplication().invokeLater({
            if (composerFile == null) {
                thisLogger().debug("Composer file not found in any parent directory of $phpFile")
                return@invokeLater
            }
            if (composerFile == lastComposerFile) return@invokeLater
            lastComposerFile = composerFile

            if (phpVersion == null) {
                thisLogger().debug("PHP not set as a requirement in $composerFile")
                return@invokeLater
            }
            if (phpVersion == lastPhpVersion) return@invokeLater
            lastPhpVersion = phpVersion

            setPhpVersion(project, phpVersion)
        }, ModalityState.defaultModalityState())
    }

    private fun setPhpVersion(project: Project, phpVersion: PhpLanguageLevel) {
        thisLogger().debug("Setting PHP version to $phpVersion...")
        PhpProjectConfigurationFacade.getInstance(project).languageLevel = phpVersion
    }

    companion object {
        private var lastPhpFile: VirtualFile? = null
        private var lastComposerFile: VirtualFile? = null
        private var lastPhpVersion: PhpLanguageLevel? = null
    }
}
