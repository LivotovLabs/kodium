//[kodium](../../../index.md)/[io.kodium.core.fips203](../index.md)/[DefaultRandomProvider](index.md)/[fillWithRandom](fill-with-random.md)

# fillWithRandom

[common]\
open override fun [fillWithRandom](fill-with-random.md)(byteArray: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html))

Uses org.kotlincrypto.random.CryptoRand.Default as a random source. This is an external library which wraps each platform's default random source to a common API.
