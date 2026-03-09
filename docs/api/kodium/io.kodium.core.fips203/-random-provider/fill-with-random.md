//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[RandomProvider](index.md)/[fillWithRandom](fill-with-random.md)

# fillWithRandom

[common]\
abstract fun [fillWithRandom](fill-with-random.md)(byteArray: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html))

Fills the byteArray with random bytes.

The cryptographic security of this relies entirely on the function underneath. It is thus important to only use a custom RandomProvider when [io.kodium.core.fips203.DefaultRandomProvider](../-default-random-provider/index.md) does not provide sufficient cryptographic security. In any case, the [io.kodium.core.fips203.DefaultRandomProvider](../-default-random-provider/index.md) already uses the strongest random source for each platform since org.kotlincrypto.random.CryptoRand wraps the default random source for each platform.

#### Parameters

common

| | |
|---|---|
| byteArray | Any [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html) needing random bytes. |
