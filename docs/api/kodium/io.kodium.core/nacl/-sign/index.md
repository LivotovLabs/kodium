//[kodium](../../../../index.md)/[io.kodium.core](../../index.md)/[nacl](../index.md)/[Sign](index.md)

# Sign

[common]\
object [Sign](index.md)

Ed25519 Digital Signatures (Detached)

## Properties

| Name | Summary |
|---|---|
| [PublicKeySize](-public-key-size.md) | [common]<br>const val [PublicKeySize](-public-key-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 32 |
| [SecretKeySize](-secret-key-size.md) | [common]<br>const val [SecretKeySize](-secret-key-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 64 |
| [SignatureSize](-signature-size.md) | [common]<br>const val [SignatureSize](-signature-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 64 |

## Functions

| Name | Summary |
|---|---|
| [keyPairFromSeed](key-pair-from-seed.md) | [common]<br>fun [keyPairFromSeed](key-pair-from-seed.md)(seed: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;<br>Generates an Ed25519 key pair from a 32-byte seed. |
| [signDetached](sign-detached.md) | [common]<br>fun [signDetached](sign-detached.md)(message: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), secretKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Signs a message and returns ONLY the 64-byte signature (detached). |
| [verifyDetached](verify-detached.md) | [common]<br>fun [verifyDetached](verify-detached.md)(signature: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), message: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), publicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)<br>Verifies a 64-byte detached signature against a message and public key. Returns true if valid, false otherwise. |
