# Quick Start

Kodium is designed to be simple and easy to integrate. This guide will help you get started quickly with the most common cryptographic operations.

## Installation

Add Kodium to your common module's dependencies.

**Gradle (Kotlin DSL)**
```kotlin
implementation("eu.livotov.labs:kodium:1.0.0") // Use actual version number
```

## First Steps

Once installed, you can access Kodium's API either via the `Kodium` singleton or specifically via `Kodium.pqc` for Post-Quantum algorithms.

### Generating Keys

```kotlin
// Classical X25519 Key Pair
val classicalKeys = Kodium.generateKeyPair()

// Hybrid Post-Quantum Key Pair (X25519 + ML-KEM-768)
val pqcKeys = Kodium.pqc.generateKeyPair()
```

### Digital Signatures
Prove authenticity and integrity using detached Ed25519 digital signatures.

```kotlin
val message = "This message is authentic".encodeToByteArray()

// Sign
val signatureBase64 = Kodium.signDetachedToEncodedString(classicalKeys, message).getOrThrow()

// Verify using the signer's Public Key
val isValid = Kodium.verifyDetachedFromEncodedString(
    theirPublicKey = classicalKeys.getSignPublicKey(), 
    data = message, 
    signatureBase64 = signatureBase64
)
```

Kodium handles complex operations behind simple APIs. Check out the dedicated sections for Digital Signatures, Asymmetric Encryption, Symmetric Encryption, Double Ratchet, and Post-Quantum cryptography.
