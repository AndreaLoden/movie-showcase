import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kmp.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization.plugin)
    jacoco
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        commonMain.dependencies {
            // put your multiplatform dependencies here
            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(compose.foundation)

            implementation(libs.androidx.lifecycle.viewmodel)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serializationJson)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.andrea.imdbshowcase"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("connectedAndroidTest") // Android unit tests

    executionData.setFrom(
        fileTree(buildDir).apply {
            include(
                "jacoco/testDebugUnitTest.exec", // Android
                "outputs/code_coverage/debugAndroidTest/connected/*.ec"
            )
        }
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    // Source directories for Android + common code
    sourceDirectories.setFrom(files("src/commonMain/kotlin"))
    classDirectories.setFrom(
        fileTree("build/tmp/kotlin-classes/debug") {
            exclude("**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*")
        }
    )
}
