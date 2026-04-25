//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[DoubleRatchetSession](index.md)/[encryptToEncodedString](encrypt-to-encoded-string.md)

# encryptToEncodedString

[common]\
fun [encryptToEncodedString](encrypt-to-encoded-string.md)(plaintext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;

A convenience wrapper around [encrypt](encrypt.md) that serializes the resulting message into a secure, checksummed Base64 string suitable for immediate network transmission or storage.

#### Return

A `Result` containing the Base64-encoded string representation of the encrypted message.

#### Parameters

common

| | |
|---|---|
| plaintext | The raw byte array message to encrypt. |
| associatedData | Optional Associated Data to authenticate. |
