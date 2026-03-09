//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[KyberEncapsulationKey](index.md)/[encapsulate](encapsulate.md)

# encapsulate

[common]\
fun [encapsulate](encapsulate.md)(randomProvider: [RandomProvider](../-random-provider/index.md) = DefaultRandomProvider): [KyberEncapsulationResult](../-kyber-encapsulation-result/index.md)

Encapsulates this [KyberEncapsulationKey](index.md) into a [KyberCipherText](../-kyber-cipher-text/index.md) and generates a Shared Secret Key using the DefaultRandomProvider.

This method is the ML-KEM.Encaps() specified in NIST FIPS 203.

#### Return

[KyberEncapsulationResult](../-kyber-encapsulation-result/index.md) - Contains the Cipher Text and the generated Shared Secret Key.

#### Parameters

common

| | |
|---|---|
| randomProvider | (Optional) [RandomProvider](../-random-provider/index.md) to use when generating the plaintext. |
