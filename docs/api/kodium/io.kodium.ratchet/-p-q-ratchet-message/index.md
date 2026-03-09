//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[PQRatchetMessage](index.md)

# PQRatchetMessage

[common]\
data class [PQRatchetMessage](index.md)(val header: [PQRatchetHeader](../-p-q-ratchet-header/index.md), val ciphertext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html))

A composite structure containing both the plaintext PQC Ratchet header and the encrypted payload.

## Constructors

| | |
|---|---|
| [PQRatchetMessage](-p-q-ratchet-message.md) | [common]<br>constructor(header: [PQRatchetHeader](../-p-q-ratchet-header/index.md), ciphertext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [ciphertext](ciphertext.md) | [common]<br>val [ciphertext](ciphertext.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>The AEAD encrypted message payload. |
| [header](header.md) | [common]<br>val [header](header.md): [PQRatchetHeader](../-p-q-ratchet-header/index.md)<br>The plaintext [PQRatchetHeader](../-p-q-ratchet-header/index.md) required for the recipient to process the message. |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
| [serialize](serialize.md) | [common]<br>fun [serialize](serialize.md)(): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Serializes the entire message (header + ciphertext) into a single byte array for network transmission. |
