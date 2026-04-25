package io.kodium.ratchet

import io.kodium.KodiumPrivateKey
import io.kodium.KodiumPublicKey
import io.kodium.core.decodeBase64WithChecksum
import io.kodium.core.encodeToBase64WithChecksum

/**
 * X3DH (Extended Triple Diffie-Hellman) Key Agreement Protocol.
 */
object X3DH {

    private val INFO_X3DH = "KodiumX3DH".encodeToByteArray()

    /**
     * Responder's public bundle for X3DH.
     * @property identityKey Responder's long-term identity public key.
     * @property signedPreKey Responder's signed pre-key public key.
     * @property oneTimePreKey Responder's one-time pre-key public key (optional).
     */
    data class PublicBundle(
        val identityKey: KodiumPublicKey,
        val signedPreKey: KodiumPublicKey,
        val oneTimePreKey: KodiumPublicKey? = null
    ) {
        /**
         * Exports this PublicBundle to a Base64-encoded string with a checksum.
         * This string can be easily transmitted over a network or stored.
         */
        fun exportToEncodedString(): Result<String> {
            return try {
                val writer = ByteWriter()
                writer.write(identityKey.encryptionKey)
                writer.write(identityKey.signingKey)
                writer.write(signedPreKey.encryptionKey)
                writer.write(signedPreKey.signingKey)
                if (oneTimePreKey != null) {
                    writer.write(1.toByte())
                    writer.write(oneTimePreKey.encryptionKey)
                    writer.write(oneTimePreKey.signingKey)
                } else {
                    writer.write(0.toByte())
                }
                Result.success(writer.toByteArray().encodeToBase64WithChecksum())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PublicBundle) return false

            if (identityKey != other.identityKey) return false
            if (signedPreKey != other.signedPreKey) return false
            if (oneTimePreKey != other.oneTimePreKey) return false

            return true
        }

        override fun hashCode(): Int {
            var result = identityKey.hashCode()
            result = 31 * result + signedPreKey.hashCode()
            result = 31 * result + (oneTimePreKey?.hashCode() ?: 0)
            return result
        }

        companion object {
            /**
             * Imports a PublicBundle from a Base64-encoded string with a checksum.
             */
            fun importFromEncodedString(data: String): Result<PublicBundle> {
                return try {
                    val bytes = data.decodeBase64WithChecksum()
                    val reader = ByteReader(bytes)
                    val identityKey = KodiumPublicKey(reader.readBytes(32), reader.readBytes(32))
                    val signedPreKey = KodiumPublicKey(reader.readBytes(32), reader.readBytes(32))
                    
                    val hasOneTime = reader.readByte() == 1.toByte()
                    val oneTimePreKey = if (hasOneTime) KodiumPublicKey(reader.readBytes(32), reader.readBytes(32)) else null
                    
                    Result.success(PublicBundle(identityKey, signedPreKey, oneTimePreKey))
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }

    /**
     * Calculates the shared secret as the Initiator (the party starting the conversation).
     * @param initiatorIdentityKey Initiator's long-term identity private key.
     * @param initiatorEphemeralKey Initiator's generated ephemeral private key.
     * @param responderBundle Responder's public bundle.
     * @param info Optional application-specific information for key derivation.
     * @return The 32-byte shared secret (SK).
     */
    fun calculateSecretAsInitiator(
        initiatorIdentityKey: KodiumPrivateKey,
        initiatorEphemeralKey: KodiumPrivateKey,
        responderBundle: PublicBundle,
        info: ByteArray = INFO_X3DH
    ): ByteArray {
        val dh1 = RatchetUtils.dh(initiatorIdentityKey.secretKey, responderBundle.signedPreKey.encryptionKey)
        val dh2 = RatchetUtils.dh(initiatorEphemeralKey.secretKey, responderBundle.identityKey.encryptionKey)
        val dh3 = RatchetUtils.dh(initiatorEphemeralKey.secretKey, responderBundle.signedPreKey.encryptionKey)

        var dhOut = dh1 + dh2 + dh3
        if (responderBundle.oneTimePreKey != null) {
            val dh4 = RatchetUtils.dh(initiatorEphemeralKey.secretKey, responderBundle.oneTimePreKey.encryptionKey)
            dhOut += dh4
        }

        return HKDF.deriveSecrets(
            salt = ByteArray(32) { 0 }, // Zeros as salt per specification or protocol choice
            ikm = dhOut,
            info = info,
            length = 32
        )
    }

    /**
     * Calculates the shared secret as the Responder (the party receiving the initial message).
     * @param responderIdentityKey Responder's long-term identity private key.
     * @param responderSignedPreKey Responder's signed pre-key private key.
     * @param responderOneTimePreKey Responder's one-time pre-key private key (optional).
     * @param initiatorIdentityKey Initiator's long-term identity public key.
     * @param initiatorEphemeralKey Initiator's ephemeral public key.
     * @param info Optional application-specific information for key derivation.
     * @return The 32-byte shared secret (SK).
     */
    fun calculateSecretAsResponder(
        responderIdentityKey: KodiumPrivateKey,
        responderSignedPreKey: KodiumPrivateKey,
        responderOneTimePreKey: KodiumPrivateKey?,
        initiatorIdentityKey: KodiumPublicKey,
        initiatorEphemeralKey: KodiumPublicKey,
        info: ByteArray = INFO_X3DH
    ): ByteArray {
        val dh1 = RatchetUtils.dh(responderSignedPreKey.secretKey, initiatorIdentityKey.encryptionKey)
        val dh2 = RatchetUtils.dh(responderIdentityKey.secretKey, initiatorEphemeralKey.encryptionKey)
        val dh3 = RatchetUtils.dh(responderSignedPreKey.secretKey, initiatorEphemeralKey.encryptionKey)

        var dhOut = dh1 + dh2 + dh3
        if (responderOneTimePreKey != null) {
            val dh4 = RatchetUtils.dh(responderOneTimePreKey.secretKey, initiatorEphemeralKey.encryptionKey)
            dhOut += dh4
        }

        return HKDF.deriveSecrets(
            salt = ByteArray(32) { 0 }, // Zeros as salt per specification or protocol choice
            ikm = dhOut,
            info = info,
            length = 32
        )
    }
}
