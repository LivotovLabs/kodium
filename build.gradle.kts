plugins {
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.maven.publish).apply(false)
    alias(libs.plugins.android.application).apply(false)
    id("org.jetbrains.dokka") version "1.9.20" apply false
}
