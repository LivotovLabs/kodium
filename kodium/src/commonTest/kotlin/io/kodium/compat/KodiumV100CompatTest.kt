package io.kodium.compat

import io.kodium.Kodium
import io.kodium.KodiumPqcPrivateKey
import io.kodium.KodiumPqcPublicKey
import io.kodium.KodiumPrivateKey
import io.kodium.MlKemVariant
import io.kodium.compat.KodiumV100Fixtures as F
import io.kodium.core.fips203.KyberAgreement
import io.kodium.core.fips203.KyberEncapsulationKey
import io.kodium.core.fips203.KyberKeyGenerator
import io.kodium.ratchet.PQDoubleRatchetSession
import io.kodium.ratchet.PQXDH
import org.kotlincrypto.hash.sha2.SHA256
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Keys, ciphertexts and sessions created by Kodium 1.0.0 must keep working unchanged, and what the
 * current code sends to 1.0.0 keys must be exactly what 1.0.0 would have sent (issue #11).
 */
class KodiumV100CompatTest {

    private fun sha256Hex(bytes: ByteArray) = SHA256().digest(bytes).toHexString()
    private fun pqc(hex: String) = KodiumPqcPrivateKey.importFromArray(hex.hexToByteArray()).getOrThrow()
    private fun classical(hex: String) = KodiumPrivateKey.importFromArray(hex.hexToByteArray()).getOrThrow()

    @Test
    fun legacyMlKemIsByteIdenticalToKodium100() {
        for ((i, v) in F.mlKem.withIndex()) {
            val kp = KyberKeyGenerator.generate(v.parameter, v.z.hexToByteArray(), v.d.hexToByteArray(), MlKemVariant.LEGACY)
            assertEquals(v.ekSha256, sha256Hex(kp.encapsulationKey.fullBytes), "ek #$i ${v.parameter}")
            assertEquals(v.dkSha256, sha256Hex(kp.decapsulationKey.fullBytes), "dk #$i ${v.parameter}")

            // The variant is recognised from the bytes, not remembered from generation.
            val ek = KyberEncapsulationKey.fromBytes(kp.encapsulationKey.fullBytes)
            assertEquals(MlKemVariant.LEGACY, ek.key.variant, "variant #$i ${v.parameter}")

            val result = KyberAgreement.encapsulate(ek, v.m.hexToByteArray())
            assertEquals(v.cSha256, sha256Hex(result.cipherText.fullBytes), "c #$i ${v.parameter}")
            assertEquals(v.k, result.sharedSecretKey.toHexString(), "k #$i ${v.parameter}")
            assertEquals(v.k, kp.decapsulationKey.decapsulate(result.cipherText).toHexString(), "decaps #$i ${v.parameter}")

            val fips = KyberKeyGenerator.generate(v.parameter, v.z.hexToByteArray(), v.d.hexToByteArray())
            assertNotEquals(v.ekSha256, sha256Hex(fips.encapsulationKey.fullBytes), "FIPS ek #$i ${v.parameter}")
            assertEquals(MlKemVariant.FIPS_203, fips.encapsulationKey.key.variant, "FIPS variant #$i ${v.parameter}")
        }
    }

    @Test
    fun kodium100KeysImportAndReportLegacy() {
        val alice = pqc(F.alicePqc)
        val bob = pqc(F.bobPqc)
        assertEquals(MlKemVariant.LEGACY, alice.mlKemVariant)
        assertEquals(MlKemVariant.LEGACY, bob.mlKemVariant)
        assertEquals(MlKemVariant.LEGACY, pqc(F.bobPqOpk).mlKemVariant)

        val bobPublic = KodiumPqcPublicKey.importFromEncodedString(F.bobPqcPublic).getOrThrow()
        assertEquals(bob.getPublicKey(), bobPublic)
        assertEquals(MlKemVariant.LEGACY, bobPublic.mlKemVariant)
        assertEquals(F.alicePqcPublic, alice.getPublicKey().exportToEncodedString())
        assertEquals(F.bobPqc, bob.exportToArray().toHexString())

        val unwrapped = KodiumPqcPrivateKey.importFromEncryptedString(F.bobPqcWrapped, F.wrapKey.hexToByteArray()).getOrThrow()
        assertEquals(bob, unwrapped)
    }

    @Test
    fun kodium100BoxAndSignatureStillVerify() {
        val alice = pqc(F.alicePqc)
        val bob = pqc(F.bobPqc)

        val opened = Kodium.pqc.decrypt(bob, alice.getPublicKey(), F.box.hexToByteArray()).getOrThrow()
        assertEquals(F.boxPlaintext, opened.decodeToString())
        assertEquals(F.boxMessage, opened.toHexString())

        assertTrue(Kodium.pqc.verifyDetached(alice.getPublicKey(), opened, F.aliceSignature.hexToByteArray()))
    }

    @Test
    fun boxesBetweenKodium100KeysStillWorkBothWays() {
        val alice = pqc(F.alicePqc)
        val bob = pqc(F.bobPqc)
        val data = "sent by 1.1.0 to 1.0.0 keys".encodeToByteArray()

        val toBob = Kodium.pqc.encrypt(alice, bob.getPublicKey(), data).getOrThrow()
        assertContentEquals(data, Kodium.pqc.decrypt(bob, alice.getPublicKey(), toBob).getOrThrow())
        val toAlice = Kodium.pqc.encrypt(bob, alice.getPublicKey(), data).getOrThrow()
        assertContentEquals(data, Kodium.pqc.decrypt(alice, bob.getPublicKey(), toAlice).getOrThrow())
    }

    @Test
    fun kodium100PqxdhHandshakeCompletesAsResponder() {
        val payload = PQXDH.PQInitiatorPayload.importFromEncodedString(F.initiatorPayload).getOrThrow()
        val bundle = PQXDH.PublicBundle.importFromEncodedString(F.bundle).getOrThrow()
        val bob = pqc(F.bobPqc)
        assertEquals(bob.getPublicKey(), bundle.pqcKey)

        val secret = PQXDH.calculateSecretAsResponder(
            responderIdentityKey = classical(F.bobId),
            responderPqcKey = bob,
            initiatorPayload = payload,
            responderOneTimePreKey = classical(F.bobOpk),
            responderPqcOneTimePreKey = pqc(F.bobPqOpk)
        )
        assertEquals(F.masterSecret, secret.toHexString())

        val bobSession = PQDoubleRatchetSession.initializeAsResponder(secret, bob, payload.pqcPublicKey!!)
        assertEquals(F.msg1Plaintext, bobSession.decryptFromEncodedString(F.msg1).getOrThrow().decodeToString())
    }

    @Test
    fun kodium100PqxdhHandshakeCompletesAsInitiator() {
        // A current initiator encapsulates to the 1.0.0 bundle; the 1.0.0 responder keys decapsulate.
        val bundle = PQXDH.PublicBundle.importFromEncodedString(F.bundle).getOrThrow()
        val carolId = Kodium.generateKeyPair()
        val carolPqc = Kodium.pqc.generateKeyPair()
        val initiated = PQXDH.calculateSecretAsInitiator(carolId, carolPqc, bundle)

        val secret = PQXDH.calculateSecretAsResponder(
            classical(F.bobId), pqc(F.bobPqc), initiated.encapsulationPayload, classical(F.bobOpk), pqc(F.bobPqOpk)
        )
        assertContentEquals(initiated.masterSecret, secret)
    }

    @Test
    fun persistedKodium100SessionsContinue() {
        // Alice's 1.0.0 session, saved right after sending msg1, receives Bob's 1.0.0 reply (a ratchet step
        // that decapsulates with Alice's 1.0.0 key), then answers (encapsulating to Bob's 1.0.0 key).
        val alice = PQDoubleRatchetSession.importFromArray(F.aliceStateAfterMsg1.hexToByteArray()).getOrThrow()
        assertEquals(F.msg2Plaintext, alice.decryptFromEncodedString(F.msg2).getOrThrow().decodeToString())
        val msg3 = alice.encryptToEncodedString("msg3 alice->bob (1.1.0)".encodeToByteArray()).getOrThrow()

        // Bob's 1.0.0 session, saved right after sending msg2, receives it and keeps going.
        val bob = PQDoubleRatchetSession.importFromArray(F.bobStateAfterMsg2.hexToByteArray()).getOrThrow()
        assertEquals("msg3 alice->bob (1.1.0)", bob.decryptFromEncodedString(msg3).getOrThrow().decodeToString())
        val msg4 = bob.encryptToEncodedString("msg4 bob->alice (1.1.0)".encodeToByteArray()).getOrThrow()
        assertEquals("msg4 bob->alice (1.1.0)", alice.decryptFromEncodedString(msg4).getOrThrow().decodeToString())
    }
}
