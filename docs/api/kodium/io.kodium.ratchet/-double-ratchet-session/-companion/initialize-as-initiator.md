//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[DoubleRatchetSession](../index.md)/[Companion](index.md)/[initializeAsInitiator](initialize-as-initiator.md)

# initializeAsInitiator

[common]\
fun [initializeAsInitiator](initialize-as-initiator.md)(sharedSecret: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), responderRatchetKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), applicationInfo: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = DEFAULT_INFO_KDF_RK, maxSkippedMessages: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = DEFAULT_MAX_SKIPPED_MESSAGES): [DoubleRatchetSession](../index.md)

Initializes a new Double Ratchet session for the Initiator (the party who sends the first message).

#### Return

A newly initialized [DoubleRatchetSession](../index.md) ready to encrypt messages.

#### Parameters

common

| | |
|---|---|
| sharedSecret | The 32-byte master shared secret, typically derived from an X3DH key agreement. |
| responderRatchetKey | The Responder's public Curve25519 ratchet key (often their Signed PreKey). |
| applicationInfo | Optional context-binding string. MUST match the string used by the Responder     to prevent cross-protocol attacks. |
| maxSkippedMessages | The maximum number of skipped message keys to store in memory (default 2000).     When exceeded, the oldest keys are evicted first (LRU behavior) preventing unbounded memory growth. |
