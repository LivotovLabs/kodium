//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[X3DH](../index.md)/[PublicBundle](index.md)

# PublicBundle

[common]\
data class [PublicBundle](index.md)(val identityKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val signedPreKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val oneTimePreKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)? = null)

Responder's public bundle for X3DH.

## Constructors

| | |
|---|---|
| [PublicBundle](-public-bundle.md) | [common]<br>constructor(identityKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), signedPreKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), oneTimePreKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)? = null) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [identityKey](identity-key.md) | [common]<br>val [identityKey](identity-key.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Responder's long-term identity public key. |
| [oneTimePreKey](one-time-pre-key.md) | [common]<br>val [oneTimePreKey](one-time-pre-key.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)? = null<br>Responder's one-time pre-key public key (optional). |
| [signedPreKey](signed-pre-key.md) | [common]<br>val [signedPreKey](signed-pre-key.md): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Responder's signed pre-key public key. |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [exportToEncodedString](export-to-encoded-string.md) | [common]<br>fun [exportToEncodedString](export-to-encoded-string.md)(): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;<br>Exports this PublicBundle to a Base64-encoded string with a checksum. This string can be easily transmitted over a network or stored. |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
