//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[HKDF](index.md)/[expand](expand.md)

# expand

[common]\
fun [expand](expand.md)(prk: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), info: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)?, length: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)

Step 2: Expand. Expands a pseudorandom key (PRK) into an Output Keying Material (OKM) of the specified length. This step can be used to generate multiple derived keys from a single PRK by providing different `info` context strings.

#### Return

The generated Output Keying Material (OKM).

#### Parameters

common

| | |
|---|---|
| prk | A pseudorandom key of at least 32 bytes (usually the output from the [extract](extract.md) method). |
| info | Optional context and application-specific information. Used to bind the derived key     to a specific protocol or application context. |
| length | The desired length of the output keying material in bytes. Maximum allowed length     is 255 * 32 bytes (8,160 bytes). |

#### Throws

| | |
|---|---|
| IllegalArgumentException | if the requested length exceeds the maximum allowed size. |
