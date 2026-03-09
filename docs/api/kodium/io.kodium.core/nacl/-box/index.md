//[kodium](../../../../index.md)/[io.kodium.core](../../index.md)/[nacl](../index.md)/[Box](index.md)

# Box

[common]\
object [Box](index.md)

Authenticated Public-Key Encryption (box)

## Properties

| Name | Summary |
|---|---|
| [MacSize](-mac-size.md) | [common]<br>const val [MacSize](-mac-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 16 |
| [NonceSize](-nonce-size.md) | [common]<br>const val [NonceSize](-nonce-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 24 |
| [PublicKeySize](-public-key-size.md) | [common]<br>const val [PublicKeySize](-public-key-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 32 |
| [SecretKeySize](-secret-key-size.md) | [common]<br>const val [SecretKeySize](-secret-key-size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 32 |

## Functions

| Name | Summary |
|---|---|
| [beforenm](beforenm.md) | [common]<br>fun [beforenm](beforenm.md)(theirPublicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), mySecretKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>Computes the shared secret between a public key and a private key. |
| [keyPair](key-pair.md) | [common]<br>fun [keyPair](key-pair.md)(): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;<br>Generates a new public/private key pair. |
| [keyPairFromSecretKey](key-pair-from-secret-key.md) | [common]<br>fun [keyPairFromSecretKey](key-pair-from-secret-key.md)(secretKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): &lt;Error class: unknown class&gt;&lt;[ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)&gt;<br>Generates a key pair from a given secret key. |
| [open](open.md) | [common]<br>fun [open](open.md)(box: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), nonce: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), theirPublicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), mySecretKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)?<br>The full open operation: Decrypts a message from a sender. |
| [seal](seal.md) | [common]<br>fun [seal](seal.md)(message: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), nonce: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), theirPublicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), mySecretKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)<br>The full box operation: Encrypts a message for a recipient. |
