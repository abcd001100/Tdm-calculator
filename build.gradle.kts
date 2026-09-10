// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Kotlin support is built into AGP 9.0+, so a separate
    // org.jetbrains.kotlin.android plugin is neither needed nor allowed.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
