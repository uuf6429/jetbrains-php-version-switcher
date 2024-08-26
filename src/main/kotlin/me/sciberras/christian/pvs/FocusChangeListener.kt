package me.sciberras.christian.pvs

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

        val composerFile = phpFile.findNearestFile("composer.json")
        if (composerFile == lastComposerFile) {
            return
        }

        lastComposerFile = composerFile

        if (composerFile == null) {
            thisLogger().debug("Composer file not found in any parent directory of $phpFile")
            return
        }

        val phpVersion = composerFile.findPhpVersion(project)
        if (phpVersion == lastPhpVersion) {
            return
        }

        lastPhpVersion = phpVersion

        if (phpVersion == null) {
            thisLogger().debug("PHP not set as a requirement in $composerFile")
            return
        }

        setPhpVersion(project, phpVersion)
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
