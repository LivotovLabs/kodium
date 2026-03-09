//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[X3DH](index.md)/[calculateSecretAsResponder](calculate-secret-as-responder.md)

# calculateSecretAsResponder

[common]\
fun [calculateSecretAsResponder](calculate-secret-as-responder.md)(responderIdentityKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md), responderSignedPreKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md), responderOneTimePreKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md)?, initiatorIdentityKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), initiatorEphemeralKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), info: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = INFO_X3DH): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)

Calculates the shared secret as the Responder (the party receiving the initial message).

#### Return

The 32-byte shared secret (SK).

#### Parameters

common

| | |
|---|---|
| responderIdentityKey | Responder's long-term identity private key. |
| responderSignedPreKey | Responder's signed pre-key private key. |
| responderOneTimePreKey | Responder's one-time pre-key private key (optional). |
| initiatorIdentityKey | Initiator's long-term identity public key. |
| initiatorEphemeralKey | Initiator's ephemeral public key. |
| info | Optional application-specific information for key derivation. |
