//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[RatchetHeader](../index.md)/[Companion](index.md)/[deserialize](deserialize.md)

# deserialize

[common]\
fun [deserialize](deserialize.md)(bytes: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [RatchetHeader](../index.md)

Deserializes a byte array back into a [RatchetHeader](../index.md).

#### Return

The parsed [RatchetHeader](../index.md) object.

#### Parameters

common

| | |
|---|---|
| bytes | The 40-byte serialized header array. |

#### Throws

| | |
|---|---|
| IllegalArgumentException | if the provided byte array is smaller than 40 bytes. |
