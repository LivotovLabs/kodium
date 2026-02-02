package io.kodium.core

import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
import kotlin.experimental.xor

/**
 * A pure Kotlin implementation of the PBKDF2 (Password-Based Key Derivation Function 2)
 * with HMAC-SHA256 as the pseudorandom function.
 *
 * This object provides a standard, secure way to derive a strong cryptographic key
 * from a user-provided password. It is designed to be computationally intensive
 * to slow down brute-force and dictionary attacks on passwords.
 */
object KDF {

    /**
     * Derives a key of a specified length from a password and salt using PBKDF2.
     *
     * @param password The password.
     * @param salt The salt.
     * @param iterations The number of iterations to perform.
     * @param keyLengthBytes The desired length of the derived key in bytes.
     * @return The derived key as a byte array.
     */
    fun deriveKey(password: ByteArray, salt: ByteArray, iterations: Int, keyLengthBytes: Int): ByteArray {
        if (iterations <= 0) throw IllegalArgumentException("Iterations must be positive")
        if (keyLengthBytes <= 0) throw IllegalArgumentException("Key length must be positive")

        val hmacLength = 32 // HMAC-SHA256 output size is 32 bytes
        val numBlocks = (keyLengthBytes + hmacLength - 1) / hmacLength
        val derivedKey = ByteArray(keyLengthBytes)
        var offset = 0

        for (i in 1..numBlocks) {
            // Calculate each block of the derived key.
            // We pass the raw password and salt each time to ensure no state is reused incorrectly.
            val block = F(password, salt, iterations, i)
            val bytesToCopy = minOf(hmacLength, keyLengthBytes - offset)
            block.copyInto(derivedKey, offset, 0, bytesToCopy)
            offset += bytesToCopy
        }

        return derivedKey
    }

    /**
     * The core PBKDF2 function F, which computes the i-th block of the derived key.
     * F(Password, Salt, c, i) = U_1 XOR U_2 XOR ... XOR U_c
     * This function is self-contained and manages its own HMAC instance to prevent state issues.
     */
    private fun F(password: ByteArray, salt: ByteArray, iterations: Int, blockIndex: Int): ByteArray {
        // Instantiate the HMAC with the key (password) for this block's calculation.
        val hmac = HmacSHA256(password)

        // Prepare the initial input for the first HMAC: salt || INT_32_BE(blockIndex)
        val saltWithIndex = salt + intToBigEndianBytes(blockIndex)

        // U_1 = HMAC(Password, Salt || INT(i))
        var u = hmac.doFinal(saltWithIndex)

        // This will hold the XOR sum of all U's
        val result = u.copyOf()

        // Loop for U_2 up to U_c
        for (j in 2..iterations) {
            // U_j = HMAC(Password, U_{j-1})
            u = hmac.doFinal(u)

            // XOR the new U into the result
            for (k in result.indices) {
                result[k] = result[k] xor u[k]
            }
        }

        return result
    }

    private fun intToBigEndianBytes(value: Int): ByteArray {
        return byteArrayOf(
            (value ushr 24).toByte(),
            (value ushr 16).toByte(),
            (value ushr 8).toByte(),
            value.toByte()
        )
    }
}