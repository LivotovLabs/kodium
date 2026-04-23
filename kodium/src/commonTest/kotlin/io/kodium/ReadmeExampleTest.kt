package io.kodium

import io.kodium.core.decodeBase58
import io.kodium.core.decodeBase58WithChecksum
import io.kodium.core.encodeToBase58String
import io.kodium.core.encodeToBase58WithChecksum
import kotlin.test.Test
import kotlin.test.assertTrue

class ReadmeExampleTest {

    @Test
    fun testAsymmetricEncryptionExample() {
        // 1. Generate keys
        val alice = Kodium.generateKeyPair()
        val bob = Kodium.generateKeyPair()

        // 2. Alice encrypts a message for Bob
        val message = "The eagle flies at midnight.".encodeToByteArray()

        val encryptedResult = Kodium.encryptToEncodedString(
            mySecretKey = alice,
            theirPublicKey = bob.getPublicKey(),
            data = message
        )

        assertTrue(encryptedResult.isSuccess, "Encryption failed")

        // 3. Bob decrypts the message
        encryptedResult.onSuccess { cipherText ->
            Kodium.decryptFromEncodedString(
                mySecretKey = bob,
                theirPublicKey = alice.getPublicKey(),
                data = cipherText
            ).onSuccess { decryptedBytes ->
                // println("Decrypted: ${decryptedBytes.decodeToString()}")
                assertTrue(decryptedBytes.contentEquals(message), "Decrypted message doesn't match original")
            }
        }
    }

    @Test
    fun testSymmetricEncryptionExample() {
        val password = "CorrectHorseBatteryStaple"
        val secretData = "Launch codes: 12345".encodeToByteArray()

        // Encrypt
        val encryptedResult = Kodium.encryptSymmetricToEncodedString(password, secretData)

        assertTrue(encryptedResult.isSuccess, "Symmetric encryption failed")

        // Decrypt
        encryptedResult.onSuccess { cipherText ->
            val decryptedResult = Kodium.decryptSymmetricFromEncodedString(password, cipherText)
            // println("Restored: ${decryptedResult.getOrThrow().decodeToString()}")
            assertTrue(decryptedResult.isSuccess, "Symmetric decryption failed")
            assertTrue(decryptedResult.getOrThrow().contentEquals(secretData), "Decrypted symmetric data doesn't match")
        }
    }

    @Test
    fun testKeyExportImportExample() {
        val keyPair = Kodium.generateKeyPair()

        // Export Public Key (Safe to share)
        val pubKeyString = keyPair.getPublicKey().exportToEncodedString()

        // Export Private Key (Encrypted with a password)
        val privKeyString = keyPair.exportToEncryptedString("StrongPassword")
        
        assertTrue(privKeyString.isSuccess, "Private key export failed")

        // Import later
        val restoredKeyPair = KodiumPrivateKey.importFromEncryptedString(
            data = privKeyString.getOrThrow(), 
            password = "StrongPassword"
        )
        
        assertTrue(restoredKeyPair.isSuccess, "Private key import failed")
        
        // Verify keys match
        val restored = restoredKeyPair.getOrThrow()
        assertTrue(restored.secretKey.contentEquals(keyPair.secretKey), "Restored secret key doesn't match")
        assertTrue(restored.getPublicKey().encryptionKey.contentEquals(keyPair.getPublicKey().encryptionKey), "Restored encryption key doesn't match")
        assertTrue(restored.getPublicKey().signingKey.contentEquals(keyPair.getPublicKey().signingKey), "Restored signing key doesn't match")
    }

    @Test
    fun testBase58UtilityExample() {
        val data = "Kodium Pure Kotlin".encodeToByteArray()

        // Standard Base58
        val b58 = data.encodeToBase58String()
        val decoded = b58.decodeBase58()
        assertTrue(decoded.contentEquals(data), "Base58 decode failed")

        // Base58 with Checksum
        val b58Check = data.encodeToBase58WithChecksum()
        val decodedCheck = b58Check.decodeBase58WithChecksum()
        assertTrue(decodedCheck.contentEquals(data), "Base58Check decode failed")
    }

    @Test
    fun testKdfUtilityExample() {
        val password = "UserPassword123".encodeToByteArray()
        val salt = "RandomSalt".encodeToByteArray()

        // Derive a 32-byte key
        val derivedKey = io.kodium.core.KDF.deriveKey(
            password = password,
            salt = salt,
            iterations = 100_000,
            keyLengthBytes = 32
        )

        assertTrue(derivedKey.size == 32, "Derived key should be 32 bytes")
    }
}
