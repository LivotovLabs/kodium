# Double Ratchet in Kodium

Kodium provides a complete, pure-Kotlin implementation of the Double Ratchet Algorithm along with the X3DH (Extended Triple Diffie-Hellman) key agreement protocol. This combination allows you to build secure, end-to-end encrypted (E2EE) peer-to-peer applications, such as a secure chat application.

## Overview

The Double Ratchet algorithm combines a cryptographic ratchet based on Diffie-Hellman (DH) key exchanges with a symmetric-key ratchet. This ensures both **Forward Secrecy** (compromise of current keys does not compromise past messages) and **Break-in Recovery** or **Future Secrecy** (compromise of current keys does not compromise future messages).

X3DH establishes a shared secret between two parties asynchronously, acting as the secure foundation to initialize the Double Ratchet sessions.

## Example: P2P Secure Chat

This example demonstrates how Alice and Bob can securely exchange messages using Kodium's Double Ratchet implementation across the typical phases of a P2P chat application.

### Phase 1: Account Creation & Key Publishing

Users Alice and Bob create their accounts in the chat app independently. During registration, their devices generate the necessary cryptographic keys and publish public data to a central server so they can be contacted later.

```kotlin
import io.kodium.KodiumPrivateKey
import io.kodium.ratchet.X3DH

// Bob creates an account and generates his keys
val bobIdentityKey = KodiumPrivateKey.generate()
val bobSignedPreKey = KodiumPrivateKey.generate()
val bobOneTimePreKey = KodiumPrivateKey.generate()

// Bob publishes his "Public Bundle" to the chat server
val bobBundle = X3DH.PublicBundle(
    identityKey = bobIdentityKey.publicKey,
    signedPreKey = bobSignedPreKey.publicKey,
    oneTimePreKey = bobOneTimePreKey.publicKey
)

// Bob can encode the bundle to a Base58 string to easily send it over the network
// and save it in the central database
val bobBundleString = bobBundle.exportToEncodedString().getOrThrow()

// --- Meanwhile, on Alice's device ---

// Alice also creates an account and generates her keys
val aliceIdentityKey = KodiumPrivateKey.generate()
val aliceEphemeralKey = KodiumPrivateKey.generate() // Generated specifically when she wants to start a chat
```

### Phase 2: Alice Initiates Contact

Alice decides to get to know Bob. She gets his username, decides to add him to her contact list, and starts a chat. Her device fetches Bob's public bundle from the server and computes the shared secret.

```kotlin
// Alice fetches Bob's Base58 encoded bundle string from the server and parses it
val fetchedBobBundleString = "..." // Fetched from the server
val fetchedBobBundle = X3DH.PublicBundle.importFromEncodedString(fetchedBobBundleString).getOrThrow()

// Alice computes the shared secret using X3DH
val aliceSharedSecret = X3DH.calculateSecretAsInitiator(
    initiatorIdentityKey = aliceIdentityKey,
    initiatorEphemeralKey = aliceEphemeralKey,
    responderBundle = fetchedBobBundle
)

import io.kodium.ratchet.DoubleRatchetSession

// Alice initializes her Double Ratchet session as the sender of the first message.
// She uses the shared secret and Bob's public Ratchet Key (his Signed PreKey).
val aliceSession = DoubleRatchetSession.initializeAsInitiator(
    sharedSecret = aliceSharedSecret,
    responderRatchetKey = fetchedBobBundle.signedPreKey
)

// Alice encrypts her first message
val messageFromAlice = "Hello Bob! I'd like to chat.".encodeToByteArray()
val firstEncodedMessage = aliceSession.encryptToEncodedString(messageFromAlice).getOrThrow()

// Alice sends the `firstEncodedMessage` to the server, along with her public keys 
// (Alice's Identity Key and her Ephemeral Key) so Bob can compute the shared secret.
```

### Phase 3: Bob Responds & Secure Chat Continues

Bob receives Alice's request. He decides to accept her chat. His device uses his private keys and Alice's provided public keys to compute the identical shared secret and initialize his session.

