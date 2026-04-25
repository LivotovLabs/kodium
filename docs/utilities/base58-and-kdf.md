# Base64 & KDF

## Base64 Encoding

Kodium provides efficient Base64 and Base64Check extensions for `ByteArray` and `String`. Base64 is superior to Base64 for many crypto use cases as it eliminates ambiguous characters (`0`, `O`, `I`, `l`) and is more compact than hexadecimal representation.

```kotlin
val data = "Kodium Pure Kotlin".encodeToByteArray()

// Standard Base64
val base64 = data.encodeToBase64String()
val decoded = base64.decodeBase64()

// Base64 with Checksum (Standard in Bitcoin and many crypto networks)
// The checksum helps prevent accidental typos when users manually enter or copy strings.
val base64Check = data.encodeToBase64WithChecksum()
val decodedCheck = base64Check.decodeBase64WithChecksum()
```

## Password-Based Key Derivation (PBKDF2)

Deriving strong cryptographic keys from user passwords is a common requirement. Kodium provides a built-in Key Derivation Function (KDF) using PBKDF2 with HMAC-SHA256.

```kotlin
val password = "UserPassword123".encodeToByteArray()
val salt = "RandomSalt".encodeToByteArray() // Should be securely random and at least 16 bytes

// Derive a 32-byte key (suitable for SecretBox)
val derivedKey = KDF.deriveKey(
    password = password,
    salt = salt,
    iterations = 100_000, // Higher iterations = slower derivation, harder to brute force
    keyLengthBytes = 32
)
```
