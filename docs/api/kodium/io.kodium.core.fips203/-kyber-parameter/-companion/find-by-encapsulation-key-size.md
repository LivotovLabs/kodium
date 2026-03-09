//[kodium](../../../../index.md)/[io.kodium.core.fips203](../../index.md)/[KyberParameter](../index.md)/[Companion](index.md)/[findByEncapsulationKeySize](find-by-encapsulation-key-size.md)

# findByEncapsulationKeySize

[common]\
fun [findByEncapsulationKeySize](find-by-encapsulation-key-size.md)(length: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)): [KyberParameter](../index.md)

Find parameter set used based on the byte length of the Encapsulation Key.

#### Return

[KyberParameter](../index.md)

#### Parameters

common

| | |
|---|---|
| length | [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |

#### Throws

| | |
|---|---|
| [UnsupportedKyberVariantException](../../-unsupported-kyber-variant-exception/index.md) | when the byte length does not match any parameter set. |
