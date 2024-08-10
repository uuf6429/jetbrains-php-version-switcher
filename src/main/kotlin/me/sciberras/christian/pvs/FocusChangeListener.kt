package me.sciberras.christian.pvs

import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.currentClassLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findPsiFile
import com.jetbrains.php.config.PhpLanguageLevel
import com.jetbrains.php.config.PhpProjectConfigurationFacade
import com.jetbrains.php.config.composer.LanguageLevelComposerParser
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.inspections.PhpSwitchComposerLanguageLevelQuickFix

internal class FocusChangeListener : FocusChangeListener {
    override fun focusGained(editor: Editor) {
        if (editor.project?.service<ProjectSettings>()?.state?.enabled != TriState.ENABLED) {
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

        val phpVersion = detectPhpVersion(composerFile, project)
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

    private fun detectPhpVersion(composerFile: VirtualFile, project: Project): PhpLanguageLevel? {
        try {
            val psiFile = composerFile.findPsiFile(project)
            val jsonProp = PhpSwitchComposerLanguageLevelQuickFix.findPhpProperty(psiFile)?.value as JsonStringLiteral
            return normalizePhpVersion(jsonProp.value)
        } catch (ex: Throwable) {
            currentClassLogger().error(ex)
            return null
        }
    }

    private fun normalizePhpVersion(version: String): PhpLanguageLevel? {
        return LanguageLevelComposerParser.getMinRequiredLanguageLevel(version, PhpLanguageLevel.PHP830)
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
