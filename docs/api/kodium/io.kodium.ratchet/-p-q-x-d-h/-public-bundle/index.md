//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[PQXDH](../index.md)/[PublicBundle](index.md)

# PublicBundle

[common]\
data class [PublicBundle](index.md)(val identityKey: [KodiumPublicKey](../../../io.kodium/-kodium-public-key/index.md), val pqcKey: [KodiumPqcPublicKey](../../../io.kodium/-kodium-pqc-public-key/index.md), val oneTimePreKey: [KodiumPublicKey](../../../io.kodium/-kodium-public-key/index.md)? = null)

Responder's public bundle for PQXDH.

## Constructors

| | |
|---|---|
| [PublicBundle](-public-bundle.md) | [common]<br>constructor(identityKey: [KodiumPublicKey](../../../io.kodium/-kodium-public-key/index.md), pqcKey: [KodiumPqcPublicKey](../../../io.kodium/-kodium-pqc-public-key/index.md), oneTimePreKey: [KodiumPublicKey](../../../io.kodium/-kodium-public-key/index.md)? = null) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [identityKey](identity-key.md) | [common]<br>val [identityKey](identity-key.md): [KodiumPublicKey](../../../io.kodium/-kodium-public-key/index.md)<br>Responder's long-term classical identity public key. |
| [oneTimePreKey](one-time-pre-key.md) | [common]<br>val [oneTimePreKey](one-time-pre-key.md): [KodiumPublicKey](../../../io.kodium/-kodium-public-key/index.md)? = null<br>Responder's classical one-time pre-key public key (optional). |
| [pqcKey](pqc-key.md) | [common]<br>val [pqcKey](pqc-key.md): [KodiumPqcPublicKey](../../../io.kodium/-kodium-pqc-public-key/index.md)<br>Responder's long-term hybrid public key. The classical portion acts as the Signed PreKey. |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [exportToEncodedString](export-to-encoded-string.md) | [common]<br>fun [exportToEncodedString](export-to-encoded-string.md)(): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;<br>Exports this PublicBundle to a Base58-encoded string with a checksum. |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
