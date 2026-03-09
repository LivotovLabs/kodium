package io.kodium.ratchet

import io.kodium.KodiumPqcPrivateKey
import io.kodium.KodiumPrivateKey
import io.kodium.core.generateForTesting
import io.kodium.core.initializeAsInitiatorForTesting
import io.kodium.core.nacl
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PQDoubleRatchetPropertyTest {

    // Custom Kotest Arbitrary generator for PQXDH Initialized Sessions
    private val sessionPairs = arbitrary {
        val aliceIdentityKey = KodiumPrivateKey.generate()
        val alicePqcKey = KodiumPqcPrivateKey.generateForTesting(nacl.randomBytes(64))
        
        val bobIdentityKey = KodiumPrivateKey.generate()
        val bobPqcKey = KodiumPqcPrivateKey.generateForTesting(nacl.randomBytes(64))
        val bobBundle = PQXDH.PublicBundle(bobIdentityKey.getPublicKey(), bobPqcKey.getPublicKey())

        val aliceSecret = PQXDH.calculateSecretAsInitiator(aliceIdentityKey, alicePqcKey, bobBundle)
        val bobSecret = PQXDH.calculateSecretAsResponder(
            bobIdentityKey, bobPqcKey, aliceSecret.encapsulationPayload
        )

        val alice = PQDoubleRatchetSession.initializeAsInitiatorForTesting(
            aliceSecret.masterSecret, bobPqcKey.getPublicKey(), alicePqcKey, nacl.randomBytes(32)
        )
        val bob = PQDoubleRatchetSession.initializeAsResponder(
            bobSecret, bobPqcKey, alicePqcKey.getPublicKey()
        )

        Pair(alice, bob)
    }

    @Test
    fun testFuzzingSingleDirectionSequentialMessaging() = runTest(timeout = kotlin.time.Duration.parse("600s")) {
        // Reduced max iterations to keep JVM tests incredibly fast
        checkAll(sessionPairs, Arb.list(Arb.string(minSize = 1, maxSize = 200), range = 1..20)) { sessions, messages ->
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
        checkAll(sessionPairs, Arb.list(Arb.string(minSize = 1, maxSize = 200), range = 1..10)) { sessions, messages ->
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
        checkAll(sessionPairs, Arb.list(Arb.string(minSize = 1, maxSize = 100), range = 2..10)) { sessions, messages ->
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
