# Post-Quantum Cryptography (PQC) in Kodium

Kodium provides high-performance, pure Kotlin support for Post-Quantum Cryptography (PQC). Our implementation follows the **Hybrid Approach**, which is currently recommended by global cybersecurity agencies (such as NIST and ANSSI) for transitioning to a quantum-resistant future.

## 🛡 The Hybrid Approach

Quantum computers, while not yet powerful enough to break modern encryption, pose a future threat to classical asymmetric algorithms like RSA and Elliptic Curve Cryptography (including X25519).

To mitigate this risk without sacrificing the proven security of classical methods, Kodium's PQC suite uses a hybrid model:
1.  **Classical Layer:** Uses **X25519** (Curve25519) Diffie-Hellman.
2.  **Quantum Layer:** Uses **ML-KEM-768** (FIPS 203), formerly known as Kyber.

The secrets from both layers are mixed using **HKDF-SHA256**. This ensures that the resulting encryption is:
*   **At least as secure as X25519** (if ML-KEM is ever found to have a flaw).
*   **Secure against Quantum Computers** (if X25519 is ever broken by a large-scale quantum machine).

---

## 🔑 Key Management

PQC keys are significantly larger than classical keys. Kodium uses dedicated classes to ensure type safety and prevent accidental misuse.

### 1. Generating Keys
```kotlin
// Generates a hybrid key pair
val myKeys: KodiumPqcPrivateKey = Kodium.pqc.generateKeyPair()

// Extract the public part to share with others
val myPublicKey: KodiumPqcPublicKey = myKeys.getPublicKey()
```

### 2. Exporting and Importing
Keys are exported as Base58-encoded strings with checksums for easy storage.

```kotlin
// Export Public Key (Safe to share)
val b58Pk: String = myPublicKey.exportToEncodedString()

// Export Private Key (Encrypted with a password)
val b58Sk: String = myKeys.exportToEncryptedString("your-secure-password").getOrThrow()

// Import back
val importedPk = KodiumPqcPublicKey.importFromEncodedString(b58Pk).getOrThrow()
val importedSk = KodiumPqcPrivateKey.importFromEncryptedString(b58Sk, "your-secure-password").getOrThrow()
```

**Key Size Reference:**
| Key Type | Raw Size (approx) | B58 Encoded Size |
| :--- | :--- | :--- |
| **Classical PK** | 32 Bytes | ~44 chars |
| **Hybrid PQC PK** | 1,216 Bytes | ~1,600 chars |

---

## 📦 Basic Encryption & Decryption

The `Kodium.pqc` namespace mirrors the standard `Kodium` API but requires PQC-specific key types.

```kotlin
val aliceKeys = Kodium.pqc.generateKeyPair()
val bobPublicKey = ... // Received from Bob

val data = "Quantum-resistant secret".encodeToByteArray()

// Alice encrypts for Bob
val encrypted: ByteArray = Kodium.pqc.encrypt(
    mySecretKey = aliceKeys,
    theirPublicKey = bobPublicKey,
    data = data
).getOrThrow()

// Bob decrypts
val decrypted: ByteArray = Kodium.pqc.decrypt(
    mySecretKey = bobKeys,
    theirPublicKey = alicePublicKey,
    cipher = encrypted
).getOrThrow()
```

---

## 🔄 Double Ratchet & PQXDH Integration

The Double Ratchet algorithm in Kodium has been fully upgraded to support a **Post-Quantum Cryptographic Suite**. This allows you to establish end-to-end encrypted sessions that are resistant to "Harvest Now, Decrypt Later" attacks.

### Example: P2P Secure Chat (Post-Quantum)

This example demonstrates how Alice and Bob can securely exchange messages using the `PQDoubleRatchetSession` across the typical phases of an E2EE chat application.

#### Phase 1: Account Creation & Key Publishing

Users Alice and Bob create their accounts. During registration, their devices generate the necessary cryptographic keys, including the new Hybrid PQC Keys, and publish them to a central server.

```kotlin
import io.kodium.Kodium
import io.kodium.ratchet.PQXDH

// Bob creates an account and generates his standard Identity Keys
val bobIdentityKey = Kodium.generateKeyPair()

// Bob generates his long-term Hybrid Post-Quantum Keys for the KEM encapsulation
val bobHybridKeys = Kodium.pqc.generateKeyPair()

// Bob publishes his "PQC Public Bundle" to the chat server
val bobBundle = PQXDH.PublicBundle(
    identityKey = bobIdentityKey.getPublicKey(),
    pqcKey = bobHybridKeys.getPublicKey()
)

// Bob encodes the bundle to a Base58 string to easily send it over the network
val bobBundleString = bobBundle.exportToEncodedString().getOrThrow()

// --- Meanwhile, on Alice's device ---
val aliceIdentityKey = Kodium.generateKeyPair()
val aliceHybridKeys = Kodium.pqc.generateKeyPair() 
```

#### Phase 2: Alice Initiates Contact

Alice starts a chat with Bob. Her device fetches Bob's public bundle from the server and computes the quantum-resistant shared secret.

