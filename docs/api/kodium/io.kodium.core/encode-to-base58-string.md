//[kodium](../../index.md)/[io.kodium.core](index.md)/[encodeToBase64String](encode-to-base64-string.md)

# encodeToBase64String

[common]\
fun [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html).[encodeToBase64String](encode-to-base64-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)

Encodes a byte array into a Base64-encoded string. Base64 encoding is typically used for encoding binary data in a format that is easier for humans to work with, such as in the case of cryptocurrency addresses or other identifiers.

The encoding process skips leading zeroes, encodes the data to Base64, and preserves the number of leading zeroes by representing them as the special Base64 character for zero.

#### Return

a Base64-encoded string representation of the byte array
