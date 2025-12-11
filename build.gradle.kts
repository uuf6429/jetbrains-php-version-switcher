import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "me.sciberras.christian"
version = "1.25.3"

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
    id("org.jetbrains.intellij.platform") version "2.5.0"
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
val platformVersion = (splitVersion[0].toInt() - 2000) * 10 + splitVersion.getOrElse(1) { "0" }.toInt()
val pluginsVersion = mapOf(
    232 to "232.8660.185",
    233 to "233.11799.232",
    242 to "242.20224.427",
    243 to "243.16718.32",
    251 to "251.23774.318",
    252 to "252.13776.59",
    253 to "253.28294.51",
)[platformVersion] ?: throw NullPointerException("Plugins version for IDE $ideVersion has not been configured")
val pluginJvmTarget = mapOf(
    232 to JvmTarget.JVM_17,
    233 to JvmTarget.JVM_17,
    242 to JvmTarget.JVM_17,
    243 to JvmTarget.JVM_17,
    251 to JvmTarget.JVM_21,
    252 to JvmTarget.JVM_21,
    253 to JvmTarget.JVM_21,
)[platformVersion] ?: throw NullPointerException("Java version for $platformVersion has not been configured")

dependencies {
    intellijPlatform {
        if (ideType === IntelliJPlatformType.PhpStorm) {
            phpstorm(ideBuildVersion)
            bundledPlugin("com.jetbrains.php")
        }
        if (ideType === IntelliJPlatformType.IntellijIdeaUltimate) {
            intellijIdeaUltimate(ideBuildVersion)
            plugin("com.jetbrains.php:$pluginsVersion")
            // 2024.3 extracted JSON support into a plugin
            if (platformVersion >= 243) {
                plugin("com.intellij.modules.json:$pluginsVersion")
            }
        }

        pluginVerifier()
        zipSigner()
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
        sourceCompatibility = JvmTarget.JVM_17.target
        targetCompatibility = pluginJvmTarget.target
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(pluginJvmTarget)
        }
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("253.*")
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
