//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[MLKEM_768](index.md)

# MLKEM_768

[common]\
class [MLKEM_768](index.md)(randomProvider: [RandomProvider](../-random-provider/index.md) = DefaultRandomProvider) : MLKEM

ML-KEM-768 (RBG Strength: 192, NIST Security Category: 3)

#### Author

Ron Lauren Hombre

#### Since

2.0.0

## Constructors

| | |
|---|---|
| [MLKEM_768](-m-l-k-e-m_768.md) | [common]<br>constructor(randomProvider: [RandomProvider](../-random-provider/index.md) = DefaultRandomProvider)<br>Uses a [RandomProvider](../-random-provider/index.md) when specified. All calls to [generate](generate.md) will then use that as a random source. |

## Properties

| Name | Summary |
|---|---|
| [parameter](parameter.md) | [common]<br>open override val [parameter](parameter.md): [KyberParameter](../-kyber-parameter/index.md)<br>The specific parameter set used. |

## Functions

| Name | Summary |
|---|---|
| [generate](generate.md) | [common]<br>open override fun [generate](generate.md)(): [KyberKEMKeyPair](../-kyber-k-e-m-key-pair/index.md)<br>Generates a [KyberKEMKeyPair](../-kyber-k-e-m-key-pair/index.md) using the specified [io.kodium.core.fips203.KyberParameter](../-kyber-parameter/index.md) and [RandomProvider](../-random-provider/index.md). |
