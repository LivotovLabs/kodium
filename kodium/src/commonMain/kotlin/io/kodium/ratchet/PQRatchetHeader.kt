package io.kodium.ratchet

import io.kodium.KodiumPublicKey
import io.kodium.core.MLKEM

/**
 * Represents the plaintext header attached to every message sent within a Post-Quantum
 * Double Ratchet session.
 *
 * Unlike a classical RatchetHeader (which is 40 bytes), the PQRatchetHeader is significantly
 * larger (typically ~1,160 bytes) because it must carry the ML-KEM ciphertext required to
 * perform the quantum-resistant asymmetric ratchet step.
 *
 * @property dh The sender's current ephemeral public unified ratchet key for the DH ratchet step.
 * @property kemCiphertext The ML-KEM ciphertext encapsulated to the recipient's long-term PQC public key.
 * @property pn The length of the previous sending chain (used to calculate how many messages to skip).
 * @property n The sequential message number within the current sending chain.
 */
data class PQRatchetHeader(
    val dh: KodiumPublicKey,
    val kemCiphertext: ByteArray,
    val pn: Int,
    val n: Int
) {
    /**
     * Serializes the header into a byte array suitable for transmission and for inclusion
     * as Associated Data (AD) during AEAD encryption.
     *
     * Format: `[64-byte unified DH Key][1088-byte ML-KEM Ciphertext][4-byte PN][4-byte N]`
     */
    fun serialize(): ByteArray {
        val dhSize = 64
        val kemSize = MLKEM.CiphertextSize
        
        val bytes = ByteArray(dhSize + kemSize + 4 + 4)
        
        dh.encryptionKey.copyInto(bytes, 0)
        dh.signingKey.copyInto(bytes, 32)
        kemCiphertext.copyInto(bytes, dhSize)
        
        val offsetPn = dhSize + kemSize
        bytes[offsetPn] = (pn shr 24).toByte()
        bytes[offsetPn + 1] = (pn shr 16).toByte()
        bytes[offsetPn + 2] = (pn shr 8).toByte()
        bytes[offsetPn + 3] = pn.toByte()

        val offsetN = offsetPn + 4
        bytes[offsetN] = (n shr 24).toByte()
        bytes[offsetN + 1] = (n shr 16).toByte()
        bytes[offsetN + 2] = (n shr 8).toByte()
        bytes[offsetN + 3] = n.toByte()

        return bytes
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PQRatchetHeader) return false

        if (dh != other.dh) return false
        if (!kemCiphertext.contentEquals(other.kemCiphertext)) return false
        if (pn != other.pn) return false
        if (n != other.n) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dh.hashCode()
        result = 31 * result + kemCiphertext.contentHashCode()
        result = 31 * result + pn
        result = 31 * result + n
        return result
    }

    companion object {
        /**
         * Deserializes a byte array back into a [PQRatchetHeader].
         *
         * @param bytes The serialized header array.
         * @return The parsed [PQRatchetHeader] object.
         * @throws IllegalArgumentException if the provided byte array is smaller than the required size.
         */
        fun deserialize(bytes: ByteArray): PQRatchetHeader {
            val dhSize = 64
            val kemSize = MLKEM.CiphertextSize
            val expectedSize = dhSize + kemSize + 8
            
            require(bytes.size >= expectedSize) { "Header size must be at least $expectedSize bytes" }
            
            val encryptionKey = bytes.sliceArray(0 until 32)
            val signingKey = bytes.sliceArray(32 until 64)
            val dh = KodiumPublicKey(encryptionKey, signingKey)
            
            val kemCiphertext = bytes.sliceArray(dhSize until dhSize + kemSize)
            
            val offsetPn = dhSize + kemSize
            val pn = ((bytes[offsetPn].toInt() and 0xFF) shl 24) or
                     ((bytes[offsetPn + 1].toInt() and 0xFF) shl 16) or
                     ((bytes[offsetPn + 2].toInt() and 0xFF) shl 8) or
                     (bytes[offsetPn + 3].toInt() and 0xFF)
                     
            val offsetN = offsetPn + 4
            val n = ((bytes[offsetN].toInt() and 0xFF) shl 24) or
                    ((bytes[offsetN + 1].toInt() and 0xFF) shl 16) or
                    ((bytes[offsetN + 2].toInt() and 0xFF) shl 8) or
                    (bytes[offsetN + 3].toInt() and 0xFF)
            
            return PQRatchetHeader(dh, kemCiphertext, pn, n)
        }
    }
}

/**
 * A composite structure containing both the plaintext PQC Ratchet header and the encrypted payload.
 *
 * @property header The plaintext [PQRatchetHeader] required for the recipient to process the message.
 * @property ciphertext The AEAD encrypted message payload.
 */
data class PQRatchetMessage(
    val header: PQRatchetHeader,
    val ciphertext: ByteArray
) {
    /**
     * Serializes the entire message (header + ciphertext) into a single byte array for network transmission.
     */
    fun serialize(): ByteArray {
        val headerBytes = header.serialize()
        return headerBytes + ciphertext
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PQRatchetMessage) return false

        if (header != other.header) return false
        if (!ciphertext.contentEquals(other.ciphertext)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = header.hashCode()
        result = 31 * result + ciphertext.contentHashCode()
        return result
    }

    companion object {
        /**
         * Parses a serialized message byte array back into a [PQRatchetMessage].
         */
        fun deserialize(bytes: ByteArray): PQRatchetMessage {
            val header = PQRatchetHeader.deserialize(bytes)
            val headerSize = 64 + MLKEM.CiphertextSize + 8
            val ciphertext = bytes.sliceArray(headerSize until bytes.size)
            return PQRatchetMessage(header, ciphertext)
        }
    }
}
