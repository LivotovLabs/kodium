//[kodium](../../index.md)/[io.kodium](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [Kodium](-kodium/index.md) | [common]<br>object [Kodium](-kodium/index.md)<br>Kodium is a cryptographic utility object for key pair generation, encryption, and decryption. It supports both asymmetric and symmetric encryption methods, allowing for secure communication and data storage. Encoded data is managed using Base64 encoding with checksum for integrity verification. |
| [KodiumPqcPrivateKey](-kodium-pqc-private-key/index.md) | [common]<br>class [KodiumPqcPrivateKey](-kodium-pqc-private-key/index.md)<br>Represents a Hybrid Post-Quantum Private Key. |
| [KodiumPqcPublicKey](-kodium-pqc-public-key/index.md) | [common]<br>data class [KodiumPqcPublicKey](-kodium-pqc-public-key/index.md)(val classicalPublicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val pqcPublicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html))<br>Represents a Hybrid Post-Quantum Public Key. |
| [KodiumPrivateKey](-kodium-private-key/index.md) | [common]<br>class [KodiumPrivateKey](-kodium-private-key/index.md) |
| [KodiumPublicKey](-kodium-public-key/index.md) | [common]<br>data class [KodiumPublicKey](-kodium-public-key/index.md)(val publicKey: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html))<br>Represents a public key used in cryptographic operations within the KMP CryptoKit. This class facilitates the handling of public keys, including importing from and exporting to encoded formats with checksum validation. |
