//[kodium](../../../../index.md)/[io.kodium.core.fips203](../../index.md)/[KyberDecapsulationKey](../index.md)/[Companion](index.md)/[fromBytes](from-bytes.md)

# fromBytes

[common]\
fun [fromBytes](from-bytes.md)(bytes: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [KyberDecapsulationKey](../index.md)

Copies raw Decapsulation Key bytes into a [KyberDecapsulationKey](../index.md) object.

#### Return

[KyberDecapsulationKey](../index.md)

#### Parameters

common

| | |
|---|---|
| bytes | [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) |

#### Throws

| | |
|---|---|
| [UnsupportedKyberVariantException](../../-unsupported-kyber-variant-exception/index.md) | when the length of the Decapsulation Key is not of any ML-KEM variant. |
| [InvalidKyberKeyException](../../-invalid-kyber-key-exception/index.md) | if the hash check or modulus fails. |
