//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[DefaultRandomProvider](index.md)

# DefaultRandomProvider

[common]\
object [DefaultRandomProvider](index.md) : [RandomProvider](../-random-provider/index.md)

The default [io.kodium.core.fips203.RandomProvider](../-random-provider/index.md) for use when the [io.kodium.core.fips203.RandomProvider](../-random-provider/index.md) is not specified.

Changing this is not recommended unless you have sufficient motivation to do so. Please read org.kotlincrypto.random.CryptoRand's source code before deciding on anything.

#### Author

Ron Lauren Hombre

## Functions

| Name | Summary |
|---|---|
| [fillWithRandom](fill-with-random.md) | [common]<br>open override fun [fillWithRandom](fill-with-random.md)(byteArray: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html))<br>Uses org.kotlincrypto.random.CryptoRand.Default as a random source. This is an external library which wraps each platform's default random source to a common API. |
