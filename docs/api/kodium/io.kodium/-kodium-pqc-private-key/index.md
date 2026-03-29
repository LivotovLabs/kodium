//[kodium](../../../index.md)/[io.kodium](../index.md)/[KodiumPqcPrivateKey](index.md)

# KodiumPqcPrivateKey

[common]\
class [KodiumPqcPrivateKey](index.md)

Represents a Hybrid Post-Quantum Private Key.

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [classicalSecretKey](classical-secret-key.md) | [common]<br>val [classicalSecretKey](classical-secret-key.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) |
| [pqcSecretKey](pqc-secret-key.md) | [common]<br>val [pqcSecretKey](pqc-secret-key.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [exportToEncryptedString](export-to-encrypted-string.md) | [common]<br>fun [exportToEncryptedString](export-to-encrypted-string.md)(password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt; |
| [getPublicKey](get-public-key.md) | [common]<br>fun [getPublicKey](get-public-key.md)(): [KodiumPqcPublicKey](../-kodium-pqc-public-key/index.md) |
| [getSignPublicKey](get-sign-public-key.md) | [common]<br>fun [getSignPublicKey](get-sign-public-key.md)(): [KodiumPqcPublicKey](../-kodium-pqc-public-key/index.md)<br>Returns the hybrid Post-Quantum public key component of this key pair that must be used for signature verification. Note: Currently only the classical Ed25519 component is used for signatures. |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
