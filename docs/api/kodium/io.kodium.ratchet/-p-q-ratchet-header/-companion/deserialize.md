//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[PQRatchetHeader](../index.md)/[Companion](index.md)/[deserialize](deserialize.md)

# deserialize

[common]\
fun [deserialize](deserialize.md)(bytes: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [PQRatchetHeader](../index.md)

Deserializes a byte array back into a [PQRatchetHeader](../index.md).

#### Return

The parsed [PQRatchetHeader](../index.md) object.

#### Parameters

common

| | |
|---|---|
| bytes | The serialized header array. |

#### Throws

| | |
|---|---|
| IllegalArgumentException | if the provided byte array is smaller than the required size. |
