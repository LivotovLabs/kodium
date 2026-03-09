//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[KyberConstants](index.md)

# KyberConstants

[common]\
object [KyberConstants](index.md)

Constants for ML-KEM.

This class contains precomputed values for optimization purposes.

#### Author

Ron Lauren Hombre

## Properties

| Name | Summary |
|---|---|
| [BARRETT_APPROX](-b-a-r-r-e-t-t_-a-p-p-r-o-x.md) | [common]<br>const val [BARRETT_APPROX](-b-a-r-r-e-t-t_-a-p-p-r-o-x.md): [Short](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-short/index.html) = 20159<br>Approximation of Q for Barrett Reduction |
| [ENCODE_SIZE](-e-n-c-o-d-e_-s-i-z-e.md) | [common]<br>const val [ENCODE_SIZE](-e-n-c-o-d-e_-s-i-z-e.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Encoding size for encoding coefficients and terms. |
| [MONT_R2](-m-o-n-t_-r2.md) | [common]<br>const val [MONT_R2](-m-o-n-t_-r2.md): [Short](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-short/index.html) = 1353<br>2^16 * 2^16 (mod Q) for Montgomery Reduction. |
| [N](-n.md) | [common]<br>const val [N](-n.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 256<br>Number of coefficients in the polynomial. |
| [N_BYTES](-n_-b-y-t-e-s.md) | [common]<br>const val [N_BYTES](-n_-b-y-t-e-s.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>The byte size of the number of coefficients in bits. |
| [PRECOMPUTED_GAMMAS_TABLE](-p-r-e-c-o-m-p-u-t-e-d_-g-a-m-m-a-s_-t-a-b-l-e.md) | [common]<br>val [PRECOMPUTED_GAMMAS_TABLE](-p-r-e-c-o-m-p-u-t-e-d_-g-a-m-m-a-s_-t-a-b-l-e.md): [IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int-array/index.html)<br>Pre-generated Gamma values according to the formula 17^(2 * bitReverse(n)) mod Q and converted to Montgomery Form. |
| [PRECOMPUTED_ZETAS_TABLE](-p-r-e-c-o-m-p-u-t-e-d_-z-e-t-a-s_-t-a-b-l-e.md) | [common]<br>val [PRECOMPUTED_ZETAS_TABLE](-p-r-e-c-o-m-p-u-t-e-d_-z-e-t-a-s_-t-a-b-l-e.md): [IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int-array/index.html)<br>Pre-generated Zeta values according to the formula 17^bitReverse(n) mod Q and converted to Montgomery Form. |
| [Q](-q.md) | [common]<br>const val [Q](-q.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 3329<br>Prime Integer composed of (2^8 * 13) + 1. |
| [Q_HALF](-q_-h-a-l-f.md) | [common]<br>const val [Q_HALF](-q_-h-a-l-f.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Half of Q. |
| [Q_INV](-q_-i-n-v.md) | [common]<br>const val [Q_INV](-q_-i-n-v.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Negated modular inverse of Q base 2^16 |
| [SECRET_KEY_LENGTH](-s-e-c-r-e-t_-k-e-y_-l-e-n-g-t-h.md) | [common]<br>const val [SECRET_KEY_LENGTH](-s-e-c-r-e-t_-k-e-y_-l-e-n-g-t-h.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>The length of the Shared Secret Key in bytes. |
