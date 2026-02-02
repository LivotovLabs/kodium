plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

kotlin {
    jvmToolchain(17)

    androidTarget { publishLibraryVariants("release") }
    jvm()
    js { browser() }
    wasmJs { browser() }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    linuxX64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(kotlincrypto.hash.sha2)
            implementation(kotlincrypto.macs.hmac.sha2)
            implementation(kotlincrypto.random.crypto.rand)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

    }

    //https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations["main"].compileTaskProvider.configure {
            compilerOptions {
                freeCompilerArgs.add("-Xexport-kdoc")
            }
        }
    }

}

android {
    namespace = "io.kodium"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }
}

mavenPublishing {
    publishToMavenCentral()
    coordinates("eu.livotov.labs", "kodium", "0.0.1")

    pom {
        name = "Kodium"
        description = "A pure Kotlin implementation of the NaCl (libsodium) cryptography suite. Zero native dependencies, 100% Kotlin Multiplatform (KMP). Secure your Android, iOS, JVM, and JS apps instantly."
        url = "https://github.com/LivotovLabs/kodium"

        licenses {
            license {
                name = "Apache-2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = "LivotovLabs"
                name = "Livotov Labs Ltd."
                email = "labs@livotov.eu"
            }
        }

        scm {
            url = "https://github.com/LivotovLabs/kodium"
        }
    }

    if (project.hasProperty("signing.keyId") &&
        project.hasProperty("signing.password") &&
        (project.hasProperty("signing.secretKeyRingFile") || project.hasProperty("signing.key"))) {
        signAllPublications()
    }
}
