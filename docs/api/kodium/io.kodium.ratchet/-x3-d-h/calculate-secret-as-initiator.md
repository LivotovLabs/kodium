//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[X3DH](index.md)/[calculateSecretAsInitiator](calculate-secret-as-initiator.md)

# calculateSecretAsInitiator

[common]\
fun [calculateSecretAsInitiator](calculate-secret-as-initiator.md)(initiatorIdentityKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md), initiatorEphemeralKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md), responderBundle: [X3DH.PublicBundle](-public-bundle/index.md), info: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = INFO_X3DH): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)

Calculates the shared secret as the Initiator (the party starting the conversation).

#### Return

The 32-byte shared secret (SK).

#### Parameters

common

| | |
|---|---|
| initiatorIdentityKey | Initiator's long-term identity private key. |
| initiatorEphemeralKey | Initiator's generated ephemeral private key. |
| responderBundle | Responder's public bundle. |
| info | Optional application-specific information for key derivation. |
