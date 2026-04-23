# Kodium

[![Maven Central](https://img.shields.io/maven-central/v/eu.livotov.labs/kodium)](https://search.maven.org/artifact/eu.livotov.labs/kodium)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-2.3-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Platform](https://img.shields.io/badge/platform-android%20|%20ios%20|%20jvm%20|%20js%20|%20linux%20|%20macos%20|%20mingw-7f52ff.svg)](http://kotlinlang.org)

<p align="center">
  <img src="logo.svg" width="256" alt="Kodium Logo">
</p>

**Secure. Portable. Pure Kotlin.**

**Kodium** is a comprehensive, pure Kotlin Multiplatform (KMP) cryptography library. It acts as a faithful port of the renowned **TweetNaCl** C library, providing high-speed, high-security cryptographic primitives, advanced **Double Ratchet** session management, and **Post-Quantum Cryptography (PQC)** protocols without *any* native dependencies. 

Write once, encrypt everywhere. Even in a post-quantum world.

---

## 📑 Table of Contents
1. [Kodium Highlights](#-kodium-highlights)
2. [Release Notes](#-release-notes)
3. [Supported Platforms](#-supported-platforms)
4. [Features](#-features)
5. [Installation](#-installation)
6. [Quick Start Guide](#-quick-start)
7. [Documentation](#-documentation)
8. [Examples and Deep Dives](#-examples--deep-dives)
9. [Contributing](#-contributing)
10. [License & Disclaimer](#-license)

---

## ⚡️ Kodium Highlights

*   **Pure Kotlin:** No JNI, no C interop headaches, no complex build scripts for native binaries. Just pure Kotlin code running everywhere.
*   **TweetNaCl Core:** Built on the solid foundation of the TweetNaCl crypto suite, known for its security and simplicity.
*   **Post-Quantum Ready:** Hybrid cryptographic primitives combining classical X25519 with FIPS 203 (ML-KEM) to protect against future quantum computers. Note that symmetric encryption (SecretBox) is naturally quantum-resistant.
*   **Double Ratchet:** Includes a full implementation of the Double Ratchet Algorithm and X3DH for secure End-to-End Encryption (E2EE), featuring a built-in LRU policy for skipped message keys to prevent memory exhaustion.
*   **Multiplatform Native:** First-class support for Android, iOS, JVM, JavaScript (Browser/Node), Wasm, Linux, macOS, and Windows.
*   **Developer Friendly:** Simple, opinionated APIs for common tasks (Box, SecretBox, Signatures).

---

## 📝 Release Notes

### v1.0.0-beta-1
*   **Security & Compatibility Fix:** Fixed a critical bug in the internal SHA-512 implementation that caused Ed25519 detached signatures to fail verification against external systems (e.g., standard JWT validators). Kodium signatures are now fully standard and cross-compatible.
*   **Optimization:** Replaced manual SHA-512 and secure random byte generation logic with optimized calls to the `kotlincrypto` library for improved performance and stability.
*   **Testing & Reliability:** Added comprehensive tests for large payload signatures (~37KB) to ensure robust block padding and boundary handling.
*   **Documentation:** Corrected API documentation to accurately reflect the use of PBKDF2 with HMAC-SHA256 (instead of HMAC-SHA512) for symmetric key derivation.

### v1.0.0-alpha-3
*   **Advanced Key Management:** Added support for raw, unprotected `ByteArray` exports and imports (`exportToArray()`, `importFromArray()`) across all private keys (`KodiumPrivateKey`, `KodiumPqcPrivateKey`) and E2EE sessions (`DoubleRatchetSession`, `PQDoubleRatchetSession`) to support apps managing their own secure storage.
*   **High-Performance Persistence:** Introduced `ByteArray` precomputed key support for symmetric encryption and state persistence, allowing developers to bypass PBKDF2 overhead when importing/exporting keys and ratchet sessions.
*   **Convenience API Additions:** Added `Kodium.generateHighEntropyKey()`, `Kodium.generateRandomSalt()`, and `Kodium.deriveKeyFromPassword()` to simplify symmetric key lifecycle management.

### v1.0.0-alpha-2
*   Ed25519 detached signature support

### v1.0.0-alpha-1  
*   **Post-Quantum Cryptography:** Added `Kodium.pqc` namespace with support for Hybrid ML-KEM-768 + X25519 encryption.
*   **FIPS 203 Compliance:** Integrated a pure Kotlin implementation of the ML-KEM (Kyber) standard.
*   **Double Ratchet Algorithm:** Full implementation of the Signal-style Double Ratchet and X3DH protocols for secure End-to-End Encrypted messaging.
*   **Expanded HKDF:** Updated secret mixing to support high-entropy hybrid keys.
*   Upgraded to **Kotlin 2.3.10**.
*   Full KDoc documentation for all public APIs.
*   Improved test coverage across JVM and JS targets.

### v0.0.1
*   Initial implementation of the library.
*   Port of TweetNaCl (Box, SecretBox, Signatures).
*   Base58Check encoding/decoding.

---

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

---

## 🛠 Features

*   **End-to-End Encryption:** Double Ratchet Algorithm & X3DH (Extended Triple Diffie-Hellman).
*   **Post-Quantum Hybrid Encryption:** FIPS 203 (ML-KEM-768) + Curve25519 authenticated encryption.
*   **Public-Key Cryptography (Box):** Authenticated encryption using Curve25519, XSalsa20, and Poly1305.
*   **Secret-Key Cryptography (SecretBox):** Authenticated encryption using XSalsa20 and Poly1305.
*   **Digital Signatures:** Ed25519 high-speed, high-security signatures.
*   **Key Management:** Secure generation, import, and export of keys (Raw & Base58Check).
*   **Utils:** Robust Base58Check encoding/decoding and HKDF (RFC 5869).

---

## 📦 Installation

Add Kodium to your common module's dependencies.

**Gradle (Kotlin DSL)**
```kotlin
implementation("eu.livotov.labs:kodium:1.0.0")
```

**Gradle (Groovy)**
```groovy
implementation 'eu.livotov.labs:kodium:1.0.0'
```

**Maven**
```xml
<dependency>
    <groupId>eu.livotov.labs</groupId>
    <artifactId>kodium</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 🚀 Quick Start

### 1. Secure Messaging (Double Ratchet)
Kodium provides a complete implementation of the Double Ratchet algorithm for secure E2EE messaging.

```kotlin
// Alice initializes her session as the initiator
val aliceSession = DoubleRatchetSession.initializeAsInitiator(sharedSecret, responderRatchetKey)

// Encrypt a message to a Base58 string
val encrypted = aliceSession.encryptToEncodedString("Hello Bob!".encodeToByteArray()).getOrThrow()

// Bob decrypts it back
val bobSession = DoubleRatchetSession.initializeAsResponder(sharedSecret, responderRatchetKeyPair)
val decrypted = bobSession.decryptFromEncodedString(encrypted).getOrThrow()
```

### 2. Post-Quantum Secure Messaging (PQ Double Ratchet)
Upgrade your E2EE sessions to be resistant to quantum computer attacks using the `PQDoubleRatchetSession`.

```kotlin
// Alice initializes her PQ session using the secrets from PQXDH
val aliceSession = PQDoubleRatchetSession.initializeAsInitiator(
    sharedSecret = aliceSharedSecret.masterSecret,
    responderPqcPublicKey = fetchedBobBundle.pqcKey,
    ourPqcPrivateKey = aliceHybridKeys
)

val encrypted = aliceSession.encryptToEncodedString("Post-Quantum Hello!".encodeToByteArray()).getOrThrow()

// Bob initializes his session using his keys and Alice's provided payload
val bobSession = PQDoubleRatchetSession.initializeAsResponder(
    sharedSecret = bobSharedSecret,
    ourPqcPrivateKey = bobHybridKeys,
    initiatorPqcPublicKey = fetchedAlicePayload.pqcPublicKey!!
)
val decrypted = bobSession.decryptFromEncodedString(encrypted).getOrThrow()
```

### 2. Post-Quantum Encryption (Hybrid PQC)
Protect your data against future quantum computer attacks using the hybrid `Kodium.pqc` suite.

```kotlin
// 1. Generate Hybrid Keys (X25519 + ML-KEM-768)
val myKeys = Kodium.pqc.generateKeyPair()
val theirPublicKey = ... // Received from peer

// 2. Encrypt
val encrypted = Kodium.pqc.encryptToEncodedString(
    mySecretKey = myKeys,
    theirPublicKey = theirPublicKey,
    data = "Secret message".encodeToByteArray()
).getOrThrow()

// 3. Decrypt
val decrypted = Kodium.pqc.decryptFromEncodedString(
    mySecretKey = myKeys,
    theirPublicKey = theirPublicKey,
    data = encrypted
).getOrThrow()
```

### 3. Asymmetric Encryption (Box)
Securely exchange messages between Alice and Bob without session management.

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

### 4. Symmetric Encryption (SecretBox)
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

### 5. Digital Signatures
Prove authenticity and integrity using detached Ed25519 digital signatures.

```kotlin
val myPrivateKey = Kodium.generateKeyPair()
val message = "This message is authentic".encodeToByteArray()

// Sign
val signatureB58 = Kodium.signDetachedToEncodedString(myPrivateKey, message).getOrThrow()

// Verify using the signer's Public Key
val isValid = Kodium.verifyDetachedFromEncodedString(
    theirPublicKey = myPrivateKey.getSignPublicKey(), 
    data = message, 
    signatureB58 = signatureB58
)
```

### 6. Key Export & Import
Easily store keys using Base58Check encoding.

```kotlin
val keyPair = Kodium.generateKeyPair()

// Export Public Key (Safe to share)
val pubKeyString = keyPair.getPublicKey().exportToEncodedString()

// Export Signing Public Key (Safe to share, needed for signature verification)
val signPubKeyString = keyPair.getSignPublicKey().exportToEncodedString()

// Export Private Key (Encrypted with a password)
val privKeyString = keyPair.exportToEncryptedString("StrongPassword")

// Import later
val restoredKeyPair = KodiumPrivateKey.importFromEncryptedString(
    data = privKeyString.getOrThrow(), 
    password = "StrongPassword"
)
```

---

## 📖 Documentation

The complete documentation for Kodium is available online and within the repository.

👉 **[Online Documentation (Manual & API Reference)](https://livotovlabs.gitbook.io/kodium/)**

The `docs/` directory is structured for GitBook and contains:
*   [**Manual**](docs/README.md): High-level guides, architectural overviews, and usage examples.
*   [**API Reference**](docs/api/index.md): Detailed documentation for every package, class, and function, generated from KDocs.

---

## 📚 Examples & Deep Dives

For advanced usage and detailed technical explanations, refer to our deep-dive standalone guides.

### 1. End-to-End Encrypted Chat (Double Ratchet)
Learn how to build a fully secure, asynchronous peer-to-peer chat application using the classical Double Ratchet protocol. This guide covers the complete lifecycle:
*   Account creation and public key publishing.
*   Asynchronous X3DH handshake.
*   Session initialization and secure message exchange.
*   Advanced topics like Context Binding and Session State Persistence.

👉 **[Read the full Double Ratchet & X3DH Guide](docs/e2ee/double-ratchet.md)**

### 2. Post-Quantum Cryptography (PQC)
Future-proof your application against "Harvest Now, Decrypt Later" attacks by upgrading to Kodium's Hybrid PQC suite. This guide covers:
*   The theory behind mixing X25519 with FIPS 203 (ML-KEM-768).
*   Managing and persisting large Hybrid Keys.
*   Establishing a Post-Quantum Double Ratchet session for next-generation E2EE security.

👉 **[Read the full PQC Reference Guide](docs/pqc/post-quantum-cryptography.md)**

---

## 🤝 Contributing

We welcome contributions! If you're interested in building Kodium from source, running tests, or updating the documentation, please refer to our [Developer Guide](DEVELOPER.md).

---

## ⚖️ License

Kodium is licensed under the [Apache 2.0 License](LICENSE).

The Post-Quantum ML-KEM math implementation in this project is based on the excellent [KyberKotlin](https://github.com/ronhombre/KyberKotlin) project by Ron Lauren Hombre.

```text
Copyright 2026 Livotov Labs Ltd.
```

---
*Disclaimer: While this library implements standard cryptographic primitives based on TweetNaCl, it has **not been audited** by a security expert. Users should always review security requirements for their specific use case and use at their own risk.*
