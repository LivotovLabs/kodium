//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[X3DH](index.md)

# X3DH

[common]\
object [X3DH](index.md)

X3DH (Extended Triple Diffie-Hellman) Key Agreement Protocol.

## Types

| Name | Summary |
|---|---|
| [PublicBundle](-public-bundle/index.md) | [common]<br>data class [PublicBundle](-public-bundle/index.md)(val identityKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val signedPreKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val oneTimePreKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)? = null)<br>Responder's public bundle for X3DH. |

## Functions

| Name | Summary |
|---|---|
| [calculateSecretAsInitiator](calculate-secret-as-initiator.md) | [common]<br>fun [calculateSecretAsInitiator](calculate-secret-as-initiator.md)(initiatorIdentityKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md), initiatorEphemeralKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md), responderBundle: [X3DH.PublicBundle](-public-bundle/index.md), info: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = INFO_X3DH): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Calculates the shared secret as the Initiator (the party starting the conversation). |
| [calculateSecretAsResponder](calculate-secret-as-responder.md) | [common]<br>fun [calculateSecretAsResponder](calculate-secret-as-responder.md)(responderIdentityKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md), responderSignedPreKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md), responderOneTimePreKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md)?, initiatorIdentityKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), initiatorEphemeralKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), info: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = INFO_X3DH): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Calculates the shared secret as the Responder (the party receiving the initial message). |
