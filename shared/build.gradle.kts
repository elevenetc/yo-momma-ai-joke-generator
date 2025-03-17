import java.util.Properties
import org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) localProperties.load(localPropertiesFile.inputStream()) else error("local.properties file not found")
val openAiApiKey = localProperties.getProperty("openai.api.key") ?: error("No openai.api.key found in local.properties")
val openAiProjectId =
    localProperties.getProperty("openai.project.id") ?: error("No openai.project.id found in local.properties")
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
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negitiation)
                implementation(libs.ktor.serialization.json)
            }
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:3.0.0")
        }
    }

    @OptIn(ExperimentalSwiftExportDsl::class)
    swiftExport {
        moduleName = "Shared"
        export(libs.kotlinx.coroutines.core) { moduleName = "kotlinxCoroutinesCore" }
        export(libs.ktor.client.core) { moduleName = "ktorCore" }
        export(libs.ktor.client.content.negitiation) { moduleName = "ktorContentNegotiation" }
        export(libs.ktor.serialization.json) { moduleName = "ktorSerializationJson" }
        export(libs.ktor.client.cio) { moduleName = "ktorClientCio" }
        export(libs.ktor.http) { moduleName = "ktorHttp" }
        export(libs.ktor.events) { moduleName = "ktorEvents" }
        export(libs.ktor.io) { moduleName = "ktorIo" }
    }
}

