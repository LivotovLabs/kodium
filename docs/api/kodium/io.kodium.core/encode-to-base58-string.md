//[kodium](../../index.md)/[io.kodium.core](index.md)/[encodeToBase58String](encode-to-base58-string.md)

# encodeToBase58String

[common]\
fun [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html).[encodeToBase58String](encode-to-base58-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)

Encodes a byte array into a Base58-encoded string. Base58 encoding is typically used for encoding binary data in a format that is easier for humans to work with, such as in the case of cryptocurrency addresses or other identifiers.

The encoding process skips leading zeroes, encodes the data to Base58, and preserves the number of leading zeroes by representing them as the special Base58 character for zero.

#### Return

a Base58-encoded string representation of the byte array
