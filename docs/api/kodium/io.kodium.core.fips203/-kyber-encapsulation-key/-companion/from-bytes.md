//[kodium](../../../../index.md)/[io.kodium.core.fips203](../../index.md)/[KyberEncapsulationKey](../index.md)/[Companion](index.md)/[fromBytes](from-bytes.md)

# fromBytes

[common]\
fun [fromBytes](from-bytes.md)(bytes: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [KyberEncapsulationKey](../index.md)

Copies raw Encapsulation Key bytes into a [KyberEncapsulationKey](../index.md) object.

#### Return

[KyberEncapsulationKey](../index.md)

#### Parameters

common

| | |
|---|---|
| bytes | [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) |

#### Throws

| | |
|---|---|
| [UnsupportedKyberVariantException](../../-unsupported-kyber-variant-exception/index.md) | when the length of the Encapsulation Key is not of any ML-KEM variant. |
| [InvalidKyberKeyException](../../-invalid-kyber-key-exception/index.md) | if the modulus check fails. |
