//[kodium](../../../index.md)/[io.kodium.core](../index.md)/[nacl](index.md)

# nacl

[common]\
object [nacl](index.md)

Provides a high-level, safe, and convenient API for the TweetNaCl cryptographic library. This object handles all necessary padding, buffer management, and length checks, wrapping the low-level functions from NaClLowLevel.

## Types

| Name | Summary |
|---|---|
| [Box](-box/index.md) | [common]<br>object [Box](-box/index.md)<br>Authenticated Public-Key Encryption (box) |
| [SecretBox](-secret-box/index.md) | [common]<br>object [SecretBox](-secret-box/index.md)<br>Authenticated Symmetric Encryption (secretbox) |
| [Sign](-sign/index.md) | [common]<br>object [Sign](-sign/index.md)<br>Ed25519 Digital Signatures (Detached) |

## Functions

| Name | Summary |
|---|---|
| [randomBytes](random-bytes.md) | [common]<br>fun [randomBytes](random-bytes.md)(size: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Generates a specified number of secure random bytes. |
