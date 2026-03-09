//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[PQXDH](index.md)/[calculateSecretAsResponder](calculate-secret-as-responder.md)

# calculateSecretAsResponder

[common]\
fun [calculateSecretAsResponder](calculate-secret-as-responder.md)(responderIdentityKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md), responderPqcKey: [KodiumPqcPrivateKey](../../io.kodium/-kodium-pqc-private-key/index.md), initiatorPayload: [PQXDH.PQInitiatorPayload](-p-q-initiator-payload/index.md), responderOneTimePreKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md)? = null, info: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = INFO_PQXDH): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)

Calculates the shared secret as the Responder (the party receiving the initial message).

#### Return

The 32-byte master secret.

#### Parameters

common

| | |
|---|---|
| responderIdentityKey | Responder's long-term classical identity private key. |
| responderPqcKey | Responder's hybrid private key. |
| initiatorPayload | The payload received from the initiator. |
| responderOneTimePreKey | Responder's optional one-time pre-key private key. |
| info | Optional application-specific information for key derivation. |
