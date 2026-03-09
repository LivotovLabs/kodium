//[kodium](../../../index.md)/[io.kodium](../index.md)/[KodiumPublicKey](index.md)

# KodiumPublicKey

[common]\
data class [KodiumPublicKey](index.md)(val publicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html))

Represents a public key used in cryptographic operations within the KMP CryptoKit. This class facilitates the handling of public keys, including importing from and exporting to encoded formats with checksum validation.

## Constructors

| | |
|---|---|
| [KodiumPublicKey](-kodium-public-key.md) | [common]<br>constructor(publicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [publicKey](public-key.md) | [common]<br>val [publicKey](public-key.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>The byte array representing the raw public key data. |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [exportToEncodedString](export-to-encoded-string.md) | [common]<br>fun [exportToEncodedString](export-to-encoded-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Exports the raw public key material to a Base58-encoded string with a checksum appended. |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
