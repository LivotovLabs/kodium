//[kodium](../../../../index.md)/[io.kodium](../../index.md)/[KodiumPrivateKey](../index.md)/[Companion](index.md)

# Companion

[common]\
object [Companion](index.md)

## Functions

| Name | Summary |
|---|---|
| [fromRaw](from-raw.md) | [common]<br>fun [fromRaw](from-raw.md)(secretKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): [KodiumPrivateKey](../index.md)<br>Imports a private key from its raw 32-byte representation. The corresponding public key will be derived. |
| [generate](generate.md) | [common]<br>fun [generate](generate.md)(): [KodiumPrivateKey](../index.md)<br>Generates a new, random cryptographic key pair. |
| [importFromEncryptedString](import-from-encrypted-string.md) | [common]<br>fun [importFromEncryptedString](import-from-encrypted-string.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[KodiumPrivateKey](../index.md)&gt;<br>Imports a private key from an encrypted, Base64-encoded string. A password MUST be provided. |
