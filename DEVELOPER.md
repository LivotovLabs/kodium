# Developer Guide

This guide provides instructions for developers who want to contribute to Kodium or build it from source.

## Prerequisites

- **JDK 17 or higher** (The project uses JVM Toolchain 17).
- **Gradle** (The project uses the Gradle Wrapper provided in the root).
- **macOS** is required to build and test Apple-specific targets (iOS, macOS).

## Development Workflow

### Building the Project

To build the entire project and ensure all Kotlin Multiplatform targets are compiled:

```bash
./gradlew build
```

To build only the core library module:

```bash
./gradlew :kodium:assemble
```

### Running Tests

Kodium uses a comprehensive test suite across all supported platforms.

#### Run All Tests
To run tests across all configured platforms (JVM, JS, Android, Native):

```bash
./gradlew check
```

#### Target-Specific Tests
You can run tests for specific platforms to save time during development:

- **JVM Only:** `./gradlew :kodium:jvmTest`
- **JS Only:** `./gradlew :kodium:jsTest`
- **iOS Simulator:** `./gradlew :kodium:iosSimulatorArm64Test`
- **Android:** `./gradlew :kodium:connectedDebugAndroidTest` (requires an emulator/device)

### Working with Post-Quantum Cryptography (PQC)
Most PQC-related logic is located in `io.kodium.core.fips203` and `io.kodium.ratchet`. Tests for these features are primarily in `PqcTest.kt` and `PQDoubleRatchetTest.kt`.

---

## Documentation Management

The project documentation is split into a **Manual** (hand-written guides) and an **API Reference** (generated from code).

### 1. Manual Documentation
The manual is located in the `/docs` directory and is formatted for GitBook.
- **Location:** `docs/*.md` and subdirectories.
- **Table of Contents:** Managed via `docs/SUMMARY.md`.

### 2. API Documentation (Dokka)
API documentation is generated from KDoc comments in the source code using [Dokka](https://kotlinlang.org/docs/dokka-introduction.html).

#### Generate API Docs
To generate the Markdown (GFM) API documentation used by GitBook:

```bash
./gradlew :kodium:dokkaGfm
```

### 3. Updating the Docs Website
To update the documentation for publication:

1.  **Edit Manual:** Modify the `.md` files in `docs/` or update `docs/SUMMARY.md` if you added new pages.
2.  **Refresh API Docs:** Run the Dokka task and sync the output:
    ```bash
    ./gradlew :kodium:dokkaGfm
    mkdir -p docs/api
    cp -R kodium/build/dokka/gfm/* docs/api/
    ```
3.  **Commit:** Commit the changes to the `docs/` directory.

---

## Code Style & Standards

- **Pure Kotlin:** Do not introduce any JNI or C-interop dependencies.
- **KDocs:** All public APIs must be documented using KDoc. Use the `@Xexport-kdoc` flag (configured in `build.gradle.kts`) to ensure these comments are exported to Objective-C headers for iOS developers.
- **No-Ignore Tests:** Ensure all tests pass on all targets before submitting a PR.
