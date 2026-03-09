//[kodium](../../../index.md)/[io.kodium.core](../index.md)/[MLKEM](index.md)

# MLKEM

[common]\
object [MLKEM](index.md)

FIPS 203 ML-KEM-768 implementation wrapper for Kodium PQC Hybrid Architecture.

## Properties

| Name | Summary |
|---|---|
| [CiphertextSize](-ciphertext-size.md) | [common]<br>const val [CiphertextSize](-ciphertext-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 1088 |
| [PublicKeySize](-public-key-size.md) | [common]<br>const val [PublicKeySize](-public-key-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 1184 |
| [SecretKeySize](-secret-key-size.md) | [common]<br>const val [SecretKeySize](-secret-key-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 2400 |
| [SharedSecretSize](-shared-secret-size.md) | [common]<br>const val [SharedSecretSize](-shared-secret-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 32 |

## Functions

| Name | Summary |
|---|---|
| [decapsulate](decapsulate.md) | [common]<br>fun [decapsulate](decapsulate.md)(ciphertext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), secretKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)?<br>Decapsulates the ciphertext using the secret key. |
| [encapsulate](encapsulate.md) | [common]<br>fun [encapsulate](encapsulate.md)(publicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;<br>Encapsulates a shared secret using the recipient's public key. Uses a CSPRNG for entropy. |
| [getPublicKeyFromSecretKey](get-public-key-from-secret-key.md) | [common]<br>fun [getPublicKeyFromSecretKey](get-public-key-from-secret-key.md)(secretKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Extracts the public key from the secret key (FIPS 203 specification). |
| [keyPair](key-pair.md) | [common]<br>fun [keyPair](key-pair.md)(): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;<br>Generates a key pair matching ML-KEM-768 dimensions using a CSPRNG. |
