//[kodium](../../../index.md)/[io.kodium](../index.md)/[Kodium](index.md)/[decryptSymmetricFromEncodedString](decrypt-symmetric-from-encoded-string.md)

# decryptSymmetricFromEncodedString

[common]\
fun [decryptSymmetricFromEncodedString](decrypt-symmetric-from-encoded-string.md)(password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;

Decrypts an encoded Base64 string that was encrypted using symmetric encryption.

This function first decodes the input string from Base64 with checksum verification. Then, it attempts to decrypt the decoded data using the provided password and the symmetric decryption method. The decryption result is returned as a success or an error.

#### Return

A `Result` object containing the decrypted data as a `ByteArray` on success,     or an error on failure.

#### Parameters

common

| | |
|---|---|
| password | The password used to decrypt the encoded string. |
| data | The Base64-encoded string containing the encrypted data with a checksum. |
