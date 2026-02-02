# Kodium

Kodium is a pure Kotlin Multiplatform library designed to simplify common cryptographic operations.
It provides functionalities for key pair generation, asymmetric (public/private key) encryption/decryption,
symmetric (password-based) encryption/decryption, and Base58Check encoding/decoding.

This library uses a pure-kotlin port of TweetNaCl C library as the encryption backend.

## Purpose

This library aims to provide a straightforward API for developers to:
*   Securely generate and manage cryptographic key pairs.
*   Encrypt and decrypt data using asymmetric encryption, suitable for scenarios where parties need to exchange encrypted information using public keys.
*   Encrypt and decrypt data using symmetric encryption, ideal for protecting data with a password.
*   Encode binary data into a string format (Base58Check) for easier storage or transmission, and decode it back.
*   Handle cryptographic operations in a Kotlin Multiplatform project, targeting common code for different platforms.

## Features

*   **Key Pair Management**:
   *   Generate Ed25519 key pairs.
   *   Import/Export key pairs and public keys from/to Base58Check encoded strings for easy storage and  transmission.


*   **Asymmetric Encryption (Box / Public-key cryptography)**:
   *   Encrypt and sign data using a sender's private key and a receiver's public key.
   *   Decrypt data and verify integrity using a receiver's private key and a sender's public key.
   *   Outputs can be raw byte arrays or Base58Check encoded strings.


*   **Symmetric Encryption (SecretBox / Password-based cryptography)**:
   *   Encrypt data using a password.
   *   Decrypt data using the same password.
   *   Outputs can be raw byte arrays or Base58Check encoded strings.


*   **Utility**:
   *   Encode byte arrays to Base58Check strings.
   *   Decode Base58Check strings to byte arrays.
*   **Error Handling**: Operations return a `Result` type, making it easy to handle success and failure cases.


## Usage

Here's how you can use the core functionalities of `Kodium`:

### 1. Key Pair Generation

Generate a new key pair:

```kotlin
// Generate a new key pair
val keyPair = Kodium.generateKeypPair()

// Export keys to strings (Base58Check encoded) for simplified storage and/or transmission
val publicKeyString = keyPair.exportPublicKeyToString()

// When exporting private key, the result is always encrypted with either default or user-supplied password. 
val keyPairString = keyPair.exportKeyPairToString()
val keyPairString = keyPair.exportKeyPairToString("myPassword")
```


### 2. Asymmetric Encryption and Decryption
Let's assume Alice wants to send a secret message to Bob.

```kotlin
// Alice generates her key pair
val aliceKeyPair = Kodium.generateKeypPair()

// Bob generates his key pair
val bobKeyPair = Kodium.generateKeypPair()

// Alice wants to send a message to Bob. She needs Bob's public key.
val messageToBob = "Hello Bob, this is a secret message!"
val dataToEncrypt = messageToBob.encodeToByteArray()

// Alice encrypts the message using her private key and Bob's public key
val encryptionResult = Kodium.encryptToEncodedString(
    senderPrivateKey = aliceKeyPair.privateKey,
    receiverPublicKey = bobKeyPair.publicKey,
    data = dataToEncrypt
)

encryptionResult.fold(
    onSuccess = { encryptedString ->
        println("Encrypted Message for Bob: $encryptedString")

        // Bob receives the encrypted string and decrypts it
        // using his private key and Alice's public key
        val decryptionResult = Kodium.decryptFromEncodedString(
            receiverPrivateKey = bobKeyPair.privateKey,
            senderPublicKey = aliceKeyPair.publicKey,
            data = encryptedString
        )

        decryptionResult.fold(
            onSuccess = { decryptedData ->
                val decryptedMessage = decryptedData.decodeToString()
                println("Bob Decrypted Message: $decryptedMessage") // Outputs: Hello Bob, this is a secret message!
            },
            onFailure = { error ->
                println("Bob: Decryption failed: ${error.message}")
            }
        )
    },
    onFailure = { error ->
        println("Alice: Encryption failed: ${error.message}")
    }
)
```

### 3. Symmetric Encryption and Decryption
Encrypt and decrypt data using a password.

```kotlin
val mySecretData = "This data needs to be password protected".encodeToByteArray()
val password = "SuperSecretPassword123"

// Encrypt data
val symmetricEncryptionResult = Kodium.encryptSymmetricToEncodedString(
    password = password,
    data = mySecretData
)

symmetricEncryptionResult.fold(
    onSuccess = { encryptedSymmetricString ->
        println("Symmetrically Encrypted Data: $encryptedSymmetricString")

        // Decrypt data
        val symmetricDecryptionResult = Kodium.decryptSymmetricFromEncodedString(
            password = password,
            data = encryptedSymmetricString
        )

        symmetricDecryptionResult.fold(
            onSuccess = { decryptedSymmetricData ->
                val originalMessage = decryptedSymmetricData.decodeToString()
                println("Symmetrically Decrypted Data: $originalMessage") // Outputs: This data needs to be password protected
            },
            onFailure = { error ->
                println("Symmetric Decryption failed: ${error.message}")
            }
        )
    },
    onFailure = { error ->
        println("Symmetric Encryption failed: ${error.message}")
    }
)
```

### 4. Encoding and Decoding Data
Convert byte arrays to and from Base58Check encoded strings.

```kotlin
val originalData = "Some binary data here".encodeToByteArray()

// Encode
val encodeResult = Kodium.encodeArrayToString(originalData)
encodeResult.fold(
    onSuccess = { encodedString ->
        println("Encoded: $encodedString")

        // Decode
        val decodeResult = Kodium.decodeArrayFromString(encodedString)
        decodeResult.fold(
            onSuccess = { decodedData ->
                println("Decoded successfully: ${originalData.contentEquals(decodedData)}") // true
                println("Decoded Data: ${decodedData.decodeToString()}")
            },
            onFailure = { error -> println("Decoding failed: ${error.message}") }
        )
    },
    onFailure = { error -> println("Encoding failed: ${error.message}") }
)
```

### 5. Importing Keys
You can import keys that were previously exported as Base58Check strings:

```kotlin
// Assuming publicKeyString and keyPairString from previous examples
val importedPublicKeyResult = Kodium.importPublicKeyFromString(publicKeyString)
importedPublicKeyResult.fold(
    onSuccess = { pubKeyBytes -> println("Imported public key successfully.") },
    onFailure = { error -> println("Failed to import public key: ${error.message}") }
)

val importedKeyPairResult = KodiumKeyPair.importKeyPairFromString(keyPairString)
val importedKeyPairResult = KodiumKeyPair.importKeyPairFromString(keyPairString, "myPassword")
importedKeyPairResult.fold(
    onSuccess = { keyPair -> println("Imported key pair successfully. Public key matches: ${keyPair.exportPublicKeyToString() == publicKeyString}") },
    onFailure = { error -> println("Failed to import key pair: ${error.message}") }
)
```

## Error Handling
All encryption, decryption, encoding, and decoding operations return `kotlin.Result<T>`.
You should use `fold`, `getOrNull`, `exceptionOrNull`, or similar extension functions to handle potential errors.


## Dependencies
This library utilizes it's internal kotlin port of TweetNaCl C  library and for its underlying cryptographic primitives
(Secure Random and MAC/HMAC) the `org.kotlincrypto` packages.

