# Symmetric Encryption (SecretBox)

Symmetric encryption uses the exact same shared secret (like a password or a derived key) to both encrypt and decrypt data. 

Kodium uses authenticated encryption (XSalsa20 and Poly1305), meaning that any tampering with the ciphertext will be detected during decryption.

> **Note:** Symmetric encryption via `SecretBox` (XSalsa20-Poly1305) with a 256-bit key is currently considered quantum-resistant.

## Basic Usage

You can encrypt data directly using a strong password string. Kodium will handle the necessary key derivation under the hood.

### Encrypting

```kotlin
import io.kodium.Kodium

val password = "CorrectHorseBatteryStaple"
val secretData = "Launch codes: 12345".encodeToByteArray()

// Encrypt the data using the password
val encryptedResult = Kodium.encryptSymmetricToEncodedString(password, secretData)

// The result is a Result<String> containing the Base58 encoded ciphertext
val cipherTextString = encryptedResult.getOrThrow()
```

### Decrypting

To decrypt the data, you must provide the exact same password.

```kotlin
// Decrypt the data
val decryptedResult = Kodium.decryptSymmetricFromEncodedString(password, cipherTextString)

decryptedResult.onSuccess { originalBytes ->
    println("Restored: ${originalBytes.decodeToString()}")
}.onFailure { error ->
    println("Failed to decrypt: Incorrect password or corrupted data.")
}
```
