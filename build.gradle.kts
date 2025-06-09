plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.kmp.compose).apply(false)
    alias(libs.plugins.dev.mokkery).apply(false)
    alias(libs.plugins.ktlint).apply(false)
    jacoco
}
