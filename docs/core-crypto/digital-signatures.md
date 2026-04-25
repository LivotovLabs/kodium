# Digital Signatures

Kodium provides high-speed, high-security digital signatures using Ed25519. These signatures are "detached", meaning the signature is separated from the message content (ideal for JWT-style Bearer tokens or standard API authentication flows).

Digital signatures prove that a message was created by a known sender (authenticity) and that the message was not altered in transit (integrity).

## Key Pairs

Kodium uses a unified key strategy. When you generate a standard `KodiumPrivateKey` or a hybrid `KodiumPqcPrivateKey`, the `getPublicKey()` method returns a unified public key object. This object automatically contains both the X25519 Encryption key and the Ed25519 Digital Signature key. You simply pass this single unified public key object to any verification method.

## Creating a Detached Signature

You can generate a detached signature directly from a `KodiumPrivateKey`. The output is automatically Base64 encoded with a checksum for easy transmission.

```kotlin
import io.kodium.Kodium

// 1. The sender creates a message
val message = "Hello, this is a signed message!".encodeToByteArray()

// 2. The sender uses their private key to sign the message
val myPrivateKey = Kodium.generateKeyPair()
val signatureResult = Kodium.signDetachedToEncodedString(myPrivateKey, message)

val signatureBase64 = signatureResult.getOrThrow()
println("Signature: $signatureBase64")
```

## Verifying a Detached Signature

To verify a message, the recipient needs:
1. The exact same message bytes.
2. The Base64 encoded signature.
3. The sender's unified **Public Key**.

```kotlin
import io.kodium.Kodium

// ... Assume the recipient receives the message, signatureBase64, and knows the sender's public key

// 1. Obtain the sender's unified public key
// The sender can export it via: myPrivateKey.getPublicKey().exportToEncodedString()
val theirPublicKey = myPrivateKey.getPublicKey() 

// 2. Verify the signature against the message
val isValid = Kodium.verifyDetachedFromEncodedString(
    theirPublicKey = theirPublicKey,
    data = message,
    signatureBase64 = signatureBase64
)

if (isValid) {
    println("The signature is valid! The message is authentic and unaltered.")
} else {
    println("WARNING: Invalid signature. Do not trust the message.")
}
```

## Post-Quantum Signatures

*Note: While Kodium supports Post-Quantum encapsulation (ML-KEM) for encryption, Post-Quantum Digital Signatures (ML-DSA) are not yet integrated. When you call `Kodium.pqc.signDetachedToEncodedString`, Kodium falls back to using the highly secure classical Ed25519 component of your hybrid key pair.*

```kotlin
// Signing with a PQC Hybrid Key uses the classical Ed25519 component automatically
val pqcKeyPair = Kodium.pqc.generateKeyPair()
val pqcSignatureResult = Kodium.pqc.signDetachedToEncodedString(pqcKeyPair, message)

// Verifying works identically, using the pqcKeyPair.getPublicKey()
val isValid = Kodium.pqc.verifyDetachedFromEncodedString(
    theirPublicKey = pqcKeyPair.getPublicKey(),
    data = message,
    signatureBase64 = pqcSignatureResult.getOrThrow()
)
```

## Low-Level API

If you need raw, byte-level access without Base64 encoding, you can use the `nacl.Sign` wrapper directly:

```kotlin
import io.kodium.core.nacl

// Generate raw 32-byte Ed25519 public key and 64-byte secret key from a 32-byte seed
val (pk, sk) = nacl.Sign.keyPairFromSeed(seedBytes)

// Sign (Returns exactly 64 bytes)
val rawSignature = nacl.Sign.signDetached(message, sk)

// Verify (Returns Boolean)
val isValid = nacl.Sign.verifyDetached(rawSignature, message, pk)
```