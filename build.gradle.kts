import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "me.sciberras.christian"
version = "1.0.4"

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("org.jetbrains.intellij.platform") version "2.0.0"
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

val ideType = IntelliJPlatformType.fromCode(System.getenv("IDE_TYPE") ?: "IU")
val ideVersion = System.getenv("IDE_VERSION") ?: "2023.2"
val ideBuildVersion = System.getenv("IDE_BUILD_VERSION") ?: ideVersion
val splitVersion = ideVersion.split('.')
val buildVersion = (splitVersion[0].toInt() - 2000) * 10 + splitVersion.getOrElse(1) { "0" }.toInt()
val pluginsVersion = mapOf(
    233 to "233.11799.232",
    242 to "242.20224.427",
    243 to "243.16718.32",
)[buildVersion]

dependencies {
    intellijPlatform {
        if (ideType === IntelliJPlatformType.PhpStorm) {
            phpstorm(ideBuildVersion)
            bundledPlugin("com.jetbrains.php")
        }
        if (ideType === IntelliJPlatformType.IntellijIdeaUltimate) {
            intellijIdeaUltimate(ideBuildVersion)
            plugin("com.jetbrains.php:$pluginsVersion")
            if (buildVersion > 242) {
                plugin("com.intellij.modules.json:$pluginsVersion")
            }
        }

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
}

intellijPlatform {
    pluginVerification {
        ides {
            ide(ideType, ideBuildVersion)
        }
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("243.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
