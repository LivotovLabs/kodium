package io.kodium.ratchet

import io.kodium.Kodium
import io.kodium.KodiumPqcPrivateKey
import io.kodium.KodiumPrivateKey
import io.kodium.core.generateForTesting
import io.kodium.core.nacl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PQDoubleRatchetTest {

    private fun fastGeneratePqcKey(): KodiumPqcPrivateKey {
        // ML-KEM key generation is very slow in JS/Wasm. 
        // We use a deterministic seed to speed up tests on those platforms.
        return KodiumPqcPrivateKey.generateForTesting(nacl.randomBytes(64))
    }

    @Test
    fun testFullConversationAndRatchetSteps() {
        // --- PHASE 1: Setup ---
        // Alice
        val aliceIdentityKey = Kodium.generateKeyPair()
        val alicePqcKey = fastGeneratePqcKey()

        // Bob
        val bobIdentityKey = Kodium.generateKeyPair()
        val bobPqcKey = fastGeneratePqcKey()

        // Bob publishes his bundle
        val bobBundle = PQXDH.PublicBundle(
            identityKey = bobIdentityKey.getPublicKey(),
            pqcKey = bobPqcKey.getPublicKey()
        )

        // --- PHASE 2: Alice Initiates ---
        // Alice calculates shared secret using Bob's bundle
        val aliceSharedSecret = PQXDH.calculateSecretAsInitiator(
            initiatorIdentityKey = aliceIdentityKey,
            initiatorPqcKey = alicePqcKey,
            responderBundle = bobBundle
        )

        // Alice initializes her session
        val aliceSession = PQDoubleRatchetSession.initializeAsInitiator(
            sharedSecret = aliceSharedSecret.masterSecret,
            responderPqcPublicKey = bobBundle.pqcKey,
            ourPqcPrivateKey = alicePqcKey
        )

        // --- PHASE 3: Bob Receives & Initializes ---
        // Bob receives Alice's payload and calculates the shared secret
        val bobSharedSecret = PQXDH.calculateSecretAsResponder(
            responderIdentityKey = bobIdentityKey,
            responderPqcKey = bobPqcKey,
            initiatorPayload = aliceSharedSecret.encapsulationPayload
        )

        // Verify PQXDH produced the exact same master secret
        assertTrue(aliceSharedSecret.masterSecret.contentEquals(bobSharedSecret), "Master secrets must match")

        // Bob initializes his session
        // Note: Bob uses Alice's PQC public key from her payload so he can encapsulate back to her
        val bobSession = PQDoubleRatchetSession.initializeAsResponder(
            sharedSecret = bobSharedSecret,
            ourPqcPrivateKey = bobPqcKey,
            initiatorPqcPublicKey = aliceSharedSecret.encapsulationPayload.pqcPublicKey!!
        )

        // --- CONVERSATION ---

        // Alice sends Message 1
        val msg1 = "Hello Bob, this is Alice!".encodeToByteArray()
        val enc1 = aliceSession.encryptToEncodedString(msg1).getOrThrow()

        // Alice sends Message 2 (No ratchet step, just symmetric chain advancement)
        val msg2 = "Are you receiving me securely over PQC?".encodeToByteArray()
        val enc2 = aliceSession.encryptToEncodedString(msg2).getOrThrow()

        // Bob receives and decrypts
        val dec1 = bobSession.decryptFromEncodedString(enc1).getOrThrow()
        assertEquals("Hello Bob, this is Alice!", dec1.decodeToString())

        val dec2 = bobSession.decryptFromEncodedString(enc2).getOrThrow()
        assertEquals("Are you receiving me securely over PQC?", dec2.decodeToString())

        // Bob replies (RATCHET STEP: Bob's turn to send, he encapsulates a new KEM secret for Alice)
        val msg3 = "Loud and clear Alice! PQC Ratchet is working.".encodeToByteArray()
        val enc3 = bobSession.encryptToEncodedString(msg3).getOrThrow()

        // Alice receives and decrypts (RATCHET STEP: Alice decapsulates the KEM secret and mixes with DH)
        val dec3 = aliceSession.decryptFromEncodedString(enc3).getOrThrow()
        assertEquals("Loud and clear Alice! PQC Ratchet is working.", dec3.decodeToString())

        // Alice replies (RATCHET STEP: Alice encapsulates a new KEM secret for Bob)
        val msg4 = "Excellent. This provides both forward secrecy and post-quantum resistance.".encodeToByteArray()
        val enc4 = aliceSession.encryptToEncodedString(msg4).getOrThrow()

        val dec4 = bobSession.decryptFromEncodedString(enc4).getOrThrow()
        assertEquals("Excellent. This provides both forward secrecy and post-quantum resistance.", dec4.decodeToString())
    }

    @Test
    fun testSkippedMessages() {
        val aliceIdentityKey = Kodium.generateKeyPair()
        val alicePqcKey = fastGeneratePqcKey()
        val bobIdentityKey = Kodium.generateKeyPair()
        val bobPqcKey = fastGeneratePqcKey()

        val bobBundle = PQXDH.PublicBundle(bobIdentityKey.getPublicKey(), bobPqcKey.getPublicKey())
        
        val aliceSharedSecret = PQXDH.calculateSecretAsInitiator(aliceIdentityKey, alicePqcKey, bobBundle)
        
        val aliceSession = PQDoubleRatchetSession.initializeAsInitiator(
            aliceSharedSecret.masterSecret, bobBundle.pqcKey, alicePqcKey
        )
        
        val bobSharedSecret = PQXDH.calculateSecretAsResponder(
            bobIdentityKey, bobPqcKey, aliceSharedSecret.encapsulationPayload
        )
        val bobSession = PQDoubleRatchetSession.initializeAsResponder(
            bobSharedSecret, bobPqcKey, aliceSharedSecret.encapsulationPayload.pqcPublicKey!!
        )

        // Alice sends 3 messages
        val enc1 = aliceSession.encryptToEncodedString("Msg 1".encodeToByteArray()).getOrThrow()
        val enc2 = aliceSession.encryptToEncodedString("Msg 2".encodeToByteArray()).getOrThrow()
        val enc3 = aliceSession.encryptToEncodedString("Msg 3".encodeToByteArray()).getOrThrow()

        // Bob receives them out of order (3, 1, 2)
        val dec3 = bobSession.decryptFromEncodedString(enc3).getOrThrow()
        assertEquals("Msg 3", dec3.decodeToString())

        val dec1 = bobSession.decryptFromEncodedString(enc1).getOrThrow()
        assertEquals("Msg 1", dec1.decodeToString())

        val dec2 = bobSession.decryptFromEncodedString(enc2).getOrThrow()
        assertEquals("Msg 2", dec2.decodeToString())
    }

    @Test
    fun testSessionSerialization() {
        val aliceIdentityKey = Kodium.generateKeyPair()
        val alicePqcKey = fastGeneratePqcKey()
        val bobIdentityKey = Kodium.generateKeyPair()
        val bobPqcKey = fastGeneratePqcKey()

        val bobBundle = PQXDH.PublicBundle(bobIdentityKey.getPublicKey(), bobPqcKey.getPublicKey())
        val aliceSharedSecret = PQXDH.calculateSecretAsInitiator(aliceIdentityKey, alicePqcKey, bobBundle)
        val aliceSession = PQDoubleRatchetSession.initializeAsInitiator(
            aliceSharedSecret.masterSecret, bobBundle.pqcKey, alicePqcKey
        )
        val bobSharedSecret = PQXDH.calculateSecretAsResponder(
            bobIdentityKey, bobPqcKey, aliceSharedSecret.encapsulationPayload
        )
        val bobSession = PQDoubleRatchetSession.initializeAsResponder(
            bobSharedSecret, bobPqcKey, aliceSharedSecret.encapsulationPayload.pqcPublicKey!!
        )

        // Do a couple of exchanges to advance state
        val enc1 = aliceSession.encryptToEncodedString("Hello".encodeToByteArray()).getOrThrow()
        bobSession.decryptFromEncodedString(enc1).getOrThrow()
        val enc2 = bobSession.encryptToEncodedString("World".encodeToByteArray()).getOrThrow()
        aliceSession.decryptFromEncodedString(enc2).getOrThrow()

        // Serialize Alice's state
        val password = "SuperSecretPassword123!"
        val exportedState = aliceSession.exportToEncryptedString(password, 1).getOrThrow()

        // Deserialize
        val restoredAliceSession = PQDoubleRatchetSession.importFromEncryptedString(exportedState, password, 1).getOrThrow()

        // Verify restored session works seamlessly with Bob's existing session
        val enc3 = restoredAliceSession.encryptToEncodedString("Can you hear me now?".encodeToByteArray()).getOrThrow()
        val dec3 = bobSession.decryptFromEncodedString(enc3).getOrThrow()
        assertEquals("Can you hear me now?", dec3.decodeToString())

        val enc4 = bobSession.encryptToEncodedString("Yes, state restored successfully.".encodeToByteArray()).getOrThrow()
        val dec4 = restoredAliceSession.decryptFromEncodedString(enc4).getOrThrow()
        assertEquals("Yes, state restored successfully.", dec4.decodeToString())
    }
}
