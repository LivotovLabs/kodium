//[kodium](../../index.md)/[io.kodium.core.fips203](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [DefaultRandomProvider](-default-random-provider/index.md) | [common]<br>object [DefaultRandomProvider](-default-random-provider/index.md) : [RandomProvider](-random-provider/index.md)<br>The default [io.kodium.core.fips203.RandomProvider](-random-provider/index.md) for use when the [io.kodium.core.fips203.RandomProvider](-random-provider/index.md) is not specified. |
| [InvalidKyberKeyException](-invalid-kyber-key-exception/index.md) | [common]<br>class [InvalidKyberKeyException](-invalid-kyber-key-exception/index.md)(val message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html))<br>When a key fails a validity check. |
| [KyberCipherText](-kyber-cipher-text/index.md) | [common]<br>class [KyberCipherText](-kyber-cipher-text/index.md)<br>A class for ML-KEM Cipher Texts. |
| [KyberConstants](-kyber-constants/index.md) | [common]<br>object [KyberConstants](-kyber-constants/index.md)<br>Constants for ML-KEM. |
| [KyberDecapsulationKey](-kyber-decapsulation-key/index.md) | [common]<br>class [KyberDecapsulationKey](-kyber-decapsulation-key/index.md) : KyberKEMKey<br>A class for ML-KEM Decapsulation Keys. |
| [KyberDecryptionKey](-kyber-decryption-key/index.md) | [common]<br>class [KyberDecryptionKey](-kyber-decryption-key/index.md) : KyberPKEKey<br>A class for ML-KEM Decryption Keys. |
| [KyberEncapsulationKey](-kyber-encapsulation-key/index.md) | [common]<br>class [KyberEncapsulationKey](-kyber-encapsulation-key/index.md) : KyberKEMKey<br>A class for ML-KEM Encapsulation Keys. |
| [KyberEncapsulationResult](-kyber-encapsulation-result/index.md) | [common]<br>class [KyberEncapsulationResult](-kyber-encapsulation-result/index.md)<br>A class for ML-KEM Encapsulation Results. |
| [KyberEncryptionKey](-kyber-encryption-key/index.md) | [common]<br>class [KyberEncryptionKey](-kyber-encryption-key/index.md) : KyberPKEKey<br>A class for ML-KEM Encryption Keys. |
| [KyberKEMKeyPair](-kyber-k-e-m-key-pair/index.md) | [common]<br>class [KyberKEMKeyPair](-kyber-k-e-m-key-pair/index.md)(val encapsulationKey: [KyberEncapsulationKey](-kyber-encapsulation-key/index.md), val decapsulationKey: [KyberDecapsulationKey](-kyber-decapsulation-key/index.md))<br>A class for ML-KEM Encapsulation and Decapsulation Key Pairs. |
| [KyberKeyGenerator](-kyber-key-generator/index.md) | [common]<br>object [KyberKeyGenerator](-kyber-key-generator/index.md)<br>A generator class for ML-KEM Keys. |
| [KyberParameter](-kyber-parameter/index.md) | [common]<br>enum [KyberParameter](-kyber-parameter/index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-enum/index.html)&lt;[KyberParameter](-kyber-parameter/index.md)&gt; <br>Parameter sets for ML-KEM. |
| [KyberPKEKeyPair](-kyber-p-k-e-key-pair/index.md) | [common]<br>class [KyberPKEKeyPair](-kyber-p-k-e-key-pair/index.md)<br>A class for K-PKE Encryption and Decryption Key Pairs. |
| [MLKEM_768](-m-l-k-e-m_768/index.md) | [common]<br>class [MLKEM_768](-m-l-k-e-m_768/index.md)(randomProvider: [RandomProvider](-random-provider/index.md) = DefaultRandomProvider) : MLKEM<br>ML-KEM-768 (RBG Strength: 192, NIST Security Category: 3) |
| [RandomBitGenerationException](-random-bit-generation-exception/index.md) | [common]<br>class [RandomBitGenerationException](-random-bit-generation-exception/index.md)<br>When the random bit source produces all zeroes. |
| [RandomProvider](-random-provider/index.md) | [common]<br>interface [RandomProvider](-random-provider/index.md)<br>A random source to use when generating an ML-KEM Key Pair or during encapsulation. |
| [UnsupportedKyberVariantException](-unsupported-kyber-variant-exception/index.md) | [common]<br>class [UnsupportedKyberVariantException](-unsupported-kyber-variant-exception/index.md)(val message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html))<br>When an unsupported ML-KEM variant was attempted to be used. |
