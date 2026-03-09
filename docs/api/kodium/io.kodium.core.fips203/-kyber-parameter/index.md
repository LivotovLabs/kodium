//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[KyberParameter](index.md)

# KyberParameter

[common]\
enum [KyberParameter](index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-enum/index.html)&lt;[KyberParameter](index.md)&gt; 

Parameter sets for ML-KEM.

This class contains the defined parameter values for each set of ML-KEM according to NIST FIPS 203.

#### Author

Ron Lauren Hombre

## Entries

| | |
|---|---|
| [ML_KEM_512](-m-l_-k-e-m_512/index.md) | [common]<br>[ML_KEM_512](-m-l_-k-e-m_512/index.md)<br>ML-KEM-512 (RBG Strength: 128, NIST Security Category: 1). |
| [ML_KEM_768](-m-l_-k-e-m_768/index.md) | [common]<br>[ML_KEM_768](-m-l_-k-e-m_768/index.md)<br>ML-KEM-768 (RBG Strength: 192, NIST Security Category: 3). |
| [ML_KEM_1024](-m-l_-k-e-m_1024/index.md) | [common]<br>[ML_KEM_1024](-m-l_-k-e-m_1024/index.md)<br>ML-KEM-1024 (RBG Strength: 256, NIST Security Category: 5). |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [CIPHERTEXT_LENGTH](-c-i-p-h-e-r-t-e-x-t_-l-e-n-g-t-h.md) | [common]<br>val [CIPHERTEXT_LENGTH](-c-i-p-h-e-r-t-e-x-t_-l-e-n-g-t-h.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>The byte length of the Cipher Text for the parameter set. |
| [DECAPSULATION_KEY_LENGTH](-d-e-c-a-p-s-u-l-a-t-i-o-n_-k-e-y_-l-e-n-g-t-h.md) | [common]<br>val [DECAPSULATION_KEY_LENGTH](-d-e-c-a-p-s-u-l-a-t-i-o-n_-k-e-y_-l-e-n-g-t-h.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>The byte length of the Decapsulation Key for the parameter set. |
| [DECRYPTION_KEY_LENGTH](-d-e-c-r-y-p-t-i-o-n_-k-e-y_-l-e-n-g-t-h.md) | [common]<br>val [DECRYPTION_KEY_LENGTH](-d-e-c-r-y-p-t-i-o-n_-k-e-y_-l-e-n-g-t-h.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>The byte length of the Decryption Key for the parameter set. |
| [DU](-d-u.md) | [common]<br>val [DU](-d-u.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
| [DV](-d-v.md) | [common]<br>val [DV](-d-v.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
| [ENCAPSULATION_KEY_LENGTH](-e-n-c-a-p-s-u-l-a-t-i-o-n_-k-e-y_-l-e-n-g-t-h.md) | [common]<br>val [ENCAPSULATION_KEY_LENGTH](-e-n-c-a-p-s-u-l-a-t-i-o-n_-k-e-y_-l-e-n-g-t-h.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>The byte length of the Encapsulation Key for the parameter set. |
| [ENCRYPTION_KEY_LENGTH](-e-n-c-r-y-p-t-i-o-n_-k-e-y_-l-e-n-g-t-h.md) | [common]<br>val [ENCRYPTION_KEY_LENGTH](-e-n-c-r-y-p-t-i-o-n_-k-e-y_-l-e-n-g-t-h.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>The byte length of the Encryption Key for the parameter set. |
| [ETA1](-e-t-a1.md) | [common]<br>val [ETA1](-e-t-a1.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
| [ETA2](-e-t-a2.md) | [common]<br>val [ETA2](-e-t-a2.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
| [K](-k.md) | [common]<br>val [K](-k.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
| [name](-m-l_-k-e-m_1024/index.md#-372974862%2FProperties%2F855309450) | [common]<br>val [name](-m-l_-k-e-m_1024/index.md#-372974862%2FProperties%2F855309450): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html) |
| [ordinal](-m-l_-k-e-m_1024/index.md#-739389684%2FProperties%2F855309450) | [common]<br>val [ordinal](-m-l_-k-e-m_1024/index.md#-739389684%2FProperties%2F855309450): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [valueOf](value-of.md) | [common]<br>fun [valueOf](value-of.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)): [KyberParameter](index.md)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.md) | [common]<br>fun [values](values.md)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-array/index.html)&lt;[KyberParameter](index.md)&gt;<br>Returns an array containing the constants of this enum type, in the order they're declared. |
