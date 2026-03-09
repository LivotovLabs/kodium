//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[HKDF](index.md)/[deriveSecrets](derive-secrets.md)

# deriveSecrets

[common]\
fun [deriveSecrets](derive-secrets.md)(salt: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)?, ikm: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), info: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)?, length: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)

Convenience method that performs both the Extract and Expand steps in a single operation.

#### Return

The final Output Keying Material (OKM).

#### Parameters

common

| | |
|---|---|
| salt | Optional salt value to strengthen the extraction phase. |
| ikm | Input keying material containing the initial cryptographic entropy. |
| info | Optional context and application-specific information to bind the resulting keys. |
| length | The desired output length in bytes for the generated keying material. |
