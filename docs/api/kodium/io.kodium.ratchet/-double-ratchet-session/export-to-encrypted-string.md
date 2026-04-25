//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[DoubleRatchetSession](index.md)/[exportToEncryptedString](export-to-encrypted-string.md)

# exportToEncryptedString

[common]\
fun [exportToEncryptedString](export-to-encrypted-string.md)(password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = io.kodium.Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;

Exports the entire internal state of the current session to a securely encrypted, Base64-encoded string.

This method is intended to be used to persist the session state to local storage (e.g., a database) between application restarts. The provided password is used to encrypt the blob using Kodium's symmetric encryption layer (XSalsa20 + Poly1305 via PBKDF2).

#### Return

A `Result` containing the Base64-encoded string on success.

#### Parameters

common

| | |
|---|---|
| password | A strong, secret password to encrypt the exported state. |
