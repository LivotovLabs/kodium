//[kodium](../../../../index.md)/[io.kodium](../../index.md)/[Kodium](../index.md)/[pqc](index.md)/[decrypt](decrypt.md)

# decrypt

[common]\
fun [decrypt](decrypt.md)(mySecretKey: [KodiumPqcPrivateKey](../../-kodium-pqc-private-key/index.md), theirPublicKey: [KodiumPqcPublicKey](../../-kodium-pqc-public-key/index.md), cipher: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;

Decrypts data using a hybrid approach (X25519 + ML-KEM).

#### Return

A Result containing the decrypted data.

#### Parameters

common

| | |
|---|---|
| mySecretKey | The recipient's hybrid private key. |
| theirPublicKey | The sender's hybrid public key. |
| cipher | The encrypted data. |
