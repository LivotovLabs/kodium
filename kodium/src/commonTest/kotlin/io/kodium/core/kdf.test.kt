package io.kodium.core

import kotlin.test.Test
import kotlin.test.assertContentEquals


/**
 * Tests for the KDF (PBKDF2-HMAC-SHA256) implementation.
 * The test vectors are taken from RFC 6070, which provides examples for PBKDF2
 * with stronger hash functions than the original RFC 2898.
 *
 * @see <a href="https://tools.ietf.org/html/rfc6070">RFC 6070</a>
 */
class KDFTest {

    // Helper function to decode hex strings from the RFC into ByteArrays
    private fun hexToByteArray(hex: String): ByteArray {
        check(hex.length % 2 == 0) { "Hex string must have an even length" }
        return hex.chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    /**
     * Test Case 1 from RFC 6070.
     */
    @Test
    fun testRfc6070_case1_iterations_1() {
        val password = "password".encodeToByteArray()
        val salt = "salt".encodeToByteArray()
        val iterations = 1
        val keyLengthBytes = 20
        val expected = hexToByteArray("120fb6cffcf8b32c43e7225256c4f837a86548c9")

        val actual = KDF.deriveKey(password, salt, iterations, keyLengthBytes)
        assertContentEquals(expected, actual, "Test case 1 (1 iteration) failed")
    }

    /**
     * Test Case 2 from RFC 6070.
     */
    @Test
    fun testRfc6070_case2_iterations_2() {
        val password = "password".encodeToByteArray()
        val salt = "salt".encodeToByteArray()
        val iterations = 2
        val keyLengthBytes = 20
        val expected = hexToByteArray("ae4d0c95af6b46d32d0adff928f06dd02a303f8e")

        val actual = KDF.deriveKey(password, salt, iterations, keyLengthBytes)
        assertContentEquals(expected, actual, "Test case 2 (2 iterations) failed")
    }

    /**
     * Test Case 3 from RFC 6070.
     */
    @Test
    fun testRfc6070_case3_iterations_4096() {
        val password = "password".encodeToByteArray()
        val salt = "salt".encodeToByteArray()
        val iterations = 4096
        val keyLengthBytes = 20
        val expected = hexToByteArray("c5e478d59288c841aa530db6845c4c8d962893a0")

        val actual = KDF.deriveKey(password, salt, iterations, keyLengthBytes)
        assertContentEquals(expected, actual, "Test case 3 (4096 iterations) failed")
    }

    /**
     * Test Case 4 from RFC 6070.
     * This test is extremely slow (~16.7 million iterations) and is not suitable for regular execution.
     */
    @Test
    fun testRfc6070_case4_iterations_slow() {
        val password = "password".encodeToByteArray()
        val salt = "salt".encodeToByteArray()
        val iterations = 16777216
        val keyLengthBytes = 20
        val expected = hexToByteArray("cf81c66fe8cfc04d1f31ecb65dab4089f7f179e8")

        val actual = KDF.deriveKey(password, salt, iterations, keyLengthBytes)
        assertContentEquals(expected, actual, "Test case 4 (16,777,216 iterations) failed")
    }

    /**
     * Test Case 5 from RFC 6070.
     */
    @Test
    fun testRfc6070_case5_complex_inputs() {
        val password = "passwordPASSWORDpassword".encodeToByteArray()
        val salt = "saltSALTsaltSALTsaltSALTsaltSALTsalt".encodeToByteArray()
        val iterations = 4096
        val keyLengthBytes = 25
        val expected = hexToByteArray("348c89dbcbd32b2f32d814b8116e84cf2b17347ebc1800181c")

        val actual = KDF.deriveKey(password, salt, iterations, keyLengthBytes)
        assertContentEquals(expected, actual, "Test case 5 (complex inputs) failed")
    }

    /**
     * Test Case 6 from RFC 6070.
     */
    @Test
    fun testRfc6070_case6_null_chars() {
        val password = "pass\u0000word".encodeToByteArray()
        val salt = "sa\u0000lt".encodeToByteArray()
        val iterations = 4096
        val keyLengthBytes = 16
        val expected = hexToByteArray("89b69d0516f829893c696226650a8687")

        val actual = KDF.deriveKey(password, salt, iterations, keyLengthBytes)
        assertContentEquals(expected, actual, "Test case 6 (null chars) failed")
    }
}