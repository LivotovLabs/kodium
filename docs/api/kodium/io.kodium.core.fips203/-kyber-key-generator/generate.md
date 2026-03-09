//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[KyberKeyGenerator](index.md)/[generate](generate.md)

# generate

[common]\
fun [generate](generate.md)(parameter: [KyberParameter](../-kyber-parameter/index.md), randomProvider: [RandomProvider](../-random-provider/index.md) = DefaultRandomProvider): [KyberKEMKeyPair](../-kyber-k-e-m-key-pair/index.md)

Generate ML-KEM keys using the DefaultRandomProvider.

This method is the ML-KEM.KeyGen() specified in NIST FIPS 203.

#### Return

[KyberKEMKeyPair](../-kyber-k-e-m-key-pair/index.md) - Contains the Encapsulation and Decapsulation Key.

#### Parameters

common

| | |
|---|---|
| parameter | [KyberParameter](../-kyber-parameter/index.md) of the keys to be generated. |
| randomProvider | (Optional) [RandomProvider](../-random-provider/index.md) to use when generating the random and pke seed. |

#### Throws

| | |
|---|---|
| IllegalStateException | when the generated random seed and pke seed are empty/null. |
