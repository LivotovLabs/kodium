//[kodium](../../../index.md)/[io.kodium](../index.md)/[Kodium](index.md)/[encryptToEncodedString](encrypt-to-encoded-string.md)

# encryptToEncodedString

[common]\
fun [encryptToEncodedString](encrypt-to-encoded-string.md)(mySecretKey: [KodiumPrivateKey](../-kodium-private-key/index.md), theirPublicKey: [KodiumPublicKey](../-kodium-public-key/index.md), data: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;

Encrypts the given data using the sender's private key and the receiver's public key, and then encodes the encrypted data to a Base58 string with a checksum.

**WARNING: Lack of Forward Secrecy.** This method uses a static-static Diffie-Hellman key exchange. If either the sender's or receiver's long-term key is compromised, all past messages encrypted between them can be decrypted. For continuous, secure communication with forward secrecy and break-in recovery, use [io.kodium.ratchet.DoubleRatchetSession](../../io.kodium.ratchet/-double-ratchet-session/index.md) instead.

This method performs encryption by generating a combined nonce and cipher from the data and keys provided, ensuring secure communication. The output is a Base58-encoded string for easy transmission and integrity verification.

#### Return

A `Result` containing the encrypted and encoded string on success, or an error on failure.

#### Parameters

common

| | |
|---|---|
| mySecretKey | The private key of the sender used for encryption. |
| theirPublicKey | The public key of the receiver used for encryption. |
| data | The data to be encrypted, provided as a byte array. |
