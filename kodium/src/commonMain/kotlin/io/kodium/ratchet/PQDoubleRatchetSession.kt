package io.kodium.ratchet

import io.kodium.KodiumPqcPrivateKey
import io.kodium.KodiumPqcPublicKey
import io.kodium.KodiumPrivateKey
import io.kodium.core.MLKEM
import io.kodium.core.decodeBase58WithChecksum
import io.kodium.core.encodeToBase58WithChecksum
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256

/**
 * The core implementation of the Post-Quantum Double Ratchet Algorithm.
 *
 * This variant extends the standard Double Ratchet algorithm to support a KEM-based
 * asymmetric ratchet using ML-KEM-768 alongside the standard X25519 DH ratchet.
 * 
 * Each ratchet step requires an ML-KEM encapsulation to the recipient's long-term
 * PQC public key, which produces a ciphertext (~1KB) that must be attached to the message header.
 */
class PQDoubleRatchetSession internal constructor(
    private val ourPqcKey: KodiumPqcPrivateKey,
    private val theirPqcPublicKey: KodiumPqcPublicKey,
    private var DHs: KodiumPrivateKey,
    private var CTs: ByteArray?,
    private var DHr: ByteArray?,
    private var RK: ByteArray,
    private var CKs: ByteArray?,
    private var CKr: ByteArray?,
    private var Ns: Int = 0,
    private var Nr: Int = 0,
    private var PN: Int = 0,
    private val MKSKIPPED: MutableMap<String, ByteArray> = mutableMapOf(),
    private val applicationInfo: ByteArray = DEFAULT_INFO_KDF_RK
) {
    companion object {
        // HKDF Info Constants
        private val DEFAULT_INFO_KDF_RK = "KodiumKdfRk".encodeToByteArray()

        private const val MAX_SKIP = 1000

        /**
         * Initializes a new PQ Double Ratchet session for the Initiator.
         *
         * @param sharedSecret The 32-byte master shared secret, derived from PQXDH.
         * @param responderPqcPublicKey The Responder's long-term PQC public key.
         * @param ourPqcPrivateKey The Initiator's long-term PQC private key.
         * @param applicationInfo Optional context-binding string.
         */
        fun initializeAsInitiator(
            sharedSecret: ByteArray,
            responderPqcPublicKey: KodiumPqcPublicKey,
            ourPqcPrivateKey: KodiumPqcPrivateKey,
            applicationInfo: ByteArray = DEFAULT_INFO_KDF_RK
        ): PQDoubleRatchetSession {
            val dhs = KodiumPrivateKey.generate()
            
            // PQC Encapsulation for the first ratchet step
            val (sharedSecretPqc, kemCiphertext) = MLKEM.encapsulate(responderPqcPublicKey.pqcPublicKey)
            
            val dhOut = RatchetUtils.dh(dhs.secretKey, responderPqcPublicKey.classicalPublicKey)
            
            val kdfResult = HKDF.deriveSecrets(
                salt = sharedSecret,
                ikm = dhOut + sharedSecretPqc,
                info = applicationInfo,
                length = 64
            )

            val rk = kdfResult.sliceArray(0 until 32)
            val cks = kdfResult.sliceArray(32 until 64)

            return PQDoubleRatchetSession(
                ourPqcKey = ourPqcPrivateKey,
                theirPqcPublicKey = responderPqcPublicKey,
                DHs = dhs,
                CTs = kemCiphertext,
                DHr = responderPqcPublicKey.classicalPublicKey,
                RK = rk,
                CKs = cks,
                CKr = null,
                applicationInfo = applicationInfo
            )
        }

        /**
         * Initializes a new PQ Double Ratchet session for the Responder.
         *
         * @param sharedSecret The 32-byte master shared secret, derived from PQXDH.
         * @param ourPqcPrivateKey The Responder's long-term PQC private key.
         * @param initiatorPqcPublicKey The Initiator's long-term PQC public key.
         * @param applicationInfo Optional context-binding string.
         */
        fun initializeAsResponder(
            sharedSecret: ByteArray,
            ourPqcPrivateKey: KodiumPqcPrivateKey,
            initiatorPqcPublicKey: KodiumPqcPublicKey,
            applicationInfo: ByteArray = DEFAULT_INFO_KDF_RK
        ): PQDoubleRatchetSession {
            val dhs = KodiumPrivateKey.fromRaw(ourPqcPrivateKey.classicalSecretKey)
            
            return PQDoubleRatchetSession(
                ourPqcKey = ourPqcPrivateKey,
                theirPqcPublicKey = initiatorPqcPublicKey,
                DHs = dhs,
                CTs = null,
                DHr = null,
                RK = sharedSecret,
                CKs = null,
                CKr = null,
                applicationInfo = applicationInfo
            )
        }

        /**
         * Initializes a new PQ Double Ratchet session for the Initiator using deterministic KEM encapsulation.
         * 
         * **WARNING: This function is intended for deterministic testing ONLY.**
         */

        fun importFromEncryptedString(data: String, password: String, keyDerivationIterations: Int = io.kodium.Kodium.PBKDF2_ITERATIONS): Result<PQDoubleRatchetSession> {
            return io.kodium.Kodium.decryptSymmetricFromEncodedString(password, data, keyDerivationIterations).mapCatching { bytes ->
                val reader = ByteReader(bytes)
                
                // Read ourPqcKey
                val ourClassicalSk = reader.readBytes(32)
                val ourPqcSk = reader.readBytes(MLKEM.SecretKeySize)
                val ourPqcKey = KodiumPqcPrivateKey.fromRaw(ourClassicalSk, ourPqcSk)

                // Read theirPqcPublicKey
                val theirClassicalPk = reader.readBytes(32)
                val theirPqcPk = reader.readBytes(MLKEM.PublicKeySize)
                val theirPqcPublicKey = KodiumPqcPublicKey(theirClassicalPk, theirPqcPk)
                
                val dhs = KodiumPrivateKey.fromRaw(reader.readBytes(32))
                
                val hasCTs = reader.readByte() == 1.toByte()
                val cts = if (hasCTs) reader.readBytes(MLKEM.CiphertextSize) else null
                
                val hasDHr = reader.readByte() == 1.toByte()
                val dhr = if (hasDHr) reader.readBytes(32) else null
                
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
                
                PQDoubleRatchetSession(
                    ourPqcKey = ourPqcKey,
                    theirPqcPublicKey = theirPqcPublicKey,
                    DHs = dhs,
                    CTs = cts,
                    DHr = dhr,
                    RK = rk,
                    CKs = cks,
                    CKr = ckr,
                    Ns = ns,
                    Nr = nr,
                    PN = pn,
                    MKSKIPPED = mkSkipped,
                    applicationInfo = appInfo
                )
            }
        }
    }

    fun exportToEncryptedString(password: String, keyDerivationIterations: Int = io.kodium.Kodium.PBKDF2_ITERATIONS): Result<String> {
        return try {
            val writer = ByteWriter()
            
            // ourPqcKey
            writer.write(ourPqcKey.classicalSecretKey)
            writer.write(ourPqcKey.pqcSecretKey)
            
            // theirPqcPublicKey
            writer.write(theirPqcPublicKey.classicalPublicKey)
            writer.write(theirPqcPublicKey.pqcPublicKey)
            
            writer.write(DHs.secretKey)
            
            if (CTs != null) {
                writer.write(1.toByte())
                writer.write(CTs!!)
            } else {
                writer.write(0.toByte())
            }
            
            if (DHr != null) {
                writer.write(1.toByte())
                writer.write(DHr!!)
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
            
            io.kodium.Kodium.encryptSymmetricToEncodedString(password, writer.toByteArray(), keyDerivationIterations)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    fun encrypt(plaintext: ByteArray, associatedData: ByteArray = ByteArray(0)): Result<PQRatchetMessage> {
        return try {
            var cks = this.CKs ?: throw IllegalStateException("Sending chain not initialized")

            val (newCks, mk) = kdfCk(cks)
            this.CKs = newCks

            val cts = this.CTs ?: throw IllegalStateException("CTs (ML-KEM Ciphertext) not initialized")
            val header = PQRatchetHeader(this.DHs.publicKey, cts, this.PN, this.Ns)
            this.Ns += 1

            val ad = associatedData + header.serialize()
            val ciphertext = RatchetUtils.aeadEncrypt(mk, plaintext, ad)

            Result.success(PQRatchetMessage(header, ciphertext))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun decrypt(message: PQRatchetMessage, associatedData: ByteArray = ByteArray(0)): Result<ByteArray> {
        return try {
            val plaintext = trySkippedMessageKeys(message.header, message.ciphertext, associatedData)
            if (plaintext != null) {
                return Result.success(plaintext)
            }

            if (DHr == null || !message.header.dh.contentEquals(DHr!!)) {
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

    fun encryptToEncodedString(plaintext: ByteArray, associatedData: ByteArray = ByteArray(0)): Result<String> {
        return encrypt(plaintext, associatedData).mapCatching { it.serialize().encodeToBase58WithChecksum() }
    }

    fun decryptFromEncodedString(data: String, associatedData: ByteArray = ByteArray(0)): Result<ByteArray> {
        return try {
            val bytes = data.decodeBase58WithChecksum()
            val msg = PQRatchetMessage.deserialize(bytes)
            decrypt(msg, associatedData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun dhRatchet(header: PQRatchetHeader) {
        this.PN = this.Ns
        this.Ns = 0
        this.Nr = 0
        this.DHr = header.dh

        // 1. Process received DH & KEM step
        val sharedSecretPqcR = MLKEM.decapsulate(header.kemCiphertext, this.ourPqcKey.pqcSecretKey)
            ?: throw IllegalStateException("ML-KEM decapsulation failed")

        val dhR = RatchetUtils.dh(this.DHs.secretKey, this.DHr!!)
        val (rk, ckr) = kdfRk(this.RK, dhR + sharedSecretPqcR)
        this.RK = rk
        this.CKr = ckr

        // 2. Perform our new DH & KEM step
        this.DHs = KodiumPrivateKey.generate()
        val (sharedSecretPqcS, kemCiphertextS) = MLKEM.encapsulate(this.theirPqcPublicKey.pqcPublicKey)
        this.CTs = kemCiphertextS

        val dhS = RatchetUtils.dh(this.DHs.secretKey, this.DHr!!)
        val (newRk, cks) = kdfRk(this.RK, dhS + sharedSecretPqcS)
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
            this.MKSKIPPED[RatchetUtils.toHex(DHr!!) + this.Nr] = mk
            this.Nr += 1
        }
        this.CKr = ckr
    }

    private fun trySkippedMessageKeys(header: PQRatchetHeader, ciphertext: ByteArray, associatedData: ByteArray): ByteArray? {
        val key = RatchetUtils.toHex(header.dh) + header.n
        val mk = MKSKIPPED[key] ?: return null
        
        val ad = associatedData + header.serialize()
        val plaintext = RatchetUtils.aeadDecrypt(mk, ciphertext, ad)
        if (plaintext != null) {
            MKSKIPPED.remove(key)
        }
        return plaintext
    }
}
