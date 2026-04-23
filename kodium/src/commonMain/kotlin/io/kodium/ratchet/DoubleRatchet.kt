package io.kodium.ratchet

import io.kodium.KodiumPrivateKey
import io.kodium.KodiumPublicKey
import io.kodium.core.decodeBase58WithChecksum
import io.kodium.core.encodeToBase58WithChecksum
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256

/**
 * Represents the plaintext header attached to every message sent within a Double Ratchet session.
 *
 * This header contains the necessary information for the recipient to track message ordering,
 * detect skipped/lost messages, and execute the Diffie-Hellman (DH) ratchet step when the sender
 * changes their public key.
 *
 * @property dh The sender's current public Curve25519 ratchet key for the Diffie-Hellman ratchet.
 * @property pn The length of the previous sending chain (used to calculate how many messages to skip).
 * @property n The sequential message number within the current sending chain.
 */
data class RatchetHeader(
    val dh: KodiumPublicKey,
    val pn: Int,
    val n: Int
) {
    /**
     * Serializes the header into a byte array suitable for transmission and for inclusion
     * as Associated Data (AD) during AEAD encryption.
     *
     * @return A byte array containing the serialized header `[64-byte unified DH Key][4-byte PN][4-byte N]`.
     */
    fun serialize(): ByteArray {
        val bytes = ByteArray(64 + 4 + 4)
        dh.encryptionKey.copyInto(bytes, 0)
        dh.signingKey.copyInto(bytes, 32)
        
        bytes[64] = (pn shr 24).toByte()
        bytes[65] = (pn shr 16).toByte()
        bytes[66] = (pn shr 8).toByte()
        bytes[67] = pn.toByte()

        bytes[68] = (n shr 24).toByte()
        bytes[69] = (n shr 16).toByte()
        bytes[70] = (n shr 8).toByte()
        bytes[71] = n.toByte()

        return bytes
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RatchetHeader) return false

        if (dh != other.dh) return false
        if (pn != other.pn) return false
        if (n != other.n) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dh.hashCode()
        result = 31 * result + pn
        result = 31 * result + n
        return result
    }

    companion object {
        /**
         * Deserializes a byte array back into a [RatchetHeader].
         *
         * @param bytes The 72-byte serialized header array.
         * @return The parsed [RatchetHeader] object.
         * @throws IllegalArgumentException if the provided byte array is smaller than 72 bytes.
         */
        fun deserialize(bytes: ByteArray): RatchetHeader {
            require(bytes.size >= 72) { "Header size must be at least 72 bytes" }
            val encryptionKey = bytes.sliceArray(0 until 32)
            val signingKey = bytes.sliceArray(32 until 64)
            val dh = KodiumPublicKey(encryptionKey, signingKey)
            
            val pn = ((bytes[64].toInt() and 0xFF) shl 24) or
                     ((bytes[65].toInt() and 0xFF) shl 16) or
                     ((bytes[66].toInt() and 0xFF) shl 8) or
                     (bytes[67].toInt() and 0xFF)
                     
            val n = ((bytes[68].toInt() and 0xFF) shl 24) or
                    ((bytes[69].toInt() and 0xFF) shl 16) or
                    ((bytes[70].toInt() and 0xFF) shl 8) or
                    (bytes[71].toInt() and 0xFF)
            
            return RatchetHeader(dh, pn, n)
        }
    }
}

/**
 * A composite structure containing both the plaintext Ratchet header and the encrypted payload.
 *
 * @property header The plaintext [RatchetHeader] required for the recipient to process the message.
 * @property ciphertext The AEAD encrypted message payload.
 */
data class RatchetMessage(
    val header: RatchetHeader,
    val ciphertext: ByteArray
) {
    /**
     * Serializes the entire message (header + ciphertext) into a single byte array for network transmission.
     */
    fun serialize(): ByteArray {
        val headerBytes = header.serialize()
        return headerBytes + ciphertext
    }

    companion object {
        /**
         * Parses a serialized message byte array back into a [RatchetMessage].
         */
        fun deserialize(bytes: ByteArray): RatchetMessage {
            val header = RatchetHeader.deserialize(bytes)
            val ciphertext = bytes.sliceArray(72 until bytes.size)
            return RatchetMessage(header, ciphertext)
        }
    }
}

