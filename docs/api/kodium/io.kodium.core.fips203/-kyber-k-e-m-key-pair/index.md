//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[KyberKEMKeyPair](index.md)

# KyberKEMKeyPair

[common]\
class [KyberKEMKeyPair](index.md)(val encapsulationKey: [KyberEncapsulationKey](../-kyber-encapsulation-key/index.md), val decapsulationKey: [KyberDecapsulationKey](../-kyber-decapsulation-key/index.md))

A class for ML-KEM Encapsulation and Decapsulation Key Pairs.

This class contains the Encapsulation and Decapsulation Key.

#### Author

Ron Lauren Hombre

## Constructors

| | |
|---|---|
| [KyberKEMKeyPair](-kyber-k-e-m-key-pair.md) | [common]<br>constructor(encapsulationKey: [KyberEncapsulationKey](../-kyber-encapsulation-key/index.md), decapsulationKey: [KyberDecapsulationKey](../-kyber-decapsulation-key/index.md))<br>Stores the Encapsulation Key and the Decapsulation Key as a pair. |

## Properties

| Name | Summary |
|---|---|
| [decapsulationKey](decapsulation-key.md) | [common]<br>val [decapsulationKey](decapsulation-key.md): [KyberDecapsulationKey](../-kyber-decapsulation-key/index.md)<br>The [KyberDecapsulationKey](../-kyber-decapsulation-key/index.md). |
| [encapsulationKey](encapsulation-key.md) | [common]<br>val [encapsulationKey](encapsulation-key.md): [KyberEncapsulationKey](../-kyber-encapsulation-key/index.md)<br>The [KyberEncapsulationKey](../-kyber-encapsulation-key/index.md). |

## Functions

| Name | Summary |
|---|---|
| [copy](copy.md) | [common]<br>fun [copy](copy.md)(): [KyberKEMKeyPair](index.md)<br>Create an independent deep copy from an untrusted source. |
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)<br>Deep equality check. |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
