//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[HKDF](index.md)/[extract](extract.md)

# extract

[common]\
fun [extract](extract.md)(salt: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)?, ikm: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)

Step 1: Extract. Computes a pseudorandom key (PRK) from the input keying material (IKM) and an optional salt. The extraction step ensures the resulting PRK is cryptographically strong, even if the IKM is not uniformly distributed.

#### Return

A pseudorandom key (PRK) of 32 bytes (HMAC-SHA256 output length).

#### Parameters

common

| | |
|---|---|
| salt | Optional salt value (a non-secret random value). If null or empty, a salt consisting     of zeros (matching the hash length) is used. |
| ikm | Input keying material. |
