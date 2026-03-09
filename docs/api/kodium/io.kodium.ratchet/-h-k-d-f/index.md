//[kodium](../../../index.md)/[io.kodium.ratchet](../index.md)/[HKDF](index.md)

# HKDF

object [HKDF](index.md)

An implementation of the HMAC-based Extract-and-Expand Key Derivation Function (HKDF) using HMAC-SHA256 as the underlying pseudorandom function (PRF).

HKDF is used to take some initial keying material (IKM) that may contain some cryptographic entropy, and derive one or more strong, uniformly distributed cryptographic keys. It consists of two distinct steps: &quot;extract&quot; (which concentrates the entropy into a short pseudorandom key) and &quot;expand&quot; (which expands that key into the desired output length).

This implementation complies with RFC 5869.

#### See also

| | |
|---|---|
|  | <a href="https://tools.ietf.org/html/rfc5869">RFC 5869</a> |

## Functions

| Name | Summary |
|---|---|
| [deriveSecrets](derive-secrets.md) | [common]<br>fun [deriveSecrets](derive-secrets.md)(salt: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)?, ikm: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), info: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)?, length: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Convenience method that performs both the Extract and Expand steps in a single operation. |
| [expand](expand.md) | [common]<br>fun [expand](expand.md)(prk: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), info: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)?, length: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Step 2: Expand. Expands a pseudorandom key (PRK) into an Output Keying Material (OKM) of the specified length. This step can be used to generate multiple derived keys from a single PRK by providing different `info` context strings. |
| [extract](extract.md) | [common]<br>fun [extract](extract.md)(salt: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)?, ikm: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Step 1: Extract. Computes a pseudorandom key (PRK) from the input keying material (IKM) and an optional salt. The extraction step ensures the resulting PRK is cryptographically strong, even if the IKM is not uniformly distributed. |
