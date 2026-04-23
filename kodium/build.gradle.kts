plugins {
    id("org.jetbrains.dokka") version "1.9.20"
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

kotlin {
    jvmToolchain(17)

    androidTarget { publishLibraryVariants("release") }
    jvm()
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = "600000ms"
                }
            }
        }
    }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
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
            implementation(kotlincrypto.hash.sha3)
            implementation(kotlincrypto.macs.hmac.sha2)
            implementation(kotlincrypto.random.crypto.rand)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.property)
            implementation(libs.kotest.framework.api)
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotlinx.coroutines.test)
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
    coordinates("eu.livotov.labs", "kodium", "1.0.0-beta-2")

    pom {
        name = "Kodium"
        description = "Pure Kotlin implementation of TweetNaCl and Double Ratchet with hybrid Post-Quantum (PQC) support. Zero native dependencies, 100% KMP. Secure your Android, iOS, JVM, Web, and Native apps with one library."
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
