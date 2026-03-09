//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[DoubleRatchetSession](index.md)/[decrypt](decrypt.md)

# decrypt

[common]\
fun [decrypt](decrypt.md)(message: [RatchetMessage](../-ratchet-message/index.md), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;

Decrypts an incoming structured [RatchetMessage](../-ratchet-message/index.md), automatically updating the session's internal state.

This handles Diffie-Hellman ratchet steps, advances the receiving symmetric chain, and manages the storage and retrieval of out-of-order (skipped) message keys.

#### Return

A `Result` containing the decrypted plaintext byte array.

#### Parameters

common

| | |
|---|---|
| message | The parsed [RatchetMessage](../-ratchet-message/index.md) to decrypt. |
| associatedData | The exact Associated Data provided by the sender during encryption. |
