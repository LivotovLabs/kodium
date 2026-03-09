//[kodium](../../../index.md)/[io.kodium](../index.md)/[KodiumPrivateKey](index.md)

# KodiumPrivateKey

[common]\
class [KodiumPrivateKey](index.md)

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [publicKey](public-key.md) | [common]<br>val [publicKey](public-key.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) |
| [secretKey](secret-key.md) | [common]<br>val [secretKey](secret-key.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [exportToEncryptedString](export-to-encrypted-string.md) | [common]<br>fun [exportToEncryptedString](export-to-encrypted-string.md)(password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;<br>Exports the private key to an encrypted, Base58-encoded string. A password MUST be provided. |
| [getPublicKey](get-public-key.md) | [common]<br>fun [getPublicKey](get-public-key.md)(): [KodiumPublicKey](../-kodium-public-key/index.md)<br>Returns the public key component of this key pair. |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
