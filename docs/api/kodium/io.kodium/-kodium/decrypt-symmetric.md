//[kodium](../../../index.md)/[io.kodium](../index.md)/[Kodium](index.md)/[decryptSymmetric](decrypt-symmetric.md)

# decryptSymmetric

[common]\
fun [decryptSymmetric](decrypt-symmetric.md)(password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), cipher: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;

Decrypts a byte array that was encrypted using symmetric encryption with a provided password.

This function extracts the nonce from the beginning of the cipher, derives a secret key using HMAC-SHA256 with the password and nonce, and then attempts to decrypt the remaining data.

If the decryption is successful, the decrypted data is returned as a `Result.success`. If it fails (e.g., due to an incorrect password or tampered cipher data), a `Result.failure` is returned.

#### Return

A `Result` object containing the decrypted byte array on success, or an error on failure.

#### Parameters

common

| | |
|---|---|
| password | The password used to derive the secret key for decryption. |
| cipher | The encrypted data, which consists of the nonce followed by the encrypted message. |
