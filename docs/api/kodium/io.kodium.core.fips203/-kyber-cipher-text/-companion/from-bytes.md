//[kodium](../../../../index.md)/[io.kodium.core.fips203](../../index.md)/[KyberCipherText](../index.md)/[Companion](index.md)/[fromBytes](from-bytes.md)

# fromBytes

[common]\
fun [fromBytes](from-bytes.md)(bytes: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [KyberCipherText](../index.md)

Copies raw Cipher Text bytes into a [KyberCipherText](../index.md) object.

#### Return

[KyberCipherText](../index.md)

#### Parameters

common

| | |
|---|---|
| bytes | [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) |

#### Throws

| | |
|---|---|
| [UnsupportedKyberVariantException](../../-unsupported-kyber-variant-exception/index.md) | when the length of the Cipher Text is not of any ML-KEM variant. |
