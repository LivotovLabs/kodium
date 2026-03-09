//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[PQDoubleRatchetSession](index.md)

# PQDoubleRatchetSession

[common]\
class [PQDoubleRatchetSession](index.md)

The core implementation of the Post-Quantum Double Ratchet Algorithm.

This variant extends the standard Double Ratchet algorithm to support a KEM-based asymmetric ratchet using ML-KEM-768 alongside the standard X25519 DH ratchet.

Each ratchet step requires an ML-KEM encapsulation to the recipient's long-term PQC public key, which produces a ciphertext (~1KB) that must be attached to the message header.

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [maxSkippedMessages](max-skipped-messages.md) | [common]<br>val [maxSkippedMessages](max-skipped-messages.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [decrypt](decrypt.md) | [common]<br>fun [decrypt](decrypt.md)(message: [PQRatchetMessage](../-p-q-ratchet-message/index.md), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt; |
| [decryptFromEncodedString](decrypt-from-encoded-string.md) | [common]<br>fun [decryptFromEncodedString](decrypt-from-encoded-string.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt; |
| [encrypt](encrypt.md) | [common]<br>fun [encrypt](encrypt.md)(plaintext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[PQRatchetMessage](../-p-q-ratchet-message/index.md)&gt; |
| [encryptToEncodedString](encrypt-to-encoded-string.md) | [common]<br>fun [encryptToEncodedString](encrypt-to-encoded-string.md)(plaintext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt; |
| [exportToEncryptedString](export-to-encrypted-string.md) | [common]<br>fun [exportToEncryptedString](export-to-encrypted-string.md)(password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = io.kodium.Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt; |
