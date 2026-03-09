//[kodium](../../../index.md)/[io.kodium](../index.md)/[Kodium](index.md)/[encrypt](encrypt.md)

# encrypt

[common]\
fun [encrypt](encrypt.md)(mySecretKey: [KodiumPrivateKey](../-kodium-private-key/index.md), theirPublicKey: [KodiumPublicKey](../-kodium-public-key/index.md), data: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;

Encrypts data using the sender's private key and the receiver's public key. The encryption utilizes a secure combination of key pairs, a randomly generated nonce, and the NaCl library for cryptographic operations.

**WARNING: Lack of Forward Secrecy.** This method uses a static-static Diffie-Hellman key exchange. If either the sender's or receiver's long-term key is compromised, all past messages encrypted between them can be decrypted. For continuous, secure communication with forward secrecy and break-in recovery, use [io.kodium.ratchet.DoubleRatchetSession](../../io.kodium.ratchet/-double-ratchet-session/index.md) instead.

#### Return

A `Result` containing the encrypted data as a byte array on success, or an error on failure.

#### Parameters

common

| | |
|---|---|
| mySecretKey | The private key of the sender, used to compute the shared secret for encryption. |
| theirPublicKey | The public key of the receiver, used to compute the shared secret for encryption. |
| data | The plaintext data to be encrypted, provided as a byte array. |
