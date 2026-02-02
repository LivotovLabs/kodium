package io.kodium.core

import kotlin.experimental.xor
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NaClLowLevelTest {

    @Test
    fun runBoxEndToEndTest() {
        // 1. Generate key pairs for Alice and Bob
        val aliceKeyPair = nacl.Box.keyPair()
        val alice_pk = aliceKeyPair.first
        val alice_sk = aliceKeyPair.second

        val bobKeyPair = nacl.Box.keyPair()
        val bob_pk = bobKeyPair.first
        val bob_sk = bobKeyPair.second

        println("Alice SK: ${alice_sk.encodeToBase58WithChecksum()}")
        println("Bob PK:   ${bob_pk.encodeToBase58WithChecksum()}")

        // 2. Alice prepares a message for Bob
        val message = "This is a secret message.".encodeToByteArray()
        val nonce = nacl.randomBytes(nacl.Box.NonceSize)

        // 3. Alice seals the message using her secret key and Bob's public key
        val ciphertext = nacl.Box.seal(message, nonce, bob_pk, alice_sk)
        println("Ciphertext created (length: ${ciphertext.size})")

        // 4. Bob receives the ciphertext and opens it using his secret key and Alice's public key
        val decryptedMessage = nacl.Box.open(ciphertext, nonce, alice_pk, bob_sk)

        // 5. Check the result
        assertNotNull(decryptedMessage, "FAILURE: Decryption failed! `open` returned null.")
        assertContentEquals(message, decryptedMessage, "Decrypted message does not match original message.")

        // 6. Test failure case: Tamper with the ciphertext
        val tamperedCiphertext = ciphertext.copyOf()
        tamperedCiphertext[20] = tamperedCiphertext[20] xor 1 // Flip one bit
        val failedDecryption = nacl.Box.open(tamperedCiphertext, nonce, alice_pk, bob_sk)

        assertNull(failedDecryption, "Decryption should have failed for tampered ciphertext.")
    }


    @Test
    fun runHardcodedSecretTest() {
        // This is the shared secret from the RFC 7748 test vector
        val shared_k = byteArrayOf(
            0x4a.toByte(), 0x5d.toByte(), 0x9d.toByte(), 0x5b.toByte(), 0xa4.toByte(), 0xce.toByte(), 0x2d.toByte(), 0xe1.toByte(),
            0x72.toByte(), 0x8e.toByte(), 0x3b.toByte(), 0xf4.toByte(), 0x80.toByte(), 0x35.toByte(), 0x0f.toByte(), 0x25.toByte(),
            0xe0.toByte(), 0x7e.toByte(), 0x21.toByte(), 0xc9.toByte(), 0x47.toByte(), 0xd1.toByte(), 0x9e.toByte(), 0x33.toByte(),
            0x76.toByte(), 0xd0.toByte(), 0x9b.toByte(), 0xc1.toByte(), 0x41.toByte(), 0x44.toByte(), 0x4e.toByte(), 0x0c.toByte()
        )

        // HSalsa20 hashes the shared point to get the final key.
        // Let's do that here. The input to hsalsa20 is the shared point,
        // and the key is all zeros.
        val final_key = ByteArray(32)
        val point_k = shared_k // In reality, it's the point, but for this test we use it directly
        val hsalsa_n = ByteArray(16) // Nonce for hsalsa is all zero
        val hsalsa_c = "expand 32-byte k".encodeToByteArray()
        NaClLowLevel.crypto_core_hsalsa20(final_key, hsalsa_n, point_k, hsalsa_c)

        val message = "This is a test".encodeToByteArray()
        val nonce = nacl.randomBytes(nacl.SecretBox.NonceSize)

        // Seal using the derived key
        val ciphertext = nacl.SecretBox.seal(message, nonce, final_key)

        // Open using the same derived key
        val decrypted = nacl.SecretBox.open(ciphertext, nonce, final_key)

        if (decrypted != null && decrypted.contentEquals(message)) {
            println("SUCCESS: Hardcoded SecretBox seal/open works.")
        } else {
            println("FAILURE: Hardcoded SecretBox seal/open FAILED.")
        }

        assertNotNull(decrypted, "Hardcoded SecretBox seal/open failed.")
        assertContentEquals(decrypted, message, "Hardcoded SecretBox seal/open failed.")
    }

}
