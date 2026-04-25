//[kodium](../../../../index.md)/[io.kodium](../../index.md)/[Kodium](../index.md)/[pqc](index.md)/[verifyDetachedFromEncodedString](verify-detached-from-encoded-string.md)

# verifyDetachedFromEncodedString

[common]\
fun [verifyDetachedFromEncodedString](verify-detached-from-encoded-string.md)(theirPublicKey: [KodiumPqcPublicKey](../../-kodium-pqc-public-key/index.md), data: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), signatureBase64: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)

Verifies a Base64 encoded detached signature against a message using the classical Ed25519 public key.
