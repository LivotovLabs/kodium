//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[DoubleRatchetSession](index.md)

# DoubleRatchetSession

[common]\
class [DoubleRatchetSession](index.md)

The core implementation of the Double Ratchet Algorithm.

The Double Ratchet algorithm is used by two parties to exchange encrypted messages based on a shared secret key. It combines a cryptographic &quot;Diffie-Hellman ratchet&quot; with a &quot;symmetric-key ratchet&quot; to provide both forward secrecy (compromise of current keys does not compromise past messages) and break-in recovery (compromise of current keys does not compromise future messages).

Instances of this class are stateful and must be securely persisted between application restarts using [exportToEncryptedString](export-to-encrypted-string.md) and [importFromEncryptedString](-companion/import-from-encrypted-string.md).

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
| [decrypt](decrypt.md) | [common]<br>fun [decrypt](decrypt.md)(message: [RatchetMessage](../-ratchet-message/index.md), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;<br>Decrypts an incoming structured [RatchetMessage](../-ratchet-message/index.md), automatically updating the session's internal state. |
| [decryptFromEncodedString](decrypt-from-encoded-string.md) | [common]<br>fun [decryptFromEncodedString](decrypt-from-encoded-string.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;<br>A convenience wrapper around [decrypt](decrypt.md) that accepts a secure, checksummed Base64 string representing the encrypted message (as produced by [encryptToEncodedString](encrypt-to-encoded-string.md)). |
| [encrypt](encrypt.md) | [common]<br>fun [encrypt](encrypt.md)(plaintext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[RatchetMessage](../-ratchet-message/index.md)&gt;<br>Encrypts a plaintext message using the current symmetric sending chain. |
| [encryptToEncodedString](encrypt-to-encoded-string.md) | [common]<br>fun [encryptToEncodedString](encrypt-to-encoded-string.md)(plaintext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), associatedData: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) = ByteArray(0)): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;<br>A convenience wrapper around [encrypt](encrypt.md) that serializes the resulting message into a secure, checksummed Base64 string suitable for immediate network transmission or storage. |
| [exportToEncryptedString](export-to-encrypted-string.md) | [common]<br>fun [exportToEncryptedString](export-to-encrypted-string.md)(password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = io.kodium.Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;<br>Exports the entire internal state of the current session to a securely encrypted, Base64-encoded string. |
