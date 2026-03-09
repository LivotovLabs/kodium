//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[KyberDecapsulationKey](index.md)

# KyberDecapsulationKey

[common]\
class [KyberDecapsulationKey](index.md) : KyberKEMKey

A class for ML-KEM Decapsulation Keys.

This class contains the raw bytes of the Decapsulation Key.

#### Author

Ron Lauren Hombre

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [encryptionKey](encryption-key.md) | [common]<br>val [encryptionKey](encryption-key.md): [KyberEncryptionKey](../-kyber-encryption-key/index.md)<br>The [KyberEncryptionKey](../-kyber-encryption-key/index.md). |
| [fullBytes](full-bytes.md) | [common]<br>val [fullBytes](full-bytes.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>A copy of the Decapsulation Key in bytes. |
| [key](key.md) | [common]<br>open override val [key](key.md): [KyberDecryptionKey](../-kyber-decryption-key/index.md)<br>The [KyberDecryptionKey](../-kyber-decryption-key/index.md). |
| [parameter](parameter.md) | [common]<br>val [parameter](parameter.md): [KyberParameter](../-kyber-parameter/index.md)<br>The [KyberParameter](../-kyber-parameter/index.md) associated with this [KyberDecapsulationKey](index.md). |

## Functions

| Name | Summary |
|---|---|
| [copy](copy.md) | [common]<br>fun [copy](copy.md)(): [KyberDecapsulationKey](index.md)<br>Create an independent deep copy from an untrusted source. |
| [decapsulate](decapsulate.md) | [common]<br>fun [decapsulate](decapsulate.md)(cipherText: [KyberCipherText](../-kyber-cipher-text/index.md)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Decapsulates a [KyberCipherText](../-kyber-cipher-text/index.md) and recovers the Shared Secret Key. |
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)<br>Deep equality check. |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
