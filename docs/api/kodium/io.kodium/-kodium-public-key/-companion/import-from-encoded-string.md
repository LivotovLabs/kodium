//[kodium](../../../../index.md)/[io.kodium](../../index.md)/[KodiumPublicKey](../index.md)/[Companion](index.md)/[importFromEncodedString](import-from-encoded-string.md)

# importFromEncodedString

[common]\
fun [importFromEncodedString](import-from-encoded-string.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)): &lt;Error class: unknown class&gt;&lt;[KodiumPublicKey](../index.md)&gt;

Imports a `KodiumPublicKey` from a Base58-encoded string with a checksum.

This method decodes the input string using Base58 encoding with checksum validation and attempts to construct a `KodiumPublicKey` instance from the decoded data. If the input string is invalid or the checksum validation fails, the method returns a `Result` containing the failure.

#### Return

A `Result` containing a successfully imported `KodiumPublicKey` or an error if the import fails.

#### Parameters

common

| | |
|---|---|
| data | A Base58-encoded string with an appended checksum, representing the public key material. |
