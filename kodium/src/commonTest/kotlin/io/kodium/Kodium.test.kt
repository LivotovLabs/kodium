package io.kodium

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KryptokitTest {

    @Test
    fun testKeyGenWithExportImportWithCustomPassword() {
        val password = "qwerty"
        val badPassword = "Qwerty"
        val keypair = Kodium.generateKeyPair()
        val exportedKeyPairString = keypair.exportToEncryptedString(password, 1).getOrNull() ?: ""

        val restoreResultWithCorrectPassword =
            KodiumPrivateKey.importFromEncryptedString(exportedKeyPairString, password, 1).getOrNull()
        val restoreResultWithBadPassword =
            KodiumPrivateKey.importFromEncryptedString(exportedKeyPairString, badPassword, 1)
        val restoreResultWithEmptyPassword = KodiumPrivateKey.importFromEncryptedString(exportedKeyPairString, "", 1)

        assertContentEquals(
            expected = keypair.secretKey,
            actual = restoreResultWithCorrectPassword?.secretKey,
            message = "Private key is not the same after import"
        )

        assertEquals(
            expected = keypair.getPublicKey(),
            actual = restoreResultWithCorrectPassword?.getPublicKey(),
            message = "Public key is not the same after import"
        )

        assertTrue(
            restoreResultWithBadPassword.isFailure,
            "Encrypted keypair should fail to restore with wrong password"
        )

        assertTrue(
            restoreResultWithEmptyPassword.isFailure,
            "Encrypted keypair should fail to restore with empty password"
        )
    }

    @Test
    fun testEncryptDecrypt() {
        val keypair1 = Kodium.generateKeyPair()
        val keypair2 = Kodium.generateKeyPair()
        val keypair3 = Kodium.generateKeyPair()

        val data = "Hello, World!".encodeToByteArray()
        val encrypted = Kodium.encryptToEncodedString(keypair1, keypair2.getPublicKey(), data).getOrNull()

        val decrypted =
            Kodium.decryptFromEncodedString(keypair2, keypair1.getPublicKey(), encrypted!!).getOrThrow()

        val wrongKeyDecrypted =
            Kodium.decryptFromEncodedString(keypair3, keypair3.getPublicKey(), encrypted).getOrNull()

        assertContentEquals(
            expected = data,
            actual = decrypted,
            message = "Encrypted and decrypted data are not the same. Expected \"${data.decodeToString()}\" but got \"${decrypted?.decodeToString()}\" instead. Original encrypted data was ${encrypted}"
        )

        assertNull(
            actual = wrongKeyDecrypted,
            message = "Decrypted data should be null with wrong key, expected to get null but got \"${wrongKeyDecrypted?.decodeToString()}\" instead.")
    }

    @Test
    fun testSymmetricEncryptDecrypt() {
        val password = "<PASSWORD>"
        val wrongPassword = "<password>"
        val data = "Hello, World Symmetric!".encodeToByteArray()
        val encrypted = Kodium.encryptSymmetricToEncodedString(password, data).getOrNull()
        val decrypted = Kodium.decryptSymmetricFromEncodedString(password, encrypted!!).getOrNull()

        assertContentEquals(
            expected = data,
            actual = decrypted,
            message = "Encrypted and decrypted data are not the same"
        )

        val decryptedWrongPassword =
            Kodium.decryptSymmetricFromEncodedString(wrongPassword, encrypted).getOrNull()

        assertNull(
            actual = decryptedWrongPassword,
            message = "Decrypted data should be null with wrong password but was ${decryptedWrongPassword?.decodeToString()}"
        )
    }

    @Test
    fun testSymmetricEncryptDecryptWithCustomKeyDerivationsNumber() {
        val password = "<PASSWORD>"
        val wrongPassword = "<password>"
        val derivations = 1_000_000
        val data = "Hello, World Symmetric!".encodeToByteArray()
        val encrypted = Kodium.encryptSymmetricToEncodedString(password, data, derivations).getOrNull()
        val encryptedWithTooSmallKeyDerivations = Kodium.encryptSymmetricToEncodedString(password, data, 0).getOrNull()
        val decrypted = Kodium.decryptSymmetricFromEncodedString(password, encrypted!!, derivations).getOrNull()
        val decryptedWithDifferentKeyDerivations = Kodium.decryptSymmetricFromEncodedString(password, encrypted, derivations+100).getOrNull()

        assertContentEquals(
            expected = data,
            actual = decrypted,
            message = "Encrypted and decrypted data are not the same"
        )

        val decryptedWrongPassword =
            Kodium.decryptSymmetricFromEncodedString(wrongPassword, encrypted).getOrNull()

        assertNull(
            actual = decryptedWrongPassword,
            message = "Decrypted data should be null with wrong password but was ${decryptedWrongPassword?.decodeToString()}"
        )

        assertNull(
            actual = decryptedWithDifferentKeyDerivations,
            message = "Decrypted data should be null with wrong key derivations number but was ${decryptedWithDifferentKeyDerivations?.decodeToString()} instead of ${decrypted?.decodeToString()}"
        )

        assertNull(
            actual = encryptedWithTooSmallKeyDerivations,
            message = "Encryption should not happen with too small key derivations number but it did. Encrypted data was $encryptedWithTooSmallKeyDerivations instead of $encrypted"
        )
    }

    @Test
    fun testStringEncoding() {
        val data = "Hello, World!"
        val dataArray = data.encodeToByteArray()
        val encoded = Kodium.encodeArrayToString(dataArray).getOrNull()
        val decoded = Kodium.decodeArrayFromString(encoded!!).getOrNull()

        assertContentEquals(
            expected = dataArray,
            actual = decoded,
            message = "Encoded and decoded data are not the same"
        )
    }
}