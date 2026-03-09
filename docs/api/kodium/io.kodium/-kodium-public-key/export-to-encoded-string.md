//[kodium](../../../index.md)/[io.kodium](../index.md)/[KodiumPublicKey](index.md)/[exportToEncodedString](export-to-encoded-string.md)

# exportToEncodedString

[common]\
fun [exportToEncodedString](export-to-encoded-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)

Exports the raw public key material to a Base58-encoded string with a checksum appended.

This method encodes the byte array representing the public key (`material`) using Base58 encoding and appends a checksum to ensure data integrity. The checksum is calculated using a double SHA-256 hash of the original byte array, with the first four bytes of the hash serving as the checksum.

The resulting string is suitable for secure storage or transmission and can subsequently be imported back into a KodiumKodiumPublicKey object using `importFromEncodedString`.

#### Return

A Base58-encoded string representation of the public key material with a checksum attached.
