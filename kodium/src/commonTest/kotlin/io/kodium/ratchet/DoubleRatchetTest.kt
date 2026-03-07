package io.kodium.ratchet

import io.kodium.KodiumPrivateKey
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DoubleRatchetTest {

    @Test
    fun testHKDF() {
        val ikm = byteArrayOf(0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b)
        val salt = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c)
        val info = byteArrayOf(0xf0.toByte(), 0xf1.toByte(), 0xf2.toByte(), 0xf3.toByte(), 0xf4.toByte(), 0xf5.toByte(), 0xf6.toByte(), 0xf7.toByte(), 0xf8.toByte(), 0xf9.toByte())
        
        val okm = HKDF.deriveSecrets(salt, ikm, info, 42)
        
        assertEquals(42, okm.size)
        // Values from RFC 5869 Test Case 1
        val expectedHex = "3cb25f25faacd57a90434f64d0362f2a2d2d0a90cf1a5a4c5db02d56ecc4c5bf34007208d5b887185865"
        assertEquals(expectedHex, RatchetUtils.toHex(okm))
    }

    @Test
    fun testX3DH() {
        val aliceIdentityKey = KodiumPrivateKey.generate()
        val aliceEphemeralKey = KodiumPrivateKey.generate()

        val bobIdentityKey = KodiumPrivateKey.generate()
        val bobSignedPreKey = KodiumPrivateKey.generate()
        val bobOneTimePreKey = KodiumPrivateKey.generate()

        val bobBundle = X3DH.PublicBundle(
            identityKey = bobIdentityKey.publicKey,
            signedPreKey = bobSignedPreKey.publicKey,
            oneTimePreKey = bobOneTimePreKey.publicKey
        )

        val aliceSecret = X3DH.calculateSecretAsInitiator(
            initiatorIdentityKey = aliceIdentityKey,
            initiatorEphemeralKey = aliceEphemeralKey,
            responderBundle = bobBundle
        )

        val bobSecret = X3DH.calculateSecretAsResponder(
            responderIdentityKey = bobIdentityKey,
            responderSignedPreKey = bobSignedPreKey,
            responderOneTimePreKey = bobOneTimePreKey,
            initiatorIdentityKey = aliceIdentityKey.publicKey,
            initiatorEphemeralKey = aliceEphemeralKey.publicKey
        )

        assertContentEquals(aliceSecret, bobSecret, "X3DH shared secrets should match")
        assertEquals(32, aliceSecret.size)
    }

    @Test
    fun testPublicBundleSerialization() {
        val identityKey = KodiumPrivateKey.generate().publicKey
        val signedPreKey = KodiumPrivateKey.generate().publicKey
        val oneTimePreKey = KodiumPrivateKey.generate().publicKey
        
        val bundleWithOneTime = X3DH.PublicBundle(identityKey, signedPreKey, oneTimePreKey)
        val serializedWithOneTime = bundleWithOneTime.exportToEncodedString().getOrThrow()
        val deserializedWithOneTime = X3DH.PublicBundle.importFromEncodedString(serializedWithOneTime).getOrThrow()
        assertEquals(bundleWithOneTime, deserializedWithOneTime)

        val bundleWithoutOneTime = X3DH.PublicBundle(identityKey, signedPreKey)
        val serializedWithoutOneTime = bundleWithoutOneTime.exportToEncodedString().getOrThrow()
        val deserializedWithoutOneTime = X3DH.PublicBundle.importFromEncodedString(serializedWithoutOneTime).getOrThrow()
        assertEquals(bundleWithoutOneTime, deserializedWithoutOneTime)
    }

    @Test
    fun testDoubleRatchetSession() {
        // 1. Initial Key Exchange via X3DH
        val aliceIdentityKey = KodiumPrivateKey.generate()
        val aliceEphemeralKey = KodiumPrivateKey.generate()

        val bobIdentityKey = KodiumPrivateKey.generate()
        val bobSignedPreKey = KodiumPrivateKey.generate() // Bob's initial ratchet key is usually his Signed PreKey

        val bobBundle = X3DH.PublicBundle(
            identityKey = bobIdentityKey.publicKey,
            signedPreKey = bobSignedPreKey.publicKey
        )

        val sharedSecret = X3DH.calculateSecretAsInitiator(aliceIdentityKey, aliceEphemeralKey, bobBundle)

        // 2. Initialize Sessions
        // Alice starts the session, Bob's public ratchet key is his signed prekey
        val aliceSession = DoubleRatchetSession.initializeAsInitiator(sharedSecret, bobSignedPreKey.publicKey)
        
        // Bob initializes his session
        val bobSession = DoubleRatchetSession.initializeAsResponder(sharedSecret, bobSignedPreKey)

        // 3. Alice sends a message
        val msg1 = aliceSession.encrypt("Hello Bob!".encodeToByteArray(), "AD1".encodeToByteArray()).getOrThrow()
        val decodedMsg1 = bobSession.decrypt(msg1, "AD1".encodeToByteArray()).getOrThrow()
        assertEquals("Hello Bob!", decodedMsg1.decodeToString())

        // 4. Bob replies (DH ratchet step occurs here)
        val msg2 = bobSession.encrypt("Hello Alice!".encodeToByteArray(), "AD2".encodeToByteArray()).getOrThrow()
        val decodedMsg2 = aliceSession.decrypt(msg2, "AD2".encodeToByteArray()).getOrThrow()
        assertEquals("Hello Alice!", decodedMsg2.decodeToString())

        // 5. Alice sends two messages back-to-back (no DH ratchet, just symmetric ratchet)
        val msg3 = aliceSession.encrypt("Message 3".encodeToByteArray()).getOrThrow()
        val msg4 = aliceSession.encrypt("Message 4".encodeToByteArray()).getOrThrow()

        // Bob decrypts them
        val decodedMsg3 = bobSession.decrypt(msg3).getOrThrow()
        assertEquals("Message 3", decodedMsg3.decodeToString())

        val decodedMsg4 = bobSession.decrypt(msg4).getOrThrow()
        assertEquals("Message 4", decodedMsg4.decodeToString())
    }

    @Test
    fun testDoubleRatchetSkippedMessages() {
        val sharedSecret = ByteArray(32) { 1 }
        val bobRatchetKey = KodiumPrivateKey.generate()

        val aliceSession = DoubleRatchetSession.initializeAsInitiator(sharedSecret, bobRatchetKey.publicKey)
        val bobSession = DoubleRatchetSession.initializeAsResponder(sharedSecret, bobRatchetKey)

        // Alice sends 3 messages
        val msg1 = aliceSession.encrypt("One".encodeToByteArray()).getOrThrow()
        val msg2 = aliceSession.encrypt("Two".encodeToByteArray()).getOrThrow()
        val msg3 = aliceSession.encrypt("Three".encodeToByteArray()).getOrThrow()

        // Bob receives msg3 first
        val dec3 = bobSession.decrypt(msg3).getOrThrow()
        assertEquals("Three", dec3.decodeToString())

        // Bob then receives msg1
        val dec1 = bobSession.decrypt(msg1).getOrThrow()
        assertEquals("One", dec1.decodeToString())

        // Bob then receives msg2
        val dec2 = bobSession.decrypt(msg2).getOrThrow()
        assertEquals("Two", dec2.decodeToString())
    }

    @Test
    fun testSessionPersistence() {
        val sharedSecret = ByteArray(32) { 42 }
        val bobRatchetKey = KodiumPrivateKey.generate()

        val aliceSession = DoubleRatchetSession.initializeAsInitiator(sharedSecret, bobRatchetKey.publicKey)
        val bobSession = DoubleRatchetSession.initializeAsResponder(sharedSecret, bobRatchetKey)

        // Alice sends a message
        val msg1 = aliceSession.encryptToEncodedString("Message 1".encodeToByteArray()).getOrThrow()
        
        // Export Alice's session state
        val password = "secure-storage-password"
        val exportedState = aliceSession.exportToEncryptedString(password).getOrThrow()
        
        // Import Alice's session state into a new object
        val restoredAliceSession = DoubleRatchetSession.importFromEncryptedString(exportedState, password).getOrThrow()

        // Bob receives msg1 and replies
        val dec1 = bobSession.decryptFromEncodedString(msg1).getOrThrow()
        assertEquals("Message 1", dec1.decodeToString())
        
        val msg2 = bobSession.encryptToEncodedString("Message 2".encodeToByteArray()).getOrThrow()
        
        // Restored Alice should be able to decrypt Bob's reply
        val dec2 = restoredAliceSession.decryptFromEncodedString(msg2).getOrThrow()
        assertEquals("Message 2", dec2.decodeToString())

        // Restored Alice sends a third message
        val msg3 = restoredAliceSession.encryptToEncodedString("Message 3".encodeToByteArray()).getOrThrow()
        val dec3 = bobSession.decryptFromEncodedString(msg3).getOrThrow()
        assertEquals("Message 3", dec3.decodeToString())
    }

    @Test
    fun testCustomApplicationInfo() {
        val aliceIdentityKey = KodiumPrivateKey.generate()
        val aliceEphemeralKey = KodiumPrivateKey.generate()
        val bobIdentityKey = KodiumPrivateKey.generate()
        val bobSignedPreKey = KodiumPrivateKey.generate()
        val bobBundle = X3DH.PublicBundle(bobIdentityKey.publicKey, bobSignedPreKey.publicKey)

        val info1 = "App-Context-1".encodeToByteArray()
        val info2 = "App-Context-2".encodeToByteArray()

        val secret1 = X3DH.calculateSecretAsInitiator(aliceIdentityKey, aliceEphemeralKey, bobBundle, info1)
        val secret2 = X3DH.calculateSecretAsInitiator(aliceIdentityKey, aliceEphemeralKey, bobBundle, info2)

        // The secrets must be different if the info string differs
        var match = 0
        for (i in 0 until 32) {
            match = match or (secret1[i].toInt() xor secret2[i].toInt())
        }
        assertTrue(match != 0, "Secrets should be different for different application info")

        // Sessions with different info should not be able to communicate
        val aliceSession = DoubleRatchetSession.initializeAsInitiator(secret1, bobSignedPreKey.publicKey, info1)
        val bobSession = DoubleRatchetSession.initializeAsResponder(secret2, bobSignedPreKey, info2)

        val msg = aliceSession.encryptToEncodedString("Hello".encodeToByteArray()).getOrThrow()
        
        // This should fail immediately because Bob's RK (secret2) is different from Alice's RK (secret1)
        val bobDecrypted = bobSession.decryptFromEncodedString(msg)
        assertTrue(bobDecrypted.isFailure, "Initial decryption should fail as RK is different")
    }
}
