//[kodium](../../index.md)/[io.kodium.core](index.md)/[decodeBase58](decode-base58.md)

# decodeBase58

[common]\
fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html).[decodeBase58](decode-base58.md)(): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)

Decodes the current string, which is encoded in Base58 format, into a byte array. Base58 is a binary-to-text encoding designed to create shorter and more human-readable strings without problematic characters that are easily confused (e.g., '0' and 'O').

#### Return

A byte array representing the decoded data of the Base58-encoded string.

#### Throws

| | |
|---|---|
| NumberFormatException | if the input string contains characters not valid in Base58 encoding. |
