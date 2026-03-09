//[kodium](../../../index.md)/[io.kodium](../index.md)/[Kodium](index.md)/[decrypt](decrypt.md)

# decrypt

[common]\
fun [decrypt](decrypt.md)(mySecretKey: [KodiumPrivateKey](../-kodium-private-key/index.md), theirPublicKey: [KodiumPublicKey](../-kodium-public-key/index.md), cipher: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;

Decrypts a cipher text using the receiver's private key and the sender's public key.

This function extracts the nonce and encrypted message from the provided cipher text, then attempts to decrypt the message using the provided keys. If the decryption fails, an error message will be returned.

#### Return

A `Result` object containing the decrypted message as a `ByteArray` on success,     or an error message on failure.

#### Parameters

common

| | |
|---|---|
| mySecretKey | The private key of the recipient used for decryption. |
| theirPublicKey | The public key of the sender used for decryption. |
| cipher | A byte array containing the cipher text to be decrypted. This includes the nonce and encrypted message. |
