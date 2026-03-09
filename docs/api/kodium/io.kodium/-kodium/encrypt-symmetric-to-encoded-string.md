//[kodium](../../../index.md)/[io.kodium](../index.md)/[Kodium](index.md)/[encryptSymmetricToEncodedString](encrypt-symmetric-to-encoded-string.md)

# encryptSymmetricToEncodedString

[common]\
fun [encryptSymmetricToEncodedString](encrypt-symmetric-to-encoded-string.md)(password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), data: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;

Encrypts the given data using a symmetric encryption algorithm and encodes the result as a Base58 string with a checksum.

The encryption process combines the given password and data, producing a secure encrypted output. The resulting encrypted byte array is then encoded into a Base58-encoded string with a checksum for safe storage or transmission.

#### Return

A `Result` object that contains the Base58-encoded string on successful encryption, or an error if the encryption fails.

#### Parameters

common

| | |
|---|---|
| password | The password used for encryption. It should be a secure string that the user must remember to decrypt the data. |
| data | The data to be encrypted, provided as a byte array. |
