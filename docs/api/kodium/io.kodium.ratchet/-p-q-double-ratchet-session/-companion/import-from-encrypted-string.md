//[kodium](../../../../index.md)/[io.kodium.ratchet](../../index.md)/[PQDoubleRatchetSession](../index.md)/[Companion](index.md)/[importFromEncryptedString](import-from-encrypted-string.md)

# importFromEncryptedString

[common]\
fun [importFromEncryptedString](import-from-encrypted-string.md)(data: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), password: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), keyDerivationIterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = io.kodium.Kodium.PBKDF2_ITERATIONS): &lt;Error class: unknown class&gt;&lt;[PQDoubleRatchetSession](../index.md)&gt;

Initializes a new PQ Double Ratchet session for the Initiator using deterministic KEM encapsulation.

**WARNING: This function is intended for deterministic testing ONLY.**
