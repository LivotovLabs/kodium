//[kodium](../../../../index.md)/[io.kodium](../../index.md)/[Kodium](../index.md)/[pqc](index.md)/[encryptToEncodedString](encrypt-to-encoded-string.md)

# encryptToEncodedString

[common]\
fun [encryptToEncodedString](encrypt-to-encoded-string.md)(mySecretKey: [KodiumPqcPrivateKey](../../-kodium-pqc-private-key/index.md), theirPublicKey: [KodiumPqcPublicKey](../../-kodium-pqc-public-key/index.md), data: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html)): &lt;Error class: unknown class&gt;&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt;

Encrypts the given data using hybrid PQC and encodes the result to a Base64 string with a checksum.

**WARNING: Lack of Forward Secrecy.** This method uses static keys for encryption. If either the sender's or receiver's long-term key is compromised, all past messages encrypted between them can be decrypted. For continuous, secure communication with forward secrecy and break-in recovery, use [io.kodium.ratchet.PQDoubleRatchetSession](../../../io.kodium.ratchet/-p-q-double-ratchet-session/index.md) instead.
