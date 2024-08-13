package me.sciberras.christian.pvs

import com.intellij.json.psi.JsonStringLiteral
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findPsiFile
import com.jetbrains.php.config.PhpLanguageLevel
import com.jetbrains.php.config.composer.LanguageLevelComposerParser
import com.jetbrains.php.lang.inspections.PhpSwitchComposerLanguageLevelQuickFix

internal val PHP_FILE_EXTS = arrayOf("php", "phtm", "phtml")

fun VirtualFile.findPhpVersion(project: Project): PhpLanguageLevel? {
    try {
        return when {
            this.name == "composer.json" -> {
                LanguageLevelComposerParser
                    .getMinRequiredLanguageLevel(
                        this.findComposerPhpProperty(project),
                        PhpLanguageLevel.PHP820
                    )
            }

            this.name == "composer.lock" -> {
                TODO("See: https://github.com/uuf6429/jetbrains-php-version-switcher/issues/4")
            }

            PHP_FILE_EXTS.contains(this.extension) -> {
                TODO("See: https://github.com/uuf6429/jetbrains-php-version-switcher/issues/5")
            }

            else -> throw RuntimeException("")
        }
    } catch (ex: Throwable) {
        thisLogger().error(ex)
        return null
    }
}

internal fun VirtualFile.findComposerPhpProperty(project: Project): String? {
    val psiFile = this.findPsiFile(project)
    val jsonProp = PhpSwitchComposerLanguageLevelQuickFix.findPhpProperty(psiFile)?.value as? JsonStringLiteral
    return jsonProp?.value
}
