# Key Management

Kodium uses dedicated classes to ensure type safety and prevent accidental misuse of keys.

## Generating Classical Keys

To generate a new classical key pair (X25519/Ed25519 compatible):

```kotlin
import io.kodium.Kodium
import io.kodium.KodiumPrivateKey

// Generate a new key pair
val myKeyPair: KodiumPrivateKey = Kodium.generateKeyPair()

// Extract the public part to share with others for encryption and digital signatures
val myPublicKey = myKeyPair.getPublicKey()
```

## Exporting and Importing

Keys can be exported to and imported from Base64-encoded strings. This is highly recommended for storing keys safely. Kodium provides built-in mechanisms to encrypt your private keys before exporting them.

### Exporting Keys

```kotlin
// Export Public Key (Safe to share openly)
val base64PubKey: String = myPublicKey.exportToEncodedString()

// Export Private Key (Encrypted with a string password)
val base64PrivKey: String = myKeyPair.exportToEncryptedString("your-secure-password").getOrThrow()

// Export Private Key (Encrypted with a high-performance precomputed ByteArray key)
val symmetricKey: ByteArray = Kodium.generateHighEntropyKey()
val base64FastPrivKey: String = myKeyPair.exportToEncryptedString(symmetricKey).getOrThrow()

// Export Raw Private Key (Unprotected - Use only if your app manages its own encryption!)
val rawPrivKey: ByteArray = myKeyPair.exportToArray()
```

### Importing Keys

```kotlin
// Import the public key back
val importedPubKey = KodiumPublicKey.importFromEncodedString(base64PubKey).getOrThrow()

// Import the private key back using a string password
val importedPrivKey = KodiumPrivateKey.importFromEncryptedString(base64PrivKey, "your-secure-password").getOrThrow()

// Import using a precomputed key
val importedFastPrivKey = KodiumPrivateKey.importFromEncryptedString(base64FastPrivKey, symmetricKey).getOrThrow()

// Import from a raw, unprotected ByteArray
val importedRawPrivKey = KodiumPrivateKey.importFromArray(rawPrivKey).getOrThrow()
```

## Key Sizes

| Key Type | Raw Size | Base64 Encoded Size |
| :--- | :--- | :--- |
| **Classical Public Key** | 32 Bytes | ~44 chars |
| **Classical Private Key** | 32 Bytes | Variable (depends on encryption overhead) |
