//[kodium](../../../index.md)/[io.kodium.core](../index.md)/[KDF](index.md)

# KDF

[common]\
object [KDF](index.md)

A pure Kotlin implementation of the PBKDF2 (Password-Based Key Derivation Function 2) with HMAC-SHA256 as the pseudorandom function.

This object provides a standard, secure way to derive a strong cryptographic key from a user-provided password. It is designed to be computationally intensive to slow down brute-force and dictionary attacks on passwords.

## Functions

| Name | Summary |
|---|---|
| [deriveKey](derive-key.md) | [common]<br>fun [deriveKey](derive-key.md)(password: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), salt: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), iterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), keyLengthBytes: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Derives a key of a specified length from a password and salt using PBKDF2. |
