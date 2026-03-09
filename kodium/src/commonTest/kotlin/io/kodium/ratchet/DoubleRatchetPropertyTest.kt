package io.kodium.ratchet

import io.kodium.KodiumPrivateKey
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DoubleRatchetPropertyTest {

    // Custom Kotest Arbitrary generator for X3DH Initialized Sessions
    private val sessionPairs = arbitrary {
        val aliceIdentityKey = KodiumPrivateKey.generate()
        val aliceEphemeralKey = KodiumPrivateKey.generate()
        val bobIdentityKey = KodiumPrivateKey.generate()
        val bobSignedPreKey = KodiumPrivateKey.generate()
        val bobBundle = X3DH.PublicBundle(bobIdentityKey.publicKey, bobSignedPreKey.publicKey)

        val secret1 = X3DH.calculateSecretAsInitiator(aliceIdentityKey, aliceEphemeralKey, bobBundle)
        val secret2 = X3DH.calculateSecretAsResponder(
            bobIdentityKey, bobSignedPreKey, null, aliceIdentityKey.publicKey, aliceEphemeralKey.publicKey
        )

        val alice = DoubleRatchetSession.initializeAsInitiator(secret1, bobSignedPreKey.publicKey)
        val bob = DoubleRatchetSession.initializeAsResponder(secret2, bobSignedPreKey)

        Pair(alice, bob)
    }

    @Test
    fun testFuzzingSingleDirectionSequentialMessaging() = runTest(timeout = kotlin.time.Duration.parse("600s")) {
        checkAll(sessionPairs, Arb.list(Arb.string(minSize = 1, maxSize = 200), range = 1..50)) { sessions, messages ->
            val (alice, bob) = sessions
            
            messages.forEach { plaintext ->
                val encrypted = alice.encryptToEncodedString(plaintext.encodeToByteArray()).getOrThrow()
                val decrypted = bob.decryptFromEncodedString(encrypted).getOrThrow().decodeToString()
                decrypted shouldBe plaintext
            }
        }
    }

    @Test
    fun testFuzzingAlternatingMessaging() = runTest(timeout = kotlin.time.Duration.parse("600s")) {
        checkAll(sessionPairs, Arb.list(Arb.string(minSize = 1, maxSize = 200), range = 1..20)) { sessions, messages ->
            val (alice, bob) = sessions
            
            messages.forEachIndexed { index, plaintext ->
                if (index % 2 == 0) {
                    val encrypted = alice.encryptToEncodedString(plaintext.encodeToByteArray()).getOrThrow()
                    val decrypted = bob.decryptFromEncodedString(encrypted).getOrThrow().decodeToString()
                    decrypted shouldBe plaintext
                } else {
                    val encrypted = bob.encryptToEncodedString(plaintext.encodeToByteArray()).getOrThrow()
                    val decrypted = alice.decryptFromEncodedString(encrypted).getOrThrow().decodeToString()
                    decrypted shouldBe plaintext
                }
            }
        }
    }

    @Test
    fun testFuzzingOutOfOrderDelivery() = runTest(timeout = kotlin.time.Duration.parse("600s")) {
        // Alice sends a batch of messages. Bob receives them in a completely random order.
        checkAll(sessionPairs, Arb.list(Arb.string(minSize = 1, maxSize = 100), range = 2..20)) { sessions, messages ->
            val (alice, bob) = sessions
            
            // 1. Alice encrypts all messages
            val ciphertexts = messages.map { alice.encryptToEncodedString(it.encodeToByteArray()).getOrThrow() }
            
            // 2. We shuffle the ciphertexts to simulate out-of-order network delivery
            val shuffledIndices = ciphertexts.indices.shuffled()
            
            // 3. Bob decrypts them in the shuffled order
            for (i in shuffledIndices) {
                val decrypted = bob.decryptFromEncodedString(ciphertexts[i]).getOrThrow().decodeToString()
                decrypted shouldBe messages[i] // Ensure it matches the original message for that index
            }
        }
    }
}
