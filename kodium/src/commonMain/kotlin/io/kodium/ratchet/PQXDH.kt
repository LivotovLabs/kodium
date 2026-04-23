package io.kodium.ratchet

import io.kodium.KodiumPqcPrivateKey
import io.kodium.KodiumPqcPublicKey
import io.kodium.KodiumPrivateKey
import io.kodium.KodiumPublicKey
import io.kodium.core.MLKEM
import io.kodium.core.decodeBase58WithChecksum
import io.kodium.core.encodeToBase58WithChecksum

/**
 * PQXDH (Post-Quantum Extended Triple Diffie-Hellman) Key Agreement Protocol.
 * 
 * Mixes standard X25519 DH secrets with an ML-KEM encapsulation to provide 
 * quantum-resistant initial key agreement.
 */
object PQXDH {

    private val INFO_PQXDH = "KodiumPQXDH".encodeToByteArray()

    /**
     * Responder's public bundle for PQXDH.
     * @property identityKey Responder's long-term classical identity public key.
     * @property pqcKey Responder's long-term hybrid public key. The classical portion acts as the Signed PreKey.
     * @property oneTimePreKey Responder's classical one-time pre-key public key (optional).
     * @property pqcOneTimePreKey Responder's hybrid one-time pre-key public key (optional).
     */
    data class PublicBundle(
        val identityKey: KodiumPublicKey,
        val pqcKey: KodiumPqcPublicKey,
        val oneTimePreKey: KodiumPublicKey? = null,
        val pqcOneTimePreKey: KodiumPqcPublicKey? = null
    ) {
        /**
         * Exports this PublicBundle to a Base58-encoded string with a checksum.
         */
        fun exportToEncodedString(): Result<String> {
            return try {
                val writer = ByteWriter()
                writer.write(identityKey.encryptionKey)
                writer.write(identityKey.signingKey)
                writer.write(pqcKey.classicalPublicKey)
                writer.write(pqcKey.classicalSignPublicKey)
                writer.write(pqcKey.pqcPublicKey)
                if (oneTimePreKey != null) {
                    writer.write(1.toByte())
                    writer.write(oneTimePreKey.encryptionKey)
                    writer.write(oneTimePreKey.signingKey)
                } else {
                    writer.write(0.toByte())
                }
                if (pqcOneTimePreKey != null) {
                    writer.write(1.toByte())
                    writer.write(pqcOneTimePreKey.classicalPublicKey)
                    writer.write(pqcOneTimePreKey.classicalSignPublicKey)
                    writer.write(pqcOneTimePreKey.pqcPublicKey)
                } else {
                    writer.write(0.toByte())
                }
                Result.success(writer.toByteArray().encodeToBase58WithChecksum())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PublicBundle) return false

            if (identityKey != other.identityKey) return false
            if (pqcKey != other.pqcKey) return false
            if (oneTimePreKey != other.oneTimePreKey) return false
            if (pqcOneTimePreKey != other.pqcOneTimePreKey) return false

            return true
        }

        override fun hashCode(): Int {
            var result = identityKey.hashCode()
            result = 31 * result + pqcKey.hashCode()
            result = 31 * result + (oneTimePreKey?.hashCode() ?: 0)
            result = 31 * result + (pqcOneTimePreKey?.hashCode() ?: 0)
            return result
        }

        companion object {
            /**
             * Imports a PublicBundle from a Base58-encoded string with a checksum.
             */
            fun importFromEncodedString(data: String): Result<PublicBundle> {
                return try {
                    val bytes = data.decodeBase58WithChecksum()
                    val reader = ByteReader(bytes)
                    
                    val identityKey = KodiumPublicKey(reader.readBytes(32), reader.readBytes(32))
                    val classicalPqcKey = reader.readBytes(32)
                    val classicalSignPqcKey = reader.readBytes(32)
                    val pqcKeyBytes = reader.readBytes(MLKEM.PublicKeySize)
                    val pqcKey = KodiumPqcPublicKey(classicalPqcKey, classicalSignPqcKey, pqcKeyBytes)
                    
                    val hasOneTime = reader.readByte() == 1.toByte()
                    val oneTimePreKey = if (hasOneTime) KodiumPublicKey(reader.readBytes(32), reader.readBytes(32)) else null
                    
                    val hasPqcOneTime = try {
                        reader.readByte() == 1.toByte()
                    } catch (e: IllegalStateException) {
                        false
                    }
                    val pqcOneTimePreKey = if (hasPqcOneTime) {
                        val classicalKey = reader.readBytes(32)
                        val classicalSignKey = reader.readBytes(32)
                        val pqcBytes = reader.readBytes(MLKEM.PublicKeySize)
                        KodiumPqcPublicKey(classicalKey, classicalSignKey, pqcBytes)
                    } else null
                    
                    Result.success(PublicBundle(identityKey, pqcKey, oneTimePreKey, pqcOneTimePreKey))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }

    /**
     * Payload generated by the Initiator to send to the Responder to establish the PQ session.
     * Contains the classical public keys and the ML-KEM ciphertext.
     * 
     * @property identityKey Initiator's long-term classical identity public key.
     * @property ephemeralKey Initiator's ephemeral classical public key.
     * @property kemCiphertext The ML-KEM encapsulated shared secret.
     * @property pqcPublicKey Optional: Initiator's hybrid public key to allow immediate PQC replies.
     * @property kemCiphertext2 Optional: The second ML-KEM encapsulated shared secret (against PQ OPK).
     */
    data class PQInitiatorPayload(
        val identityKey: KodiumPublicKey,
        val ephemeralKey: KodiumPublicKey,
        val kemCiphertext: ByteArray,
        val pqcPublicKey: KodiumPqcPublicKey? = null,
        val kemCiphertext2: ByteArray? = null
    ) {
        fun exportToEncodedString(): Result<String> {
            return try {
                val writer = ByteWriter()
                writer.write(identityKey.encryptionKey)
                writer.write(identityKey.signingKey)
                writer.write(ephemeralKey.encryptionKey)
                writer.write(ephemeralKey.signingKey)
                
                writer.writeInt(kemCiphertext.size)
                writer.write(kemCiphertext)
                
                if (pqcPublicKey != null) {
                    writer.write(1.toByte())
                    writer.write(pqcPublicKey.classicalPublicKey)
                    writer.write(pqcPublicKey.classicalSignPublicKey)
                    writer.write(pqcPublicKey.pqcPublicKey)
                } else {
                    writer.write(0.toByte())
                }
                
                if (kemCiphertext2 != null) {
                    writer.write(1.toByte())
                    writer.writeInt(kemCiphertext2.size)
                    writer.write(kemCiphertext2)
                } else {
                    writer.write(0.toByte())
                }
                
                Result.success(writer.toByteArray().encodeToBase58WithChecksum())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PQInitiatorPayload) return false

            if (identityKey != other.identityKey) return false
            if (ephemeralKey != other.ephemeralKey) return false
            if (!kemCiphertext.contentEquals(other.kemCiphertext)) return false
            if (pqcPublicKey != other.pqcPublicKey) return false
            if (kemCiphertext2 != null) {
                if (other.kemCiphertext2 == null) return false
                if (!kemCiphertext2.contentEquals(other.kemCiphertext2)) return false
            } else if (other.kemCiphertext2 != null) return false

            return true
        }

        override fun hashCode(): Int {
            var result = identityKey.hashCode()
            result = 31 * result + ephemeralKey.hashCode()
            result = 31 * result + kemCiphertext.contentHashCode()
            result = 31 * result + (pqcPublicKey?.hashCode() ?: 0)
            result = 31 * result + (kemCiphertext2?.contentHashCode() ?: 0)
            return result
        }

        companion object {
            fun importFromEncodedString(data: String): Result<PQInitiatorPayload> {
                return try {
                    val bytes = data.decodeBase58WithChecksum()
                    val reader = ByteReader(bytes)
                    
                    val idKey = KodiumPublicKey(reader.readBytes(32), reader.readBytes(32))
                    val ephKey = KodiumPublicKey(reader.readBytes(32), reader.readBytes(32))
                    
                    val ctSize = reader.readInt()
                    val kemCt = reader.readBytes(ctSize)
                    
                    val hasPqc = reader.readByte() == 1.toByte()
                    val pqcKey = if (hasPqc) {
                        val classicalKey = reader.readBytes(32)
                        val classicalSignKey = reader.readBytes(32)
                        val pqcBytes = reader.readBytes(MLKEM.PublicKeySize)
                        KodiumPqcPublicKey(classicalKey, classicalSignKey, pqcBytes)
                    } else null
                    
                    val hasKem2 = try {
                        reader.readByte() == 1.toByte()
                    } catch (e: IllegalStateException) {
                        false
                    }
                    val kemCt2 = if (hasKem2) {
                        val size = reader.readInt()
                        reader.readBytes(size)
                    } else null
                    
                    Result.success(PQInitiatorPayload(idKey, ephKey, kemCt, pqcKey, kemCt2))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }

    /**
     * The resulting shared secret and the payload to send to the responder.
     */
    data class PQSharedSecret(
        val masterSecret: ByteArray, // 32-byte resulting key
        val encapsulationPayload: PQInitiatorPayload // To be sent to responder
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PQSharedSecret) return false

            if (!masterSecret.contentEquals(other.masterSecret)) return false
            if (encapsulationPayload != other.encapsulationPayload) return false

            return true
        }

        override fun hashCode(): Int {
            var result = masterSecret.contentHashCode()
            result = 31 * result + encapsulationPayload.hashCode()
            return result
        }
    }

    /**
     * Calculates the shared secret as the Initiator (the party starting the conversation).
     * @param initiatorIdentityKey Initiator's long-term classical identity private key.
     * @param initiatorPqcKey Initiator's hybrid private key (passed to the payload for immediate replies).
     * @param responderBundle Responder's public bundle.
     * @param info Optional application-specific information for key derivation.
     * @return The [PQSharedSecret] containing the master secret and the payload for the responder.
     */
    fun calculateSecretAsInitiator(
        initiatorIdentityKey: KodiumPrivateKey,
        initiatorPqcKey: KodiumPqcPrivateKey,
        responderBundle: PublicBundle,
        info: ByteArray = INFO_PQXDH
    ): PQSharedSecret {
        val initiatorEphemeralKey = KodiumPrivateKey.generate()

        // dh1 = DH(IK_A, SPK_B)
        val dh1 = RatchetUtils.dh(initiatorIdentityKey.secretKey, responderBundle.pqcKey.classicalPublicKey)
        // dh2 = DH(EK_A, IK_B)
        val dh2 = RatchetUtils.dh(initiatorEphemeralKey.secretKey, responderBundle.identityKey.encryptionKey)
        // dh3 = DH(EK_A, SPK_B)
        val dh3 = RatchetUtils.dh(initiatorEphemeralKey.secretKey, responderBundle.pqcKey.classicalPublicKey)

        var dhOut = dh1 + dh2 + dh3
        if (responderBundle.oneTimePreKey != null) {
            // dh4 = DH(EK_A, OPK_B)
            val dh4 = RatchetUtils.dh(initiatorEphemeralKey.secretKey, responderBundle.oneTimePreKey.encryptionKey)
            dhOut += dh4
        }

        // PQC Encapsulation
        val (sharedSecretPqc, kemCiphertext) = MLKEM.encapsulate(responderBundle.pqcKey.pqcPublicKey)

        var sharedSecretPqc2: ByteArray? = null
        var kemCiphertext2: ByteArray? = null
        if (responderBundle.pqcOneTimePreKey != null) {
            val result = MLKEM.encapsulate(responderBundle.pqcOneTimePreKey.pqcPublicKey)
            sharedSecretPqc2 = result.first
            kemCiphertext2 = result.second
        }

        // Mix all secrets
        val masterSecret = HKDF.deriveSecrets(
            salt = ByteArray(32) { 0 },
            ikm = dhOut + sharedSecretPqc + (sharedSecretPqc2 ?: ByteArray(0)),
            info = info,
            length = 32
        )

        val payload = PQInitiatorPayload(
            identityKey = initiatorIdentityKey.getPublicKey(),
            ephemeralKey = initiatorEphemeralKey.getPublicKey(),
            kemCiphertext = kemCiphertext,
            pqcPublicKey = initiatorPqcKey.getPublicKey(),
            kemCiphertext2 = kemCiphertext2
        )

        return PQSharedSecret(masterSecret, payload)
    }

    /**
     * Calculates the shared secret as the Responder (the party receiving the initial message).
     * @param responderIdentityKey Responder's long-term classical identity private key.
     * @param responderPqcKey Responder's hybrid private key.
     * @param initiatorPayload The payload received from the initiator.
     * @param responderOneTimePreKey Responder's optional one-time pre-key private key.
     * @param responderPqcOneTimePreKey Responder's optional hybrid one-time pre-key private key.
     * @param info Optional application-specific information for key derivation.
     * @return The 32-byte master secret.
     */
    fun calculateSecretAsResponder(
        responderIdentityKey: KodiumPrivateKey,
        responderPqcKey: KodiumPqcPrivateKey,
        initiatorPayload: PQInitiatorPayload,
        responderOneTimePreKey: KodiumPrivateKey? = null,
        responderPqcOneTimePreKey: KodiumPqcPrivateKey? = null,
        info: ByteArray = INFO_PQXDH
    ): ByteArray {
        // dh1 = DH(SPK_B, IK_A)
        val dh1 = RatchetUtils.dh(responderPqcKey.classicalSecretKey, initiatorPayload.identityKey.encryptionKey)
        // dh2 = DH(IK_B, EK_A)
        val dh2 = RatchetUtils.dh(responderIdentityKey.secretKey, initiatorPayload.ephemeralKey.encryptionKey)
        // dh3 = DH(SPK_B, EK_A)
        val dh3 = RatchetUtils.dh(responderPqcKey.classicalSecretKey, initiatorPayload.ephemeralKey.encryptionKey)

        var dhOut = dh1 + dh2 + dh3
        if (responderOneTimePreKey != null) {
            // dh4 = DH(OPK_B, EK_A)
            val dh4 = RatchetUtils.dh(responderOneTimePreKey.secretKey, initiatorPayload.ephemeralKey.encryptionKey)
            dhOut += dh4
        }

        // PQC Decapsulation
        val sharedSecretPqc = MLKEM.decapsulate(initiatorPayload.kemCiphertext, responderPqcKey.pqcSecretKey)
            ?: throw IllegalStateException("ML-KEM decapsulation failed")

        var sharedSecretPqc2: ByteArray? = null
        if (responderPqcOneTimePreKey != null && initiatorPayload.kemCiphertext2 != null) {
            sharedSecretPqc2 = MLKEM.decapsulate(initiatorPayload.kemCiphertext2, responderPqcOneTimePreKey.pqcSecretKey)
                ?: throw IllegalStateException("ML-KEM decapsulation failed for OPK")
        }

        // Mix all secrets
        return HKDF.deriveSecrets(
            salt = ByteArray(32) { 0 },
            ikm = dhOut + sharedSecretPqc + (sharedSecretPqc2 ?: ByteArray(0)),
            info = info,
            length = 32
        )
    }
}