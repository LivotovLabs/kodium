package io.kodium

import io.kodium.core.MLKEM
import io.kodium.ratchet.PQDoubleRatchetSession
import io.kodium.ratchet.PQXDH
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class MlKemVariantTest {

    @Test
    fun newKeysAreFips203ByDefault() {
        assertEquals(MlKemVariant.FIPS_203, Kodium.pqc.generateKeyPair().mlKemVariant)
        assertEquals(MlKemVariant.FIPS_203, KodiumPqcPrivateKey.generate().mlKemVariant)
        assertEquals(MlKemVariant.FIPS_203, MLKEM.variantOf(MLKEM.keyPair().first))
    }

    @Test
    fun legacyKeysCanStillBeGeneratedOnRequest() {
        assertEquals(MlKemVariant.LEGACY, Kodium.pqc.generateKeyPair(MlKemVariant.LEGACY).mlKemVariant)
        assertEquals(MlKemVariant.LEGACY, MLKEM.variantOf(MLKEM.keyPair(MlKemVariant.LEGACY).first))
        assertEquals(MlKemVariant.FIPS_203, Kodium.pqc.generateKeyPair(MlKemVariant.FIPS_203).mlKemVariant)
    }

    @Test
    fun variantIsRecognisedForManyKeys() {
        repeat(8) {
            for (variant in MlKemVariant.entries) {
                val (pk, sk) = MLKEM.keyPair(variant)
                assertEquals(variant, MLKEM.variantOf(pk))
                assertEquals(variant, MLKEM.variantOf(MLKEM.getPublicKeyFromSecretKey(sk)))
            }
        }
    }

    @Test
    fun variantSurvivesExportAndImport() {
        for (variant in MlKemVariant.entries) {
            val key = Kodium.pqc.generateKeyPair(variant)
            assertEquals(variant, KodiumPqcPrivateKey.importFromArray(key.exportToArray()).getOrThrow().mlKemVariant)
            assertEquals(variant, KodiumPqcPublicKey.importFromEncodedString(key.getPublicKey().exportToEncodedString()).getOrThrow().mlKemVariant)
        }
    }

    @Test
    fun rawMlKemRoundTripsInEveryVariant() {
        for (variant in MlKemVariant.entries) {
            val (pk, sk) = MLKEM.keyPair(variant)
            val (secret, ct) = MLKEM.encapsulate(pk)
            assertContentEquals(secret, MLKEM.decapsulate(ct, sk), "variant $variant")
        }
    }

    @Test
    fun hybridBoxWorksBetweenAnyVariants() {
        val data = "mixed variants".encodeToByteArray()
        for (senderVariant in MlKemVariant.entries) for (recipientVariant in MlKemVariant.entries) {
            val sender = Kodium.pqc.generateKeyPair(senderVariant)
            val recipient = Kodium.pqc.generateKeyPair(recipientVariant)
            val box = Kodium.pqc.encrypt(sender, recipient.getPublicKey(), data).getOrThrow()
            assertContentEquals(data, Kodium.pqc.decrypt(recipient, sender.getPublicKey(), box).getOrThrow(), "$senderVariant -> $recipientVariant")
        }
    }

    @Test
    fun pqxdhAndRatchetWorkBetweenAnyVariants() {
        for (aliceVariant in MlKemVariant.entries) for (bobVariant in MlKemVariant.entries) {
            val aliceId = Kodium.generateKeyPair()
            val alicePqc = Kodium.pqc.generateKeyPair(aliceVariant)
            val bobId = Kodium.generateKeyPair()
            val bobPqc = Kodium.pqc.generateKeyPair(bobVariant)
            val bobPqOpk = Kodium.pqc.generateKeyPair(bobVariant)
            val bundle = PQXDH.PublicBundle(bobId.getPublicKey(), bobPqc.getPublicKey(), pqcOneTimePreKey = bobPqOpk.getPublicKey())

            val initiated = PQXDH.calculateSecretAsInitiator(aliceId, alicePqc, bundle)
            val bobSecret = PQXDH.calculateSecretAsResponder(bobId, bobPqc, initiated.encapsulationPayload, responderPqcOneTimePreKey = bobPqOpk)
            assertContentEquals(initiated.masterSecret, bobSecret, "$aliceVariant / $bobVariant")

            val alice = PQDoubleRatchetSession.initializeAsInitiator(initiated.masterSecret, bobPqc.getPublicKey(), alicePqc)
            val bob = PQDoubleRatchetSession.initializeAsResponder(bobSecret, bobPqc, alicePqc.getPublicKey())
            assertEquals("a1", bob.decryptFromEncodedString(alice.encryptToEncodedString("a1".encodeToByteArray()).getOrThrow()).getOrThrow().decodeToString())
            assertEquals("b1", alice.decryptFromEncodedString(bob.encryptToEncodedString("b1".encodeToByteArray()).getOrThrow()).getOrThrow().decodeToString())
            assertEquals("a2", bob.decryptFromEncodedString(alice.encryptToEncodedString("a2".encodeToByteArray()).getOrThrow()).getOrThrow().decodeToString())
        }
    }
}