/**
 * The core implementation of the Double Ratchet Algorithm.
 *
 * The Double Ratchet algorithm is used by two parties to exchange encrypted messages based on a
 * shared secret key. It combines a cryptographic "Diffie-Hellman ratchet" with a "symmetric-key ratchet"
 * to provide both forward secrecy (compromise of current keys does not compromise past messages) and
 * break-in recovery (compromise of current keys does not compromise future messages).
 *
 * Instances of this class are stateful and must be securely persisted between application restarts
 * using [exportToEncryptedString] and [importFromEncryptedString].
 */
class DoubleRatchetSession private constructor(
    private var DHs: KodiumPrivateKey,
    private var DHr: KodiumPublicKey?,
    private var RK: ByteArray,
    private var CKs: ByteArray?,
    private var CKr: ByteArray?,
    private var Ns: Int = 0,
    private var Nr: Int = 0,
    private var PN: Int = 0,
    private val MKSKIPPED: MutableMap<String, ByteArray> = LinkedHashMap(),
    private val applicationInfo: ByteArray = DEFAULT_INFO_KDF_RK,
    val maxSkippedMessages: Int = DEFAULT_MAX_SKIPPED_MESSAGES
) {
    companion object {
        // HKDF Info Constants
        private val DEFAULT_INFO_KDF_RK = "KodiumKdfRk".encodeToByteArray()

        private const val MAX_SKIP = 1000
        const val DEFAULT_MAX_SKIPPED_MESSAGES = 2000

        /**
         * Initializes a new Double Ratchet session for the Initiator (the party who sends the first message).
         *
         * @param sharedSecret The 32-byte master shared secret, typically derived from an X3DH key agreement.
         * @param responderRatchetKey The Responder's public unified ratchet key (often their Signed PreKey).
         * @param applicationInfo Optional context-binding string. MUST match the string used by the Responder
         *                        to prevent cross-protocol attacks.
         * @param maxSkippedMessages The maximum number of skipped message keys to store in memory (default 2000).
         *                           When exceeded, the oldest keys are evicted first (LRU behavior) preventing unbounded memory growth.
         * @return A newly initialized [DoubleRatchetSession] ready to encrypt messages.
         */
        fun initializeAsInitiator(
            sharedSecret: ByteArray,
            responderRatchetKey: KodiumPublicKey,
            applicationInfo: ByteArray = DEFAULT_INFO_KDF_RK,
            maxSkippedMessages: Int = DEFAULT_MAX_SKIPPED_MESSAGES
        ): DoubleRatchetSession {
            val dhs = KodiumPrivateKey.generate()
            
            val kdfResult = HKDF.deriveSecrets(
                salt = sharedSecret,
                ikm = RatchetUtils.dh(dhs.secretKey, responderRatchetKey.encryptionKey),
                info = applicationInfo,
                length = 64
            )

            val rk = kdfResult.sliceArray(0 until 32)
            val cks = kdfResult.sliceArray(32 until 64)

            return DoubleRatchetSession(
                DHs = dhs,
                DHr = responderRatchetKey,
                RK = rk,
                CKs = cks,
                CKr = null,
                applicationInfo = applicationInfo,
                maxSkippedMessages = maxSkippedMessages
            )
        }

        /**
         * Initializes a new Double Ratchet session for the Responder (the party who receives the first message).
         *
         * @param sharedSecret The 32-byte master shared secret, typically derived from an X3DH key agreement.
         * @param responderRatchetKeypair The Responder's ratchet keypair (often their Signed PreKey pair).
         * @param applicationInfo Optional context-binding string. MUST match the string used by the Initiator
         *                        to prevent cross-protocol attacks.
         * @param maxSkippedMessages The maximum number of skipped message keys to store in memory (default 2000).
         *                           When exceeded, the oldest keys are evicted first (LRU behavior) preventing unbounded memory growth.
         * @return A newly initialized [DoubleRatchetSession] ready to decrypt incoming messages.
         */
        fun initializeAsResponder(
            sharedSecret: ByteArray,
            responderRatchetKeypair: KodiumPrivateKey,
            applicationInfo: ByteArray = DEFAULT_INFO_KDF_RK,
            maxSkippedMessages: Int = DEFAULT_MAX_SKIPPED_MESSAGES
        ): DoubleRatchetSession {
            return DoubleRatchetSession(
                DHs = responderRatchetKeypair,
                DHr = null,
                RK = sharedSecret,
                CKs = null,
                CKr = null,
                applicationInfo = applicationInfo,
                maxSkippedMessages = maxSkippedMessages
            )
        }

        /**
         * Safely imports a previously saved Double Ratchet Session from an encrypted, Base58-encoded string.
         *
         * @param data The encrypted, Base58-encoded session state string.
         * @param password The secret password used to encrypt the session state during export.
         * @return A `Result` containing the restored [DoubleRatchetSession], or an error if decryption/parsing fails.
         */
        fun importFromEncryptedString(data: String, password: String, keyDerivationIterations: Int = io.kodium.Kodium.PBKDF2_ITERATIONS): Result<DoubleRatchetSession> {
            return io.kodium.Kodium.decryptSymmetricFromEncodedString(password, data, keyDerivationIterations).mapCatching { bytes ->
                importFromArray(bytes).getOrThrow()
            }
        }

        /**
         * Safely imports a previously saved Double Ratchet Session from an encrypted, Base58-encoded string.
         *
         * @param data The encrypted, Base58-encoded session state string.
         * @param key A precomputed 32-byte symmetric key used to encrypt the session state during export.
         * @return A `Result` containing the restored [DoubleRatchetSession], or an error if decryption/parsing fails.
         */
        fun importFromEncryptedString(data: String, key: ByteArray): Result<DoubleRatchetSession> {
            return io.kodium.Kodium.decryptSymmetricFromEncodedString(key, data).mapCatching { bytes ->
                importFromArray(bytes).getOrThrow()
            }
        }

        /**
         * Imports a Double Ratchet Session from a raw, unprotected ByteArray.
         *
         * @param bytes The unencrypted serialized state of the session.
         * @return A `Result` containing the restored [DoubleRatchetSession], or an error if parsing fails.
         */
        fun importFromArray(bytes: ByteArray): Result<DoubleRatchetSession> {
            return runCatching {
                val reader = ByteReader(bytes)
                
                val dhs = KodiumPrivateKey.fromRaw(reader.readBytes(32))
                
                val hasDHr = reader.readByte() == 1.toByte()
                val dhr = if (hasDHr) {
                    val encryptionKey = reader.readBytes(32)
                    val signingKey = reader.readBytes(32)
                    KodiumPublicKey(encryptionKey, signingKey)
                } else null
                
                val rk = reader.readBytes(32)
                
                val hasCKs = reader.readByte() == 1.toByte()
                val cks = if (hasCKs) reader.readBytes(32) else null
                
                val hasCKr = reader.readByte() == 1.toByte()
                val ckr = if (hasCKr) reader.readBytes(32) else null
                
                val ns = reader.readInt()
                val nr = reader.readInt()
                val pn = reader.readInt()
                
                val skippedSize = reader.readInt()
                val mkSkipped = mutableMapOf<String, ByteArray>()
                for (i in 0 until skippedSize) {
                    val keyLen = reader.readInt()
                    val keyStr = reader.readBytes(keyLen).decodeToString()
                    val value = reader.readBytes(32)
                    mkSkipped[keyStr] = value
                }

                val appInfoLen = reader.readInt()
                val appInfo = reader.readBytes(appInfoLen)
                
                // Read optional maxSkippedMessages if available in newer serializations
                val maxSkipped = try {
                    reader.readInt()
                } catch (e: IllegalStateException) {
                    DEFAULT_MAX_SKIPPED_MESSAGES
                }
                
                DoubleRatchetSession(
                    DHs = dhs,
                    DHr = dhr,
                    RK = rk,
                    CKs = cks,
                    CKr = ckr,
                    Ns = ns,
                    Nr = nr,
                    PN = pn,
                    MKSKIPPED = mkSkipped,
                    applicationInfo = appInfo,
                    maxSkippedMessages = maxSkipped
                )
            }
        }
    }

    /**
     * Exports the entire internal state of the current session to a securely encrypted, Base58-encoded string.
     * 
     * This method is intended to be used to persist the session state to local storage (e.g., a database)
     * between application restarts. The provided password is used to encrypt the blob using Kodium's
     * symmetric encryption layer (XSalsa20 + Poly1305 via PBKDF2).
     *
     * @param password A strong, secret password to encrypt the exported state.
     * @return A `Result` containing the Base58-encoded string on success.
     */
    fun exportToEncryptedString(password: String, keyDerivationIterations: Int = io.kodium.Kodium.PBKDF2_ITERATIONS): Result<String> {
        return try {
            io.kodium.Kodium.encryptSymmetricToEncodedString(password, exportToArray(), keyDerivationIterations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Exports the entire internal state of the current session to a securely encrypted, Base58-encoded string
     * using a precomputed symmetric key.
     * 
     * @param key A precomputed 32-byte symmetric key used to encrypt the exported state.
     * @return A `Result` containing the Base58-encoded string on success.
     */
    fun exportToEncryptedString(key: ByteArray): Result<String> {
        return try {
            io.kodium.Kodium.encryptSymmetricToEncodedString(key, exportToArray())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Exports the raw, unprotected internal state of the session to a ByteArray.
     * This is useful when the application manages its own state encryption or storage protection.
     * 
     * @return A ByteArray containing the serialized internal state.
     */
    fun exportToArray(): ByteArray {
        val writer = ByteWriter()
        writer.write(DHs.secretKey)
        
        if (DHr != null) {
            writer.write(1.toByte())
            writer.write(DHr!!.encryptionKey)
            writer.write(DHr!!.signingKey)
        } else {
            writer.write(0.toByte())
        }
        
        writer.write(RK)
        
        if (CKs != null) {
            writer.write(1.toByte())
            writer.write(CKs!!)
        } else {
            writer.write(0.toByte())
        }
        
        if (CKr != null) {
            writer.write(1.toByte())
            writer.write(CKr!!)
        } else {
            writer.write(0.toByte())
        }
        
        writer.writeInt(Ns)
        writer.writeInt(Nr)
        writer.writeInt(PN)
        
        writer.writeInt(MKSKIPPED.size)
        for ((key, value) in MKSKIPPED) {
            val keyBytes = key.encodeToByteArray()
            writer.writeInt(keyBytes.size)
            writer.write(keyBytes)
            writer.write(value)
        }

        writer.writeInt(applicationInfo.size)
        writer.write(applicationInfo)
        
        writer.writeInt(maxSkippedMessages)
        
        return writer.toByteArray()
    }

    private fun kdfRk(rk: ByteArray, dhOut: ByteArray): Pair<ByteArray, ByteArray> {
        val result = HKDF.deriveSecrets(
            salt = rk,
            ikm = dhOut,
            info = applicationInfo,
            length = 64
        )
        return Pair(result.sliceArray(0 until 32), result.sliceArray(32 until 64))
    }

    private fun kdfCk(ck: ByteArray): Pair<ByteArray, ByteArray> {
        val hmac = HmacSHA256(ck)
        val mac = hmac.doFinal(byteArrayOf(0x01))
        
        val hmac2 = HmacSHA256(ck)
        val mac2 = hmac2.doFinal(byteArrayOf(0x02))

        return Pair(mac, mac2)
    }

    /**
     * Encrypts a plaintext message using the current symmetric sending chain.
     *
     * @param plaintext The raw byte array message to encrypt.
     * @param associatedData Optional Associated Data to authenticate alongside the message (e.g., protocol version).
     * @return A `Result` containing the structured [RatchetMessage] object.
     */
    fun encrypt(plaintext: ByteArray, associatedData: ByteArray = ByteArray(0)): Result<RatchetMessage> {
        return try {
            var cks = this.CKs ?: throw IllegalStateException("Sending chain not initialized")

            // KDF_CK(CKs)
            val (newCks, mk) = kdfCk(cks)
            this.CKs = newCks

            val header = RatchetHeader(this.DHs.getPublicKey(), this.PN, this.Ns)
            this.Ns += 1

            val ad = associatedData + header.serialize()
            val ciphertext = RatchetUtils.aeadEncrypt(mk, plaintext, ad)

            Result.success(RatchetMessage(header, ciphertext))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Decrypts an incoming structured [RatchetMessage], automatically updating the session's internal state.
     * 
     * This handles Diffie-Hellman ratchet steps, advances the receiving symmetric chain, and manages
     * the storage and retrieval of out-of-order (skipped) message keys.
     *
     * @param message The parsed [RatchetMessage] to decrypt.
     * @param associatedData The exact Associated Data provided by the sender during encryption.
     * @return A `Result` containing the decrypted plaintext byte array.
     */
    fun decrypt(message: RatchetMessage, associatedData: ByteArray = ByteArray(0)): Result<ByteArray> {
        return try {
            val plaintext = trySkippedMessageKeys(message.header, message.ciphertext, associatedData)
            if (plaintext != null) {
                return Result.success(plaintext)
            }

            if (DHr == null || message.header.dh != DHr) {
                skipMessageKeys(message.header.pn)
                dhRatchet(message.header)
            }
            skipMessageKeys(message.header.n)
            
            var ckr = this.CKr ?: throw IllegalStateException("Receiving chain not initialized")
            val (newCkr, mk) = kdfCk(ckr)
            this.CKr = newCkr
            this.Nr += 1

            val ad = associatedData + message.header.serialize()
            val decrypted = RatchetUtils.aeadDecrypt(mk, message.ciphertext, ad)
                ?: throw IllegalStateException("Failed to decrypt message (MAC check failed)")
            Result.success(decrypted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * A convenience wrapper around [encrypt] that serializes the resulting message into a secure,
     * checksummed Base58 string suitable for immediate network transmission or storage.
     *
     * @param plaintext The raw byte array message to encrypt.
     * @param associatedData Optional Associated Data to authenticate.
     * @return A `Result` containing the Base58-encoded string representation of the encrypted message.
     */
    fun encryptToEncodedString(plaintext: ByteArray, associatedData: ByteArray = ByteArray(0)): Result<String> {
        return encrypt(plaintext, associatedData).mapCatching { it.serialize().encodeToBase58WithChecksum() }
    }

    /**
     * A convenience wrapper around [decrypt] that accepts a secure, checksummed Base58 string
     * representing the encrypted message (as produced by [encryptToEncodedString]).
     *
     * @param data The Base58-encoded encrypted string.
     * @param associatedData The exact Associated Data provided by the sender during encryption.
     * @return A `Result` containing the decrypted plaintext byte array.
     */
    fun decryptFromEncodedString(data: String, associatedData: ByteArray = ByteArray(0)): Result<ByteArray> {
        return try {
            val bytes = data.decodeBase58WithChecksum()
            val msg = RatchetMessage.deserialize(bytes)
            decrypt(msg, associatedData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun dhRatchet(header: RatchetHeader) {
        this.PN = this.Ns
        this.Ns = 0
        this.Nr = 0
        this.DHr = header.dh

        val (rk, ckr) = kdfRk(this.RK, RatchetUtils.dh(this.DHs.secretKey, this.DHr!!.encryptionKey))
        this.RK = rk
        this.CKr = ckr

        this.DHs = KodiumPrivateKey.generate()
        
        val (newRk, cks) = kdfRk(this.RK, RatchetUtils.dh(this.DHs.secretKey, this.DHr!!.encryptionKey))
        this.RK = newRk
        this.CKs = cks
    }

    private fun skipMessageKeys(until: Int) {
        if (this.Nr + MAX_SKIP < until) {
            throw IllegalArgumentException("Too many messages skipped")
        }
        
        var ckr = this.CKr ?: return
        
        while (this.Nr < until) {
            val (newCkr, mk) = kdfCk(ckr)
            ckr = newCkr
            this.MKSKIPPED[RatchetUtils.toHex(DHr!!.encryptionKey) + this.Nr] = mk
            
            // Enforce LRU Memory Limit
            if (this.MKSKIPPED.size > this.maxSkippedMessages) {
                // LinkedHashMap maintains insertion order; removing the first key evicts the oldest.
                val oldestKey = this.MKSKIPPED.keys.first()
                this.MKSKIPPED.remove(oldestKey)
            }
            
            this.Nr += 1
        }
        this.CKr = ckr
    }

    private fun trySkippedMessageKeys(header: RatchetHeader, ciphertext: ByteArray, associatedData: ByteArray): ByteArray? {
        val key = RatchetUtils.toHex(header.dh.encryptionKey) + header.n
        val mk = MKSKIPPED[key] ?: return null
        
        val ad = associatedData + header.serialize()
        val plaintext = RatchetUtils.aeadDecrypt(mk, ciphertext, ad)
        if (plaintext != null) {
            MKSKIPPED.remove(key)
        }
        return plaintext
    }
}
