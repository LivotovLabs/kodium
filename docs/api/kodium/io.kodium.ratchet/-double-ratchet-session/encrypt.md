//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[DoubleRatchetSession](index.md)/[encrypt](encrypt.md)

# encrypt

[common]\
fun [encrypt](encrypt.md)(plaintext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[RatchetMessage](../-ratchet-message/index.md)&gt;

Encrypts a plaintext message using the current symmetric sending chain.

#### Return

A `Result` containing the structured [RatchetMessage](../-ratchet-message/index.md) object.

#### Parameters

common

| | |
|---|---|
| plaintext | The raw byte array message to encrypt. |
| associatedData | Optional Associated Data to authenticate alongside the message (e.g., protocol version). |
