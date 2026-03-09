//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[PQXDH](../index.md)/[PQSharedSecret](index.md)

# PQSharedSecret

[common]\
data class [PQSharedSecret](index.md)(val masterSecret: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val encapsulationPayload: [PQXDH.PQInitiatorPayload](../-p-q-initiator-payload/index.md))

The resulting shared secret and the payload to send to the responder.

## Constructors

| | |
|---|---|
| [PQSharedSecret](-p-q-shared-secret.md) | [common]<br>constructor(masterSecret: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), encapsulationPayload: [PQXDH.PQInitiatorPayload](../-p-q-initiator-payload/index.md)) |

## Properties

| Name | Summary |
|---|---|
| [encapsulationPayload](encapsulation-payload.md) | [common]<br>val [encapsulationPayload](encapsulation-payload.md): [PQXDH.PQInitiatorPayload](../-p-q-initiator-payload/index.md) |
| [masterSecret](master-secret.md) | [common]<br>val [masterSecret](master-secret.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
