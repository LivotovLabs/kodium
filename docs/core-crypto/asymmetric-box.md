# Asymmetric Encryption (Box)

Asymmetric encryption, or Public-Key Cryptography, allows two parties to securely exchange messages without ever sharing their private keys. Kodium implements authenticated encryption using Curve25519, XSalsa20, and Poly1305.

## Basic Usage

To securely send a message, the sender needs the recipient's **Public Key** and their own **Private Key**.

### 1. Generating Keys

```kotlin
// Alice and Bob generate their respective key pairs
val alice = Kodium.generateKeyPair()
val bob = Kodium.generateKeyPair()
```

### 2. Encrypting a Message (Alice to Bob)

Alice wants to send a secret message to Bob. She uses her private key and Bob's public key.

```kotlin
val message = "The eagle flies at midnight.".encodeToByteArray()

// Alice encrypts a message for Bob
val encryptedResult = Kodium.encryptToEncodedString(
    mySecretKey = alice,
    theirPublicKey = bob.getPublicKey(),
    data = message
)

// The result is a Result<String> containing the Base64Check encoded ciphertext
val cipherTextString = encryptedResult.getOrThrow()
```

### 3. Decrypting a Message (Bob from Alice)

Bob receives the `cipherTextString` from Alice. He decrypts it using his private key and Alice's public key. This not only decrypts the message but also **authenticates** that it truly came from Alice.

```kotlin
val decryptedResult = Kodium.decryptFromEncodedString(
    mySecretKey = bob,
    theirPublicKey = alice.getPublicKey(),
    data = cipherTextString
)

decryptedResult.onSuccess { decryptedBytes ->
    println("Decrypted: ${decryptedBytes.decodeToString()}")
}.onFailure { error ->
    println("Decryption failed or message was forged: ${error.message}")
}
```
