//[kodium](../../../../index.md)/[io.kodium.core](../../index.md)/[nacl](../index.md)/[SecretBox](index.md)

# SecretBox

[common]\
object [SecretBox](index.md)

Authenticated Symmetric Encryption (secretbox)

## Properties

| Name | Summary |
|---|---|
| [KeySize](-key-size.md) | [common]<br>const val [KeySize](-key-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 32 |
| [MacSize](-mac-size.md) | [common]<br>const val [MacSize](-mac-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 16 |
| [NonceSize](-nonce-size.md) | [common]<br>const val [NonceSize](-nonce-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 24 |

## Functions

| Name | Summary |
|---|---|
| [open](open.md) | [common]<br>fun [open](open.md)(box: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), nonce: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), key: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)?<br>Verifies and decrypts a ciphertext. |
| [seal](seal.md) | [common]<br>fun [seal](seal.md)(message: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), nonce: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), key: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Encrypts and authenticates a message using a secret key and a nonce. |
