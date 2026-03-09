# Base58 & KDF

## Base58 Encoding

Kodium provides efficient Base58 and Base58Check extensions for `ByteArray` and `String`. Base58 is superior to Base64 for many crypto use cases as it eliminates ambiguous characters (`0`, `O`, `I`, `l`) and is more compact than hexadecimal representation.

```kotlin
val data = "Kodium Pure Kotlin".encodeToByteArray()

// Standard Base58
val b58 = data.encodeToBase58String()
val decoded = b58.decodeBase58()

// Base58 with Checksum (Standard in Bitcoin and many crypto networks)
// The checksum helps prevent accidental typos when users manually enter or copy strings.
val b58Check = data.encodeToBase58WithChecksum()
val decodedCheck = b58Check.decodeBase58WithChecksum()
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
