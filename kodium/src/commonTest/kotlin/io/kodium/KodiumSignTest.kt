package io.kodium

import io.kodium.core.nacl
import io.kodium.core.NaClLowLevel
import io.kodium.core.encodeToBase58WithChecksum
import io.kodium.core.decodeBase58WithChecksum
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse

class KodiumSignTest {

    @Test
    fun testLowLevelSignAndVerify() {
        val message = "Hello, Kodium!".encodeToByteArray()
        
        val pk = ByteArray(32)
        val sk = ByteArray(64)
        NaClLowLevel.crypto_sign_keypair(pk, sk)
        
        val sm = ByteArray(message.size + 64)
        val signResult = NaClLowLevel.crypto_sign(sm, message, message.size.toULong(), sk)
        assertTrue(signResult == 0, "crypto_sign failed")
        
        val m2 = ByteArray(sm.size)
        val openResult = NaClLowLevel.crypto_sign_open(m2, sm, sm.size.toULong(), pk)
        assertTrue(openResult == 0, "crypto_sign_open failed")

        // Tamper test (modify a single byte in the message portion of sm)
        sm[64] = (sm[64].toInt() xor 1).toByte()
        val openResultTampered = NaClLowLevel.crypto_sign_open(m2, sm, sm.size.toULong(), pk)
        assertTrue(openResultTampered != 0, "crypto_sign_open should fail for tampered message")
    }

    @Test
    fun testMidLevelSignAndVerify() {
        val message = "Hello, Kodium!".encodeToByteArray()
        
        val seed = nacl.randomBytes(32)
        val (pk, sk) = nacl.Sign.keyPairFromSeed(seed)
        
        val signature = nacl.Sign.signDetached(message, sk)
        val isValid = nacl.Sign.verifyDetached(signature, message, pk)
        assertTrue(isValid, "Mid-level detached signature verification failed")

        // Tamper test (modify message)
        val tamperedMessage = message.copyOf()
        tamperedMessage[0] = (tamperedMessage[0].toInt() xor 1).toByte()
        val isValidTampered = nacl.Sign.verifyDetached(signature, tamperedMessage, pk)
        assertFalse(isValidTampered, "Verification should fail for tampered message")

        // Tamper test (modify signature)
        val tamperedSignature = signature.copyOf()
        tamperedSignature[0] = (tamperedSignature[0].toInt() xor 1).toByte()
        val isValidTamperedSig = nacl.Sign.verifyDetached(tamperedSignature, message, pk)
        assertFalse(isValidTamperedSig, "Verification should fail for tampered signature")
    }

    @Test
    fun testHighLevelKodiumSignAndVerify() {
        val message = "Hello, Kodium! High Level!".encodeToByteArray()
        
        // Generate standard Kodium key pair
        val keyPair = Kodium.generateKeyPair()
        
        // Sign
        val signatureResult = Kodium.signDetachedToEncodedString(keyPair, message)
        assertTrue(signatureResult.isSuccess, "High-level sign failed")
        val signatureB58 = signatureResult.getOrThrow()
        
        // Verify
        val isValid = Kodium.verifyDetachedFromEncodedString(keyPair.getSignPublicKey(), message, signatureB58)
        assertTrue(isValid, "High-level detached signature verification failed")
        
        // Tamper test (modify message)
        val tamperedMessage = "Hello, Kodium! High Level? ".encodeToByteArray()
        val isTamperedValid = Kodium.verifyDetachedFromEncodedString(keyPair.getSignPublicKey(), tamperedMessage, signatureB58)
        assertFalse(isTamperedValid, "Tampered message should fail verification")

        // Tamper test (modify signature string)
        val signatureBytes = signatureB58.decodeBase58WithChecksum()
        signatureBytes[0] = (signatureBytes[0].toInt() xor 1).toByte()
        val tamperedSignatureB58 = signatureBytes.encodeToBase58WithChecksum()
        
        val isTamperedSigValid = Kodium.verifyDetachedFromEncodedString(keyPair.getSignPublicKey(), message, tamperedSignatureB58)
        assertFalse(isTamperedSigValid, "Tampered signature should fail verification")
    }

    @Test
    fun testHighLevelPqcSignAndVerify() {
        val message = "Hello, Kodium! PQC Level!".encodeToByteArray()
        
        // Generate PQC hybrid key pair
        val keyPair = Kodium.pqc.generateKeyPair()
        
        // Sign
        val signatureResult = Kodium.pqc.signDetachedToEncodedString(keyPair, message)
        assertTrue(signatureResult.isSuccess, "PQC high-level sign failed")
        val signatureB58 = signatureResult.getOrThrow()
        
        // Verify
        val isValid = Kodium.pqc.verifyDetachedFromEncodedString(keyPair.getSignPublicKey(), message, signatureB58)
        assertTrue(isValid, "PQC high-level detached signature verification failed")
        
        // Tamper test (modify message)
        val tamperedMessage = "Hello, Kodium! PQC Level? ".encodeToByteArray()
        val isTamperedValid = Kodium.pqc.verifyDetachedFromEncodedString(keyPair.getSignPublicKey(), tamperedMessage, signatureB58)
        assertFalse(isTamperedValid, "Tampered message should fail verification in PQC")
    }
}
