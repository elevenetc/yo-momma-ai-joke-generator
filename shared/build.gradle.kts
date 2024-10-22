import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0-Beta1"
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) localProperties.load(localPropertiesFile.inputStream()) else error("local.properties file not found")
val openAiApiKey = localProperties.getProperty("openai.api.key") ?: error("No openai.api.key found in local.properties")
val openAiProjectId = localProperties.getProperty("openai.project.id") ?: error("No openai.project.id found in local.properties")
val openAiOrgId = localProperties.getProperty("openai.org.id") ?: error("No openai.org.id found in local.properties")

val buildConfigGenerator by tasks.registering(Sync::class) {

    from(
        resources.text.fromString(
            """
          |package com.elevenetc.yomomma.joke.aigen
          |
          |object OpenAiConfig {
          |  const val API_KEY = "$openAiApiKey"
          |  const val PROJECT_ID = "$openAiProjectId"
          |  const val ORG_ID = "$openAiOrgId"
          |}
          |
        """.trimMargin()
        )
    ) {
        rename { "OpenAiConfig.kt" } // set the file name
        into("com/elevenetc/yomomma/joke/aigen/") // change the directory to match the package
    }

    into(layout.buildDirectory.dir("generated-src/kotlin/"))
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(
                buildConfigGenerator.map { it.destinationDir }
            )
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
                implementation("io.ktor:ktor-client-core:3.0.0")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
            }
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:3.0.0")
        }
    }
}

