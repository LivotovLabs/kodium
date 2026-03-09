package io.kodium.ratchet

import org.kotlincrypto.macs.hmac.sha2.HmacSHA256

/**
 * An implementation of the HMAC-based Extract-and-Expand Key Derivation Function (HKDF)
 * using HMAC-SHA256 as the underlying pseudorandom function (PRF).
 *
 * HKDF is used to take some initial keying material (IKM) that may contain some cryptographic
 * entropy, and derive one or more strong, uniformly distributed cryptographic keys. It consists
 * of two distinct steps: "extract" (which concentrates the entropy into a short pseudorandom key)
 * and "expand" (which expands that key into the desired output length).
 *
 * This implementation complies with RFC 5869.
 *
 * @see <a href="https://tools.ietf.org/html/rfc5869">RFC 5869</a>
 */
object HKDF {
    private const val HASH_LEN = 32 // SHA-256 output length in bytes

    /**
     * Step 1: Extract.
     * Computes a pseudorandom key (PRK) from the input keying material (IKM) and an optional salt.
     * The extraction step ensures the resulting PRK is cryptographically strong, even if the IKM
     * is not uniformly distributed.
     *
     * @param salt Optional salt value (a non-secret random value). If null or empty, a salt consisting
     *             of zeros (matching the hash length) is used.
     * @param ikm Input keying material.
     * @return A pseudorandom key (PRK) of 32 bytes (HMAC-SHA256 output length).
     */
    fun extract(salt: ByteArray?, ikm: ByteArray): ByteArray {
        val actualSalt = if (salt == null || salt.isEmpty()) ByteArray(HASH_LEN) else salt
        val hmac = HmacSHA256(actualSalt)
        return hmac.doFinal(ikm)
    }

    /**
     * Step 2: Expand.
     * Expands a pseudorandom key (PRK) into an Output Keying Material (OKM) of the specified length.
     * This step can be used to generate multiple derived keys from a single PRK by providing
     * different `info` context strings.
     *
     * @param prk A pseudorandom key of at least 32 bytes (usually the output from the [extract] method).
     * @param info Optional context and application-specific information. Used to bind the derived key
     *             to a specific protocol or application context.
     * @param length The desired length of the output keying material in bytes. Maximum allowed length
     *               is 255 * 32 bytes (8,160 bytes).
     * @return The generated Output Keying Material (OKM).
     * @throws IllegalArgumentException if the requested length exceeds the maximum allowed size.
     */
    fun expand(prk: ByteArray, info: ByteArray?, length: Int): ByteArray {
        require(length <= 255 * HASH_LEN) { "Length must be <= 255 * $HASH_LEN" }
        val actualInfo = info ?: ByteArray(0)
        
        val okm = ByteArray(length)
        var offset = 0
        var t = ByteArray(0)
        var blockIndex = 1

        while (offset < length) {
            val hmac = HmacSHA256(prk)
            hmac.update(t)
            hmac.update(actualInfo)
            hmac.update(byteArrayOf(blockIndex.toByte()))
            t = hmac.doFinal()

            val bytesToCopy = minOf(t.size, length - offset)
            t.copyInto(okm, offset, 0, bytesToCopy)
            offset += bytesToCopy
            blockIndex++
        }

        return okm
    }

    /**
     * Convenience method that performs both the Extract and Expand steps in a single operation.
     *
     * @param salt Optional salt value to strengthen the extraction phase.
     * @param ikm Input keying material containing the initial cryptographic entropy.
     * @param info Optional context and application-specific information to bind the resulting keys.
     * @param length The desired output length in bytes for the generated keying material.
     * @return The final Output Keying Material (OKM).
     */
    fun deriveSecrets(salt: ByteArray?, ikm: ByteArray, info: ByteArray?, length: Int): ByteArray {
        val prk = extract(salt, ikm)
        return expand(prk, info, length)
    }
}
