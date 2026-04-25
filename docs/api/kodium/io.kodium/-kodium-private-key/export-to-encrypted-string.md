//[kodium](../../../index.md)/[io.kodium](../index.md)/[KodiumPrivateKey](index.md)/[exportToEncryptedString](export-to-encrypted-string.md)

# exportToEncryptedString

[common]\
fun [exportToEncryptedString](export-to-encrypted-string.md)(password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;

Exports the private key to an encrypted, Base64-encoded string. A password MUST be provided.
