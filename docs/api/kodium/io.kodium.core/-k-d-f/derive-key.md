//[kodium](../../../index.md)/[io.kodium.core](../index.md)/[KDF](index.md)/[deriveKey](derive-key.md)

# deriveKey

[common]\
fun [deriveKey](derive-key.md)(password: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), salt: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), iterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), keyLengthBytes: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)

Derives a key of a specified length from a password and salt using PBKDF2.

#### Return

The derived key as a byte array.

#### Parameters

common

| | |
|---|---|
| password | The password. |
| salt | The salt. |
| iterations | The number of iterations to perform. |
| keyLengthBytes | The desired length of the derived key in bytes. |
