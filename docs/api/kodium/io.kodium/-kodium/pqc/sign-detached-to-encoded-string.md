//[kodium](../../../../index.md)/[io.kodium](../../index.md)/[Kodium](../index.md)/[pqc](index.md)/[signDetachedToEncodedString](sign-detached-to-encoded-string.md)

# signDetachedToEncodedString

[common]\
fun [signDetachedToEncodedString](sign-detached-to-encoded-string.md)(mySecretKey: [KodiumPqcPrivateKey](../../-kodium-pqc-private-key/index.md), data: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;

Signs data using the classical Ed25519 component of the PQC Private Key. Returns the detached signature as a Base64 encoded string. Note: This signature is purely classical (Ed25519) as PQC signatures (e.g. ML-DSA) are not yet integrated.
