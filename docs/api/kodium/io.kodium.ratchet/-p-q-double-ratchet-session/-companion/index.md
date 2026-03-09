//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[PQDoubleRatchetSession](../index.md)/[Companion](index.md)

# Companion

[common]\
object [Companion](index.md)

## Properties

| Name | Summary |
|---|---|
| [DEFAULT_MAX_SKIPPED_MESSAGES](-d-e-f-a-u-l-t_-m-a-x_-s-k-i-p-p-e-d_-m-e-s-s-a-g-e-s.md) | [common]<br>const val [DEFAULT_MAX_SKIPPED_MESSAGES](-d-e-f-a-u-l-t_-m-a-x_-s-k-i-p-p-e-d_-m-e-s-s-a-g-e-s.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 2000 |

## Functions

| Name | Summary |
|---|---|
| [importFromEncryptedString](import-from-encrypted-string.md) | [common]<br>fun [importFromEncryptedString](import-from-encrypted-string.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = io.kodium.Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[PQDoubleRatchetSession](../index.md)&gt;<br>Initializes a new PQ Double Ratchet session for the Initiator using deterministic KEM encapsulation. |
| [initializeAsInitiator](initialize-as-initiator.md) | [common]<br>fun [initializeAsInitiator](initialize-as-initiator.md)(sharedSecret: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), responderPqcPublicKey: [KodiumPqcPublicKey](../../../io.kodium/-kodium-pqc-public-key/index.md), ourPqcPrivateKey: [KodiumPqcPrivateKey](../../../io.kodium/-kodium-pqc-private-key/index.md), applicationInfo: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = DEFAULT_INFO_KDF_RK, maxSkippedMessages: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = DEFAULT_MAX_SKIPPED_MESSAGES): [PQDoubleRatchetSession](../index.md)<br>Initializes a new PQ Double Ratchet session for the Initiator. |
| [initializeAsResponder](initialize-as-responder.md) | [common]<br>fun [initializeAsResponder](initialize-as-responder.md)(sharedSecret: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), ourPqcPrivateKey: [KodiumPqcPrivateKey](../../../io.kodium/-kodium-pqc-private-key/index.md), initiatorPqcPublicKey: [KodiumPqcPublicKey](../../../io.kodium/-kodium-pqc-public-key/index.md), applicationInfo: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = DEFAULT_INFO_KDF_RK, maxSkippedMessages: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = DEFAULT_MAX_SKIPPED_MESSAGES): [PQDoubleRatchetSession](../index.md)<br>Initializes a new PQ Double Ratchet session for the Responder. |
