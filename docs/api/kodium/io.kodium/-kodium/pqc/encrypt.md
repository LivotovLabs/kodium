//[kodium](../../../../index.md)/[io.kodium](../../index.md)/[Kodium](../index.md)/[pqc](index.md)/[encrypt](encrypt.md)

# encrypt

[common]\
fun [encrypt](encrypt.md)(mySecretKey: [KodiumPqcPrivateKey](../../-kodium-pqc-private-key/index.md), theirPublicKey: [KodiumPqcPublicKey](../../-kodium-pqc-public-key/index.md), data: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;

Encrypts data using a hybrid approach (X25519 + ML-KEM).

**WARNING: Lack of Forward Secrecy.** This method uses static keys for encryption. If either the sender's or receiver's long-term key is compromised, all past messages encrypted between them can be decrypted. For continuous, secure communication with forward secrecy and break-in recovery, use [io.kodium.ratchet.PQDoubleRatchetSession](../../../io.kodium.ratchet/-p-q-double-ratchet-session/index.md) instead.

#### Return

A Result containing the encrypted data.

#### Parameters

common

| | |
|---|---|
| mySecretKey | The sender's hybrid private key. |
| theirPublicKey | The recipient's hybrid public key. |
| data | The data to be encrypted. |
