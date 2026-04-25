//[kodium](../../../../index.md)/[io.kodium](../../index.md)/[KodiumPrivateKey](../index.md)/[Companion](index.md)/[importFromEncryptedString](import-from-encrypted-string.md)

# importFromEncryptedString

[common]\
fun [importFromEncryptedString](import-from-encrypted-string.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[KodiumPrivateKey](../index.md)&gt;

Imports a private key from an encrypted, Base64-encoded string. A password MUST be provided.
