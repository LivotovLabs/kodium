//[kodium](../../../index.md)/[io.kodium](../index.md)/[KodiumPqcPublicKey](index.md)

# KodiumPqcPublicKey

[common]\
data class [KodiumPqcPublicKey](index.md)(val classicalPublicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val pqcPublicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html))

Represents a Hybrid Post-Quantum Public Key.

## Constructors

| | |
|---|---|
| [KodiumPqcPublicKey](-kodium-pqc-public-key.md) | [common]<br>constructor(classicalPublicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), pqcPublicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [classicalPublicKey](classical-public-key.md) | [common]<br>val [classicalPublicKey](classical-public-key.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) |
| [pqcPublicKey](pqc-public-key.md) | [common]<br>val [pqcPublicKey](pqc-public-key.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [exportToEncodedString](export-to-encoded-string.md) | [common]<br>fun [exportToEncodedString](export-to-encoded-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
