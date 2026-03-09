//[kodium](../../index.md)/[io.kodium.core](index.md)/[decodeBase58WithChecksum](decode-base58-with-checksum.md)

# decodeBase58WithChecksum

[common]\
fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html).[decodeBase58WithChecksum](decode-base58-with-checksum.md)(): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)

Decodes a Base58-encoded string with a checksum validation. The method first decodes the Base58 string, separates the checksum from the payload, computes the checksum of the payload, and verifies it against the decoded checksum. If the checksum validation fails, an exception is thrown.

#### Return

A byte array representing the decoded payload of the Base58-encoded string after verifying the checksum.

#### Throws

| | |
|---|---|
| IllegalArgumentException | if the input string is too short for containing a checksum or if the checksum validation fails. |
