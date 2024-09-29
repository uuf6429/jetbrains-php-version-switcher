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
val pluginVersion = mapOf(
    "2023" to "232.8660.185",
    "2024" to "242.20224.427",
    "242" to "242.20224.427",
    "243" to "243.16718.32",
)[ideVersion.split('.').first()]

dependencies {
    intellijPlatform {
        if (ideType === IntelliJPlatformType.PhpStorm) {
            phpstorm(ideVersion)
            bundledPlugin("com.jetbrains.php")
        }
        if (ideType === IntelliJPlatformType.IntellijIdeaUltimate) {
            intellijIdeaUltimate(ideVersion)
            plugin("com.jetbrains.php:$pluginVersion")
        }

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
}

intellijPlatform {
    pluginVerification {
        ides {
            ide(ideType, ideVersion)
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
