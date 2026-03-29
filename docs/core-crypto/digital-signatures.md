# Digital Signatures

Kodium provides high-speed, high-security digital signatures using Ed25519. These signatures are "detached", meaning the signature is separated from the message content (ideal for JWT-style Bearer tokens or standard API authentication flows).

Digital signatures prove that a message was created by a known sender (authenticity) and that the message was not altered in transit (integrity).

## Key Pairs

Kodium uses a unified seed strategy. When you generate a standard `KodiumPrivateKey` or a hybrid `KodiumPqcPrivateKey`, the same underlying secret seed can be used to perform X25519 Encryption (Box) or Ed25519 Digital Signatures. 

However, because X25519 and Ed25519 use different curve coordinates mathematically, they have different Public Keys. To verify a signature, you must provide the signer's **Signing Public Key**, not their encryption key.

## Creating a Detached Signature

You can generate a detached signature directly from a `KodiumPrivateKey`. The output is automatically Base58 encoded with a checksum for easy transmission.

```kotlin
import io.kodium.Kodium

// 1. The sender creates a message
val message = "Hello, this is a signed message!".encodeToByteArray()

// 2. The sender uses their private key to sign the message
val myPrivateKey = Kodium.generateKeyPair()
val signatureResult = Kodium.signDetachedToEncodedString(myPrivateKey, message)

val signatureBase58 = signatureResult.getOrThrow()
println("Signature: $signatureBase58")
```

## Verifying a Detached Signature

To verify a message, the recipient needs:
1. The exact same message bytes.
2. The Base58 encoded signature.
3. The sender's **Signing Public Key**.

```kotlin
import io.kodium.Kodium

// ... Assume the recipient receives the message, signatureBase58, and knows the sender's signing key

// 1. Obtain the sender's signing public key (this is critical!)
// The sender can export it via: myPrivateKey.getSignPublicKey().exportToEncodedString()
val theirSignPublicKey = myPrivateKey.getSignPublicKey() 

// 2. Verify the signature against the message
val isValid = Kodium.verifyDetachedFromEncodedString(
    theirPublicKey = theirSignPublicKey,
    data = message,
    signatureB58 = signatureBase58
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

// Verifying works identically, using the pqcKeyPair.getSignPublicKey()
val isValid = Kodium.pqc.verifyDetachedFromEncodedString(
    theirPublicKey = pqcKeyPair.getSignPublicKey(),
    data = message,
    signatureB58 = pqcSignatureResult.getOrThrow()
)
```

## Low-Level API

If you need raw, byte-level access without Base58 encoding, you can use the `nacl.Sign` wrapper directly:

```kotlin
import io.kodium.core.nacl

// Generate raw 32-byte Ed25519 public key and 64-byte secret key from a 32-byte seed
val (pk, sk) = nacl.Sign.keyPairFromSeed(seedBytes)

// Sign (Returns exactly 64 bytes)
val rawSignature = nacl.Sign.signDetached(message, sk)

// Verify (Returns Boolean)
val isValid = nacl.Sign.verifyDetached(rawSignature, message, pk)
```