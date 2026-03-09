//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[DoubleRatchetSession](../index.md)/[Companion](index.md)/[initializeAsResponder](initialize-as-responder.md)

# initializeAsResponder

[common]\
fun [initializeAsResponder](initialize-as-responder.md)(sharedSecret: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), responderRatchetKeypair: [KodiumPrivateKey](../../../io.kodium/-kodium-private-key/index.md), applicationInfo: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = DEFAULT_INFO_KDF_RK, maxSkippedMessages: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = DEFAULT_MAX_SKIPPED_MESSAGES): [DoubleRatchetSession](../index.md)

Initializes a new Double Ratchet session for the Responder (the party who receives the first message).

#### Return

A newly initialized [DoubleRatchetSession](../index.md) ready to decrypt incoming messages.

#### Parameters

common

| | |
|---|---|
| sharedSecret | The 32-byte master shared secret, typically derived from an X3DH key agreement. |
| responderRatchetKeypair | The Responder's ratchet keypair (often their Signed PreKey pair). |
| applicationInfo | Optional context-binding string. MUST match the string used by the Initiator     to prevent cross-protocol attacks. |
| maxSkippedMessages | The maximum number of skipped message keys to store in memory (default 2000).     When exceeded, the oldest keys are evicted first (LRU behavior) preventing unbounded memory growth. |
