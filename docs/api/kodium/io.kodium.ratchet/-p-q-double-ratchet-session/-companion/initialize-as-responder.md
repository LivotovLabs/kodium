//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[PQDoubleRatchetSession](../index.md)/[Companion](index.md)/[initializeAsResponder](initialize-as-responder.md)

# initializeAsResponder

[common]\
fun [initializeAsResponder](initialize-as-responder.md)(sharedSecret: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), ourPqcPrivateKey: [KodiumPqcPrivateKey](../../../io.kodium/-kodium-pqc-private-key/index.md), initiatorPqcPublicKey: [KodiumPqcPublicKey](../../../io.kodium/-kodium-pqc-public-key/index.md), applicationInfo: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = DEFAULT_INFO_KDF_RK, maxSkippedMessages: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = DEFAULT_MAX_SKIPPED_MESSAGES): [PQDoubleRatchetSession](../index.md)

Initializes a new PQ Double Ratchet session for the Responder.

#### Parameters

common

| | |
|---|---|
| sharedSecret | The 32-byte master shared secret, derived from PQXDH. |
| ourPqcPrivateKey | The Responder's long-term PQC private key. |
| initiatorPqcPublicKey | The Initiator's long-term PQC public key. |
| applicationInfo | Optional context-binding string. |
| maxSkippedMessages | The maximum number of skipped message keys to store in memory (default 2000).     When exceeded, the oldest keys are evicted first (LRU behavior) preventing unbounded memory growth. |
