//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[DoubleRatchetSession](../index.md)/[Companion](index.md)

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
| [importFromEncryptedString](import-from-encrypted-string.md) | [common]<br>fun [importFromEncryptedString](import-from-encrypted-string.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = io.kodium.Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[DoubleRatchetSession](../index.md)&gt;<br>Safely imports a previously saved Double Ratchet Session from an encrypted, Base58-encoded string. |
| [initializeAsInitiator](initialize-as-initiator.md) | [common]<br>fun [initializeAsInitiator](initialize-as-initiator.md)(sharedSecret: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), responderRatchetKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), applicationInfo: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = DEFAULT_INFO_KDF_RK, maxSkippedMessages: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = DEFAULT_MAX_SKIPPED_MESSAGES): [DoubleRatchetSession](../index.md)<br>Initializes a new Double Ratchet session for the Initiator (the party who sends the first message). |
| [initializeAsResponder](initialize-as-responder.md) | [common]<br>fun [initializeAsResponder](initialize-as-responder.md)(sharedSecret: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), responderRatchetKeypair: [KodiumPrivateKey](../../../io.kodium/-kodium-private-key/index.md), applicationInfo: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = DEFAULT_INFO_KDF_RK, maxSkippedMessages: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = DEFAULT_MAX_SKIPPED_MESSAGES): [DoubleRatchetSession](../index.md)<br>Initializes a new Double Ratchet session for the Responder (the party who receives the first message). |
