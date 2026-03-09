//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[PQXDH](index.md)/[calculateSecretAsInitiator](calculate-secret-as-initiator.md)

# calculateSecretAsInitiator

[common]\
fun [calculateSecretAsInitiator](calculate-secret-as-initiator.md)(initiatorIdentityKey: [KodiumPrivateKey](../../io.kodium/-kodium-private-key/index.md), initiatorPqcKey: [KodiumPqcPrivateKey](../../io.kodium/-kodium-pqc-private-key/index.md), responderBundle: [PQXDH.PublicBundle](-public-bundle/index.md), info: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = INFO_PQXDH): [PQXDH.PQSharedSecret](-p-q-shared-secret/index.md)

Calculates the shared secret as the Initiator (the party starting the conversation).

#### Return

The [PQSharedSecret](-p-q-shared-secret/index.md) containing the master secret and the payload for the responder.

#### Parameters

common

| | |
|---|---|
| initiatorIdentityKey | Initiator's long-term classical identity private key. |
| initiatorPqcKey | Initiator's hybrid private key (passed to the payload for immediate replies). |
| responderBundle | Responder's public bundle. |
| info | Optional application-specific information for key derivation. |
