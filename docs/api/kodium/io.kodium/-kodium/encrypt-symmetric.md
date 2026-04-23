//[kodium](../../../index.md)/[io.kodium](../index.md)/[Kodium](index.md)/[encryptSymmetric](encrypt-symmetric.md)

# encryptSymmetric

[common]\
fun [encryptSymmetric](encrypt-symmetric.md)(password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), data: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;

Encrypts the given data using a symmetric encryption algorithm.

**Post-Quantum Security:** Symmetric encryption using 256-bit keys and XSalsa20 (as implemented here) is considered resistant to attacks by future quantum computers. Unlike asymmetric algorithms, Grover's algorithm only provides a square-root speedup for symmetric key searches, effectively meaning a 256-bit key remains as secure against a quantum computer as a 128-bit key is against a classical computer today.

This method generates a unique nonce for every encryption process, derives a cryptographic key using HMAC-SHA256, and encrypts the data with the derived key and the nonce using a secret box encryption mechanism.

#### Return

A `Result` object containing the encrypted byte array on success (nonce and encrypted message concatenated),     or an error in case the encryption process fails.

#### Parameters

common

| | |
|---|---|
| password | The password used to derive the cryptographic key. It should be a secure string. |
| data | The data to be encrypted, provided as a byte array. |
