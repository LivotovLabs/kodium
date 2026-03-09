package io.kodium.core

import io.kodium.core.fips203.KyberCipherText
import io.kodium.core.fips203.KyberDecapsulationKey
import io.kodium.core.fips203.KyberEncapsulationKey
import io.kodium.core.fips203.MLKEM_768

/**
 * FIPS 203 ML-KEM-768 implementation wrapper for Kodium PQC Hybrid Architecture.
 */
object MLKEM {
    const val PublicKeySize = 1184 // ML-KEM-768
    const val SecretKeySize = 2400 // ML-KEM-768
    const val CiphertextSize = 1088 // ML-KEM-768
    const val SharedSecretSize = 32

    private val mlkem = MLKEM_768()

    /**
     * Generates a key pair matching ML-KEM-768 dimensions using a CSPRNG.
     */
    fun keyPair(): Pair<ByteArray, ByteArray> {
        val kp = mlkem.generate()
        return Pair(kp.encapsulationKey.fullBytes, kp.decapsulationKey.fullBytes)
    }

    /**
     * Encapsulates a shared secret using the recipient's public key.
     * Uses a CSPRNG for entropy.
     */
    fun encapsulate(publicKey: ByteArray): Pair<ByteArray, ByteArray> {
        require(publicKey.size == PublicKeySize) { "Invalid public key size" }
        val key = KyberEncapsulationKey.fromBytes(publicKey)
        val result = key.encapsulate()
        return Pair(result.sharedSecretKey, result.cipherText.fullBytes)
    }

    /**
     * Decapsulates the ciphertext using the secret key.
     */
    fun decapsulate(ciphertext: ByteArray, secretKey: ByteArray): ByteArray? {
        if (ciphertext.size != CiphertextSize || secretKey.size != SecretKeySize) return null
        
        return try {
            val key = KyberDecapsulationKey.fromBytes(secretKey)
            val ct = KyberCipherText.fromBytes(ciphertext)
            key.decapsulate(ct)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extracts the public key from the secret key (FIPS 203 specification).
     */
    fun getPublicKeyFromSecretKey(secretKey: ByteArray): ByteArray {
        require(secretKey.size == SecretKeySize) { "Invalid secret key size" }
        return secretKey.copyOfRange(1152, 1152 + PublicKeySize)
    }
}
