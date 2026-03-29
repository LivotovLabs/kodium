//[kodium](../../../../index.md)/[io.kodium.core](../../index.md)/[nacl](../index.md)/[Sign](index.md)/[verifyDetached](verify-detached.md)

# verifyDetached

[common]\
fun [verifyDetached](verify-detached.md)(signature: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), message: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), publicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)

Verifies a 64-byte detached signature against a message and public key. Returns true if valid, false otherwise.
