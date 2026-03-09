//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[RatchetMessage](index.md)

# RatchetMessage

[common]\
data class [RatchetMessage](index.md)(val header: [RatchetHeader](../-ratchet-header/index.md), val ciphertext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html))

A composite structure containing both the plaintext Ratchet header and the encrypted payload.

## Constructors

| | |
|---|---|
| [RatchetMessage](-ratchet-message.md) | [common]<br>constructor(header: [RatchetHeader](../-ratchet-header/index.md), ciphertext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [ciphertext](ciphertext.md) | [common]<br>val [ciphertext](ciphertext.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>The AEAD encrypted message payload. |
| [header](header.md) | [common]<br>val [header](header.md): [RatchetHeader](../-ratchet-header/index.md)<br>The plaintext [RatchetHeader](../-ratchet-header/index.md) required for the recipient to process the message. |

## Functions

| Name | Summary |
|---|---|
| [serialize](serialize.md) | [common]<br>fun [serialize](serialize.md)(): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Serializes the entire message (header + ciphertext) into a single byte array for network transmission. |