```kotlin
// Alice fetches Bob's Base58 encoded bundle string from the server
val fetchedBobBundleString = "..." // Fetched from server
val fetchedBobBundle = PQXDH.PublicBundle.importFromEncodedString(fetchedBobBundleString).getOrThrow()

// Alice computes the shared secret using PQXDH.
// Under the hood, this performs an X25519 DH exchange AND an ML-KEM encapsulation,
// mixing the results via HKDF.
val aliceSharedSecret = PQXDH.calculateSecretAsInitiator(
    initiatorIdentityKey = aliceIdentityKey,
    initiatorPqcKey = aliceHybridKeys,
    responderBundle = fetchedBobBundle
)

import io.kodium.ratchet.PQDoubleRatchetSession

// Alice initializes her PQC-enabled Double Ratchet session.
// Note: She provides Bob's KodiumPqcPublicKey and her own Private Key.
val aliceSession = PQDoubleRatchetSession.initializeAsInitiator(
    sharedSecret = aliceSharedSecret.masterSecret,
    responderPqcPublicKey = fetchedBobBundle.pqcKey,
    ourPqcPrivateKey = aliceHybridKeys
)

// Alice encrypts her first message.
// The session automatically handles the KEM encapsulation and attaches the 
// hybrid ciphertext (~1KB) to the message header.
val messageFromAlice = "Hello Bob, securely in the Post-Quantum era!".encodeToByteArray()
val firstEncodedMessage = aliceSession.encryptToEncodedString(messageFromAlice).getOrThrow()

// Alice sends `firstEncodedMessage` to the server, along with her `aliceSharedSecret.encapsulationPayload`
// (which is exported to a string) so Bob can decapsulate and compute the shared secret.
val alicePayloadString = aliceSharedSecret.encapsulationPayload.exportToEncodedString().getOrThrow()
```

#### Phase 3: Bob Responds & Secure Chat Continues

Bob receives Alice's request. His device uses his private keys and Alice's provided payload to compute the identical shared secret and initialize his session.

```kotlin
import io.kodium.ratchet.PQXDH.PQInitiatorPayload

// Bob parses Alice's payload from the server
val fetchedAlicePayload = PQInitiatorPayload.importFromEncodedString(alicePayloadString).getOrThrow()

// Bob computes the shared secret using Alice's provided payload
val bobSharedSecret = PQXDH.calculateSecretAsResponder(
    responderIdentityKey = bobIdentityKey,
    responderPqcKey = bobHybridKeys,
    initiatorPayload = fetchedAlicePayload // Received from Alice
)

// Bob initializes his session using his Hybrid Private Key and Alice's Public Key from the payload
val bobSession = PQDoubleRatchetSession.initializeAsResponder(
    sharedSecret = bobSharedSecret,
    ourPqcPrivateKey = bobHybridKeys,
    initiatorPqcPublicKey = fetchedAlicePayload.pqcPublicKey!!
)

// Bob decrypts Alice's first message.
// The session detects the PQC header, decapsulates the ML-KEM secret,
// mixes it with the X25519 secret, and steps the ratchet.
val decryptedByBob = bobSession.decryptFromEncodedString(firstEncodedMessage).getOrThrow()
println("Bob reads: ${decryptedByBob.decodeToString()}") 
// Output: "Hello Bob, securely in the Post-Quantum era!"

// ---------------------------------------------------------
// BOB REPLIES
// ---------------------------------------------------------

// When Bob replies, his session encapsulates a new ML-KEM secret for Alice's next turn.
// Note: The DH Ratchet step is triggered automatically when the direction of messages changes!
val encryptedReplyFromBob = bobSession.encryptToEncodedString(
    "Loud and clear, Alice!".encodeToByteArray()
).getOrThrow()

// --- Back on Alice's device ---
// Alice receives and decrypts Bob's reply
val decryptedByAlice = aliceSession.decryptFromEncodedString(encryptedReplyFromBob).getOrThrow()
println("Alice reads: ${decryptedByAlice.decodeToString()}") 
// Output: "Loud and clear, Alice!"
```

### Protocol Changes
When PQC is enabled in a Double Ratchet session via `PQDoubleRatchetSession`:
*   **Handshake:** The initial key exchange (`PQXDH`) includes an ML-KEM encapsulation alongside the standard X25519 prekeys.
*   **Ratchet Steps:** Every asymmetric ratchet step performs both an X25519 DH exchange and an ML-KEM encapsulation/decapsulation to the static long-term keys. The secrets are combined using HKDF to derive the new root key.
*   **Header Size:** Message headers are larger. A classical Double Ratchet header is 40 bytes. A `PQRatchetHeader` carries the ML-KEM ciphertext (1088 bytes for ML-KEM-768), bringing the header size to 1,128 bytes.

---

## ⚡️ Technical Specifications

*   **Standard:** NIST FIPS 203 (ML-KEM).
*   **Security Level:** ML-KEM-768 (equivalent to AES-192/RSA-4096 quantum security).
*   **KDF:** HKDF-SHA256 for secret mixing.
*   **Cipher:** XSalsa20-Poly1305 (via NaCl SecretBox).

---
*Note: PQC is a rapidly evolving field. This implementation is based on the finalized FIPS 203 standard. As with all cryptography, ensure your library is kept up to date.*
