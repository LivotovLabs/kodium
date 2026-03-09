//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[KyberKeyGenerator](index.md)

# KyberKeyGenerator

[common]\
object [KyberKeyGenerator](index.md)

A generator class for ML-KEM Keys.

This class contains K-PKE.KeyGen() and ML-KEM.KeyGen() all according to NIST FIPS 203.

#### Author

Ron Lauren Hombre

## Functions

| Name | Summary |
|---|---|
| [generate](generate.md) | [common]<br>fun [generate](generate.md)(parameter: [KyberParameter](../-kyber-parameter/index.md), randomProvider: [RandomProvider](../-random-provider/index.md) = DefaultRandomProvider): [KyberKEMKeyPair](../-kyber-k-e-m-key-pair/index.md)<br>Generate ML-KEM keys using the DefaultRandomProvider. |