```kotlin
// Bob computes the shared secret using Alice's public keys
val bobSharedSecret = X3DH.calculateSecretAsResponder(
    responderIdentityKey = bobIdentityKey,
    responderSignedPreKey = bobSignedPreKey,
    responderOneTimePreKey = bobOneTimePreKey, // Depending on whether Alice used it
    initiatorIdentityKey = aliceIdentityKey.publicKey,
    initiatorEphemeralKey = aliceEphemeralKey.publicKey
)

// Bob initializes his session as the receiver of the first message.
// He uses the shared secret and his Ratchet Key Pair (his Signed PreKey pair).
val bobSession = DoubleRatchetSession.initializeAsResponder(
    sharedSecret = bobSharedSecret,
    responderRatchetKeypair = bobSignedPreKey
)

// Bob decrypts Alice's first message
val decryptedByBob = bobSession.decryptFromEncodedString(firstEncodedMessage).getOrThrow()
println("Bob reads: ${decryptedByBob.decodeToString()}")

// Bob replies to Alice. 
// Note: The DH Ratchet step is triggered automatically when the direction of messages changes!
val messageFromBob = "Hi Alice! Nice to meet you.".encodeToByteArray()
val replyEncodedString = bobSession.encryptToEncodedString(messageFromBob).getOrThrow()

// --- Back on Alice's device ---
// Alice receives and decrypts Bob's reply
val decryptedByAlice = aliceSession.decryptFromEncodedString(replyEncodedString).getOrThrow()
println("Alice reads: ${decryptedByAlice.decodeToString()}")
```

## Session Lifespan: Can a session be used forever?

Technically, **yes**. Once a `DoubleRatchetSession` is established, the math allows it to run indefinitely. 

The algorithm is specifically designed for long-lived asynchronous messaging:
*   The **Symmetric Ratchet** ensures forward secrecy for every single message.
*   The **Diffie-Hellman (DH) Ratchet** introduces new entropy whenever the direction of the conversation changes, ensuring break-in recovery. 

You do **not** need to periodically recreate or expire the session strictly based on a time period. 

However, in practice, a session might need to be recreated (requiring a new X3DH exchange) if:
1.  **Device Loss/Reset:** A user loses their device, wipes their local database, or uninstalls the app without a backup, permanently losing their current session state.
2.  **Manual Key Rotation:** The application enforces a strict security policy requiring complete identity key rotation.
3.  **Irrecoverable Desynchronization:** If an extreme number of messages are permanently lost in transit (exceeding the configured `MAX_SKIP` window), the chains cannot recover and a new session must be established.

## Advanced Usage: Context Binding

To prevent cross-protocol attacks, the X3DH and Double Ratchet algorithms support passing an optional `applicationInfo` (or `info`) byte array. This ensures that cryptographic keys derived in your application cannot accidentally or maliciously be used to decrypt messages in a completely different application that might be using the exact same underlying keys.

```kotlin
val myAppInfo = "MySecureChatApp-DoubleRatchet-V1".encodeToByteArray()

// Use the app info during X3DH shared secret calculation
val sharedSecret = X3DH.calculateSecretAsInitiator(
    initiatorIdentityKey = aliceIdentityKey,
    initiatorEphemeralKey = aliceEphemeralKey,
    responderBundle = fetchedBobBundle,
    info = myAppInfo
)

// Pass the same app info into the Double Ratchet session
val session = DoubleRatchetSession.initializeAsInitiator(
    sharedSecret = sharedSecret,
    responderRatchetKey = fetchedBobBundle.signedPreKey,
    applicationInfo = myAppInfo
)
```

## Advanced Usage: Associated Data (AD)

To prevent replay attacks and ensure context binding, you can provide `Associated Data` when encrypting messages. Both the sender and receiver must provide the exact same Associated Data (such as conversation IDs or protocol versions).

```kotlin
val associatedData = "ChatProtocolV1".encodeToByteArray()

// Sender
val encoded = session.encryptToEncodedString(data, associatedData).getOrThrow()

// Receiver
val decoded = session.decryptFromEncodedString(encoded, associatedData).getOrThrow()
```

Kodium automatically handles appending the plaintext Ratchet header to the Associated Data to authenticate it during the AEAD encryption process.

## Persisting Sessions Between App Restarts

Since a Double Ratchet session manages several moving pieces (ratchet keys, chains, and out-of-order message keys), you will need to store its state securely between app restarts.

Kodium's `DoubleRatchetSession` offers simple APIs to export and import the entire session state as a securely encrypted, Base58-encoded string.

```kotlin
// A strong password derived from user credentials or the system Keystore/Keychain
val storagePassword = "my-super-secure-storage-password"

// --- Saving the Session ---

// Export the session state into an encrypted string
val serializedSession = aliceSession.exportToEncryptedString(storagePassword).getOrThrow()

// Save `serializedSession` to your local database (e.g., SharedPreferences, Room, CoreData)
println("Saved session: $serializedSession")


// --- Restoring the Session (After Restart) ---

// Load the serialized string from your local database
val loadedSessionString = "..." // from database

// Import the session back
val restoredAliceSession = DoubleRatchetSession.importFromEncryptedString(
    data = loadedSessionString,
    password = storagePassword
).getOrThrow()

// The restored session is ready to encrypt and decrypt messages right where it left off
val newMessage = restoredAliceSession.encryptToEncodedString("I'm back!".encodeToByteArray()).getOrThrow()
```
