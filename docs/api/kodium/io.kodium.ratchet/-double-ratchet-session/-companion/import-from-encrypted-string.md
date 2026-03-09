//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[DoubleRatchetSession](../index.md)/[Companion](index.md)/[importFromEncryptedString](import-from-encrypted-string.md)

# importFromEncryptedString

[common]\
fun [importFromEncryptedString](import-from-encrypted-string.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = io.kodium.Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[DoubleRatchetSession](../index.md)&gt;

Safely imports a previously saved Double Ratchet Session from an encrypted, Base58-encoded string.

#### Return

A `Result` containing the restored [DoubleRatchetSession](../index.md), or an error if decryption/parsing fails.

#### Parameters

common

| | |
|---|---|
| data | The encrypted, Base58-encoded session state string. |
| password | The secret password used to encrypt the session state during export. |
