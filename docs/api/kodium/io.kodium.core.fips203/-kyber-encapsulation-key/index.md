//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[KyberEncapsulationKey](index.md)

# KyberEncapsulationKey

[common]\
class [KyberEncapsulationKey](index.md) : KyberKEMKey

A class for ML-KEM Encapsulation Keys.

This class contains the raw bytes of the Decryption Key.

#### Author

Ron Lauren Hombre

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [fullBytes](full-bytes.md) | [common]<br>val [fullBytes](full-bytes.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>A copy of the Encapsulation Key in bytes. |
| [key](key.md) | [common]<br>open override val [key](key.md): [KyberEncryptionKey](../-kyber-encryption-key/index.md)<br>The [KyberEncryptionKey](../-kyber-encryption-key/index.md). |

## Functions

| Name | Summary |
|---|---|
| [copy](copy.md) | [common]<br>fun [copy](copy.md)(): [KyberEncapsulationKey](index.md)<br>Create an independent deep copy from an untrusted source. |
| [encapsulate](encapsulate.md) | [common]<br>fun [encapsulate](encapsulate.md)(randomProvider: [RandomProvider](../-random-provider/index.md) = DefaultRandomProvider): [KyberEncapsulationResult](../-kyber-encapsulation-result/index.md)<br>Encapsulates this [KyberEncapsulationKey](index.md) into a [KyberCipherText](../-kyber-cipher-text/index.md) and generates a Shared Secret Key using the DefaultRandomProvider. |
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)<br>Deep equality check. |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
