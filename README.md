# Kodium

[![Maven Central](https://img.shields.io/maven-central/v/eu.livotov.labs/kodium)](https://search.maven.org/artifact/eu.livotov.labs/kodium)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-2.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Platform](https://img.shields.io/badge/platform-android%20|%20ios%20|%20jvm%20|%20js%20|%20linux%20|%20macos%20|%20mingw-7f52ff.svg)](http://kotlinlang.org)

**Secure. Portable. Pure Kotlin.**

**Kodium** is a comprehensive, pure Kotlin Multiplatform (KMP) cryptography library. It acts as a faithful port of the renowned **TweetNaCl** C library, providing high-speed, high-security cryptographic primitives without *any* native dependencies.

Write once, encrypt everywhere.

---

## ⚡️ Why Kodium?

*   **Pure Kotlin:** No JNI, no C interop headaches, no complex build scripts for native binaries. Just pure Kotlin code running everywhere.
*   **TweetNaCl Core:** Built on the solid foundation of the TweetNaCl crypto suite, known for its security and simplicity.
*   **Multiplatform Native:** First-class support for Android, iOS, JVM, JavaScript (Browser/Node), Wasm, Linux, macOS, and Windows.
*   **Developer Friendly:** Simple, opinionated APIs for common tasks (Box, SecretBox, Signatures).

## 📝 Release Notes

### v0.0.1
*   Initial implementation of the library.
*   Port of TweetNaCl (Box, SecretBox, Signatures).
*   Base58Check encoding/decoding.
*   Minimal test coverage.

## 📦 Installation

Add Kodium to your common module's dependencies.

**Gradle (Kotlin DSL)**
```kotlin
implementation("eu.livotov.labs:kodium:0.0.1")
```

**Gradle (Groovy)**
```groovy
implementation 'eu.livotov.labs:kodium:0.0.1'
```

**Maven**
```xml
<dependency>
    <groupId>eu.livotov.labs</groupId>
    <artifactId>kodium</artifactId>
    <version>0.0.1</version>
</dependency>
```

## 🛠 Features

*   **Public-Key Cryptography (Box):** Authenticated encryption using Curve25519, XSalsa20, and Poly1305.
*   **Secret-Key Cryptography (SecretBox):** Authenticated encryption using XSalsa20 and Poly1305.
*   **Digital Signatures:** Ed25519 high-speed, high-security signatures.
*   **Key Management:** Secure generation, import, and export of keys (Raw & Base58Check).
*   **Utils:** Robust Base58Check encoding/decoding.

## 🚀 Quick Start

### 1. Asymmetric Encryption (Box)
Securely exchange messages between Alice and Bob.

```kotlin
// 1. Generate keys
val alice = Kodium.generateKeyPair()
val bob = Kodium.generateKeyPair()

// 2. Alice encrypts a message for Bob
val message = "The eagle flies at midnight.".encodeToByteArray()

val encryptedResult = Kodium.encryptToEncodedString(
    mySecretKey = alice,
    theirPublicKey = bob.getPublicKey(),
    data = message
)

// 3. Bob decrypts the message
encryptedResult.onSuccess { cipherText ->
    Kodium.decryptFromEncodedString(
        mySecretKey = bob,
        theirPublicKey = alice.getPublicKey(),
        data = cipherText
    ).onSuccess { decryptedBytes ->
        println("Decrypted: ${decryptedBytes.decodeToString()}")
    }
}
```

### 2. Symmetric Encryption (SecretBox)
Protect data with a shared password/secret.

```kotlin
val password = "CorrectHorseBatteryStaple"
val secretData = "Launch codes: 12345".encodeToByteArray()

// Encrypt
val encryptedResult = Kodium.encryptSymmetricToEncodedString(password, secretData)

// Decrypt
encryptedResult.onSuccess { cipherText ->
    val decryptedResult = Kodium.decryptSymmetricFromEncodedString(password, cipherText)
    println("Restored: ${decryptedResult.getOrThrow().decodeToString()}")
}
```

### 3. Key Export & Import
Easily store keys using Base58Check encoding.

```kotlin
val keyPair = Kodium.generateKeyPair()

// Export Public Key (Safe to share)
val pubKeyString = keyPair.getPublicKey().exportToEncodedString()

// Export Private Key (Encrypted with a password)
val privKeyString = keyPair.exportToEncryptedString("StrongPassword")

// Import later
val restoredKeyPair = KodiumPrivateKey.importFromEncryptedString(
    data = privKeyString.getOrThrow(), 
    password = "StrongPassword"
)
```

## 🛠 Utilities

### Base58 Encoding
Kodium provides efficient Base58 and Base58Check extensions for `ByteArray` and `String`. Base58 is superior to Base64 for many crypto use cases as it eliminates ambiguous characters (0OIl) and is more compact than hexadecimal.

```kotlin
val data = "Kodium Pure Kotlin".encodeToByteArray()

// Standard Base58
val b58 = data.encodeToBase58String()
val decoded = b58.decodeBase58()

// Base58 with Checksum (Standard in Bitcoin/Crypto)
val b58Check = data.encodeToBase58WithChecksum()
val decodedCheck = b58Check.decodeBase58WithChecksum()
```

### Password-Based Key Derivation (PBKDF2)
Derive strong cryptographic keys from user passwords using PBKDF2 with HMAC-SHA256.

```kotlin
val password = "UserPassword123".encodeToByteArray()
val salt = "RandomSalt".encodeToByteArray() // Should be random and at least 16 bytes

// Derive a 32-byte key
val derivedKey = KDF.deriveKey(
    password = password,
    salt = salt,
    iterations = 100_000,
    keyLengthBytes = 32
)
```

## 🌍 Supported Platforms

| Platform | Support |
| :--- | :---: |
| **Android** | ✅ |
| **iOS** (Arm64, X64, Sim) | ✅ |
| **JVM** (Java 17+) | ✅ |
| **JavaScript** (Browser/Node) | ✅ |
| **Wasm** (WebAssembly) | ✅ |
| **macOS** (Arm64, X64) | ✅ |
| **Linux** (X64) | ✅ |
| **Windows** (MinGW X64) | ✅ |

## ⚖️ License

Kodium is licensed under the [Apache 2.0 License](LICENSE).

```text
Copyright 2026 Livotov Labs Ltd.
```

---
*Disclaimer: While this library implements standard cryptographic primitives based on TweetNaCl, it has **not been audited** by a security expert. Users should always review security requirements for their specific use case and use at their own risk.*