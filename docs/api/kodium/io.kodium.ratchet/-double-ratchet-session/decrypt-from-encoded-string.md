//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[DoubleRatchetSession](index.md)/[decryptFromEncodedString](decrypt-from-encoded-string.md)

# decryptFromEncodedString

[common]\
fun [decryptFromEncodedString](decrypt-from-encoded-string.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;

A convenience wrapper around [decrypt](decrypt.md) that accepts a secure, checksummed Base58 string representing the encrypted message (as produced by [encryptToEncodedString](encrypt-to-encoded-string.md)).

#### Return

A `Result` containing the decrypted plaintext byte array.

#### Parameters

common

| | |
|---|---|
| data | The Base58-encoded encrypted string. |
| associatedData | The exact Associated Data provided by the sender during encryption. |
