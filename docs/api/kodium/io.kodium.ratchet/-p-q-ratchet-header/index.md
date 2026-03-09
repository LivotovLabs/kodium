//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[PQRatchetHeader](index.md)

# PQRatchetHeader

[common]\
data class [PQRatchetHeader](index.md)(val dh: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val kemCiphertext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val pn: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html))

Represents the plaintext header attached to every message sent within a Post-Quantum Double Ratchet session.

Unlike a classical RatchetHeader (which is 40 bytes), the PQRatchetHeader is significantly larger (typically ~1,128 bytes) because it must carry the ML-KEM ciphertext required to perform the quantum-resistant asymmetric ratchet step.

## Constructors

| | |
|---|---|
| [PQRatchetHeader](-p-q-ratchet-header.md) | [common]<br>constructor(dh: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), kemCiphertext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), pn: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [dh](dh.md) | [common]<br>val [dh](dh.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>The sender's current ephemeral public Curve25519 ratchet key for the DH ratchet step. |
| [kemCiphertext](kem-ciphertext.md) | [common]<br>val [kemCiphertext](kem-ciphertext.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>The ML-KEM ciphertext encapsulated to the recipient's long-term PQC public key. |
| [n](n.md) | [common]<br>val [n](n.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>The sequential message number within the current sending chain. |
| [pn](pn.md) | [common]<br>val [pn](pn.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>The length of the previous sending chain (used to calculate how many messages to skip). |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
| [serialize](serialize.md) | [common]<br>fun [serialize](serialize.md)(): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Serializes the header into a byte array suitable for transmission and for inclusion as Associated Data (AD) during AEAD encryption. |
