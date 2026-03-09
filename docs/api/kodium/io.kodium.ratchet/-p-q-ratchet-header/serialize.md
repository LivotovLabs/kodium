//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[PQRatchetHeader](index.md)/[serialize](serialize.md)

# serialize

[common]\
fun [serialize](serialize.md)(): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)

Serializes the header into a byte array suitable for transmission and for inclusion as Associated Data (AD) during AEAD encryption.

Format: `[32-byte DH Key][1088-byte ML-KEM Ciphertext][4-byte PN][4-byte N]`
