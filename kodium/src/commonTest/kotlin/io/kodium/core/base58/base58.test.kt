package io.kodium.core.base58

import io.kodium.core.decodeBase58
import io.kodium.core.decodeBase58WithChecksum
import io.kodium.core.encodeToBase58String
import io.kodium.core.encodeToBase58WithChecksum
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Base58Test {

    @Test
    fun encodeDecodeTest() {
        val sample = "Hello, World!".encodeToByteArray()
        val encoded = sample.encodeToBase58String()
        val decoded = encoded.decodeBase58()
        assertContentEquals(
            expected = sample,
            actual = decoded,
            message = "Encoded and decoded data are not the same"
        )
    }

    @Test
    fun encodeDecodeWithChecksumTest() {
        val sample = "Hello, World!".encodeToByteArray()
        val encoded = sample.encodeToBase58WithChecksum()
        val decoded = encoded.decodeBase58WithChecksum()
        assertContentEquals(
            expected = sample,
            actual = decoded,
            message = "Encoded and decoded data with checksum are not the same"
        )

        val encodedBytes = encoded.encodeToByteArray()
        encodedBytes[3] = encodedBytes[6]
        val damagedEncodedString = encodedBytes.decodeToString()

        val unableToDecode = try {
            damagedEncodedString.decodeBase58WithChecksum()
            false
        } catch (err: Throwable) {
            true
        }

        assertEquals(
            expected = true,
            actual = unableToDecode,
            message = "We should not be able to decode damaged data as checksum verification must fail but it did not"
        )
    }
}