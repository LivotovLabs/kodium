//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[KyberPKEKeyPair](index.md)

# KyberPKEKeyPair

[common]\
class [KyberPKEKeyPair](index.md)

A class for K-PKE Encryption and Decryption Key Pairs.

This class contains the Encryption and Decryption Key.

#### Author

Ron Lauren Hombre

## Properties

| Name | Summary |
|---|---|
| [decryptionKey](decryption-key.md) | [common]<br>val [decryptionKey](decryption-key.md): [KyberDecryptionKey](../-kyber-decryption-key/index.md)<br>The [KyberDecryptionKey](../-kyber-decryption-key/index.md). |
| [encryptionKey](encryption-key.md) | [common]<br>val [encryptionKey](encryption-key.md): [KyberEncryptionKey](../-kyber-encryption-key/index.md)<br>The [KyberEncryptionKey](../-kyber-encryption-key/index.md). |

## Functions

| Name | Summary |
|---|---|
| [copy](copy.md) | [common]<br>fun [copy](copy.md)(): [KyberPKEKeyPair](index.md)<br>Create an independent deep copy from an untrusted source. |
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)<br>Deep equality check. |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
