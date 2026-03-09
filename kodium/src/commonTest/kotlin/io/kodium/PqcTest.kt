package io.kodium

import io.kodium.Kodium
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertContentEquals

class PqcTest {

    @Test
    fun testPqcKeyGeneration() {
        val keys = Kodium.pqc.generateKeyPair()
        assertTrue(keys.classicalSecretKey.isNotEmpty())
        assertTrue(keys.pqcSecretKey.isNotEmpty())
        
        val publicKey = keys.getPublicKey()
        assertTrue(publicKey.classicalPublicKey.isNotEmpty())
        assertTrue(publicKey.pqcPublicKey.isNotEmpty())
    }

    @Test
    fun testPqcEncryptionDecryption() {
        val aliceKeys = Kodium.pqc.generateKeyPair()
        val bobKeys = Kodium.pqc.generateKeyPair()

        val data = "Hello Post-Quantum World!".encodeToByteArray()
        
        // Alice encrypts for Bob
        val result = Kodium.pqc.encrypt(aliceKeys, bobKeys.getPublicKey(), data)
        if (result.isFailure) {
            println("Encryption failed: ${result.exceptionOrNull()}")
            result.exceptionOrNull()?.printStackTrace()
        }
        assertTrue(result.isSuccess, "Encryption should succeed")
        
        val encrypted = result.getOrThrow()
        
        // Bob decrypts Alice's message
        val decryptedResult = Kodium.pqc.decrypt(bobKeys, aliceKeys.getPublicKey(), encrypted)
        if (decryptedResult.isFailure) {
            println("Decryption failed: ${decryptedResult.exceptionOrNull()}")
            decryptedResult.exceptionOrNull()?.printStackTrace()
        }
        assertTrue(decryptedResult.isSuccess, "Decryption should succeed")
        
        assertContentEquals(data, decryptedResult.getOrThrow(), "Decrypted data should match original")
    }

    @Test
    fun testPqcEncodedStringEncryptionDecryption() {
        val aliceKeys = Kodium.pqc.generateKeyPair()
        val bobKeys = Kodium.pqc.generateKeyPair()

        val data = "Another secret message".encodeToByteArray()
        
        val result = Kodium.pqc.encryptToEncodedString(aliceKeys, bobKeys.getPublicKey(), data)
        assertTrue(result.isSuccess)
        
        val encoded = result.getOrThrow()
        
        val decryptedResult = Kodium.pqc.decryptFromEncodedString(bobKeys, aliceKeys.getPublicKey(), encoded)
        assertTrue(decryptedResult.isSuccess)
        
        assertContentEquals(data, decryptedResult.getOrThrow())
    }

    @Test
    fun testPqcKeyImportExport() {
        val keys = Kodium.pqc.generateKeyPair()
        val password = "secret-password"
        
        val exportedSk = keys.exportToEncryptedString(password).getOrThrow()
        val importedKeys = KodiumPqcPrivateKey.importFromEncryptedString(exportedSk, password).getOrThrow()
        
        assertContentEquals(keys.classicalSecretKey, importedKeys.classicalSecretKey)
        assertContentEquals(keys.pqcSecretKey, importedKeys.pqcSecretKey)
        
        val exportedPk = keys.getPublicKey().exportToEncodedString()
        val importedPk = KodiumPqcPublicKey.importFromEncodedString(exportedPk).getOrThrow()
        
        assertContentEquals(keys.getPublicKey().classicalPublicKey, importedPk.classicalPublicKey)
        assertContentEquals(keys.getPublicKey().pqcPublicKey, importedPk.pqcPublicKey)
    }
}
