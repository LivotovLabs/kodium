//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[KyberEncryptionKey](index.md)

# KyberEncryptionKey

[common]\
class [KyberEncryptionKey](index.md) : KyberPKEKey

A class for ML-KEM Encryption Keys.

This class contains the raw bytes of the Encryption Key and the accompanying NTT Seed.

#### Author

Ron Lauren Hombre

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [fullBytes](full-bytes.md) | [common]<br>val [fullBytes](full-bytes.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>A copy of the Encryption Key in bytes. |
| [parameter](parameter.md) | [common]<br>open override val [parameter](parameter.md): [KyberParameter](../-kyber-parameter/index.md)<br>The [KyberParameter](../-kyber-parameter/index.md) associated with this [KyberEncryptionKey](index.md). |

## Functions

| Name | Summary |
|---|---|
| [copy](copy.md) | [common]<br>fun [copy](copy.md)(): [KyberEncryptionKey](index.md)<br>Create an independent deep copy from an untrusted source. |
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)<br>Deep equality check. |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
