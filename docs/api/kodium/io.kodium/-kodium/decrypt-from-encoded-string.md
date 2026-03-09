//[kodium](../../../index.md)/[io.kodium](../index.md)/[Kodium](index.md)/[decryptFromEncodedString](decrypt-from-encoded-string.md)

# decryptFromEncodedString

[common]\
fun [decryptFromEncodedString](decrypt-from-encoded-string.md)(mySecretKey: [KodiumPrivateKey](../-kodium-private-key/index.md), theirPublicKey: [KodiumPublicKey](../-kodium-public-key/index.md), data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;

Decrypts an encoded string using the recipient's private key and the sender's public key.

This method takes a Base58-encoded string (with checksum) representing encrypted data, decodes it to a byte array, and decrypts it using a combination of the receiver's private key and the sender's public key.

#### Return

A `Result` object containing the decrypted data as a `ByteArray` on success,     or an error on failure.

#### Parameters

common

| | |
|---|---|
| mySecretKey | The private key of the recipient used for decryption. |
| theirPublicKey | The public key of the sender used for decryption. |
| data | The Base58-encoded string containing the encrypted data with a checksum. |
