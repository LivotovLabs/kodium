# Key Management

Kodium uses dedicated classes to ensure type safety and prevent accidental misuse of keys.

## Generating Classical Keys

To generate a new classical key pair (X25519/Ed25519 compatible):

```kotlin
import io.kodium.Kodium
import io.kodium.KodiumPrivateKey

// Generate a new key pair
val myKeyPair: KodiumPrivateKey = Kodium.generateKeyPair()

// Extract the public part to share with others for encryption (Box)
val myPublicKey = myKeyPair.getPublicKey()

// Extract the public part to share with others for digital signatures (Ed25519)
val mySignPublicKey = myKeyPair.getSignPublicKey()
```

## Exporting and Importing

Keys can be exported to and imported from Base58-encoded strings. This is highly recommended for storing keys safely. Kodium provides built-in mechanisms to encrypt your private keys before exporting them.

### Exporting Keys

```kotlin
// Export Public Key (Safe to share openly)
val b58PubKey: String = myPublicKey.exportToEncodedString()

// Export Private Key (Encrypted with a password)
val b58PrivKey: String = myKeyPair.exportToEncryptedString("your-secure-password").getOrThrow()
```

### Importing Keys

```kotlin
// Import the public key back
val importedPubKey = KodiumPublicKey.importFromEncodedString(b58PubKey).getOrThrow()

// Import the private key back, requiring the password
val importedPrivKey = KodiumPrivateKey.importFromEncryptedString(b58PrivKey, "your-secure-password").getOrThrow()
```

## Key Sizes

| Key Type | Raw Size | Base58 Encoded Size |
| :--- | :--- | :--- |
| **Classical Public Key** | 32 Bytes | ~44 chars |
| **Classical Private Key** | 32 Bytes | Variable (depends on encryption overhead) |
