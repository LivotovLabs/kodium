package io.kodium.core.base64

import io.kodium.core.decodeBase64
import io.kodium.core.decodeBase64WithChecksum
import io.kodium.core.encodeToBase64String
import io.kodium.core.encodeToBase64WithChecksum
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Base64Test {

    @Test
    fun encodeDecodeTest() {
        val sample = "Hello, World!".encodeToByteArray()
        val encoded = sample.encodeToBase64String()
        val decoded = encoded.decodeBase64()
        assertContentEquals(
            expected = sample,
            actual = decoded,
            message = "Encoded and decoded data are not the same"
        )
    }

    @Test
    fun encodeDecodeWithChecksumTest() {
        val sample = "Hello, World!".encodeToByteArray()
        val encoded = sample.encodeToBase64WithChecksum()
        val decoded = encoded.decodeBase64WithChecksum()
        assertContentEquals(
            expected = sample,
            actual = decoded,
            message = "Encoded and decoded data with checksum are not the same"
        )

        val encodedBytes = encoded.encodeToByteArray()
        encodedBytes[3] = encodedBytes[6]
        val damagedEncodedString = encodedBytes.decodeToString()

        val unableToDecode = try {
            damagedEncodedString.decodeBase64WithChecksum()
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