package io.kodium

import io.kodium.core.MLKEM
import io.kodium.ratchet.PQDoubleRatchetSession
import io.kodium.ratchet.PQXDH
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

/**
 * Seeded key derivation (issue #12): golden vectors, structural equivalence with random keys,
 * independence of the derived components, and argument validation.
 */
class SeededKeyTest {
    private val message = SeededHybridVectors.signatureMessage.encodeToByteArray()

    @Test
    fun hybridKeysReproduceTheGoldenVectors() {
        for (v in SeededHybridVectors.hybrid) {
            val key = Kodium.pqc.generateKeyPair(v.seed.hexToByteArray())
            assertEquals(v.privateKey, key.exportToArray().toHexString(), "privateKey, seed ${v.seed}")
            assertEquals(v.publicKey, key.getPublicKey().exportToEncodedString(), "publicKey, seed ${v.seed}")
            assertEquals(v.signature, Kodium.pqc.signDetached(key, message).getOrThrow().toHexString(), "signature, seed ${v.seed}")
            assertEquals(MlKemVariant.FIPS_203, key.mlKemVariant)
        }
    }

    @Test
    fun ed25519KeysReproduceTheGoldenVectors() {
        for (v in SeededHybridVectors.ed25519) {
            val key = Kodium.generateKeyPair(v.seed.hexToByteArray())
            assertEquals(v.secretKey, key.exportToArray().toHexString(), "secretKey, seed ${v.seed}")
            assertEquals(v.publicKey, key.getPublicKey().exportToEncodedString(), "publicKey, seed ${v.seed}")
            assertEquals(v.signature, Kodium.signDetached(key, message).getOrThrow().toHexString(), "signature, seed ${v.seed}")
        }
    }

    @Test
    fun derivationIsDeterministicAndLeavesTheSeedIntact() {
        val seed = Kodium.generateHighEntropyKey()
        val copy = seed.copyOf()
        val first = Kodium.pqc.generateKeyPair(seed)
        assertContentEquals(copy, seed)
        assertEquals(first, Kodium.pqc.generateKeyPair(seed))
        assertEquals(first.getPublicKey(), Kodium.pqc.generateKeyPair(seed).getPublicKey())
    }

    @Test
    fun seedsOfAnyOtherLengthAreRejected() {
        for (size in listOf(0, 16, 31, 33, 64)) {
            assertFailsWith<IllegalArgumentException>("size $size") { Kodium.pqc.generateKeyPair(ByteArray(size) { 1 }) }
            assertFailsWith<IllegalArgumentException>("size $size") { Kodium.generateKeyPair(ByteArray(size) { 1 }) }
        }
    }

    @Test
    fun seededKeysHaveTheSameStructureAsRandomKeys() {
        val seeded = Kodium.pqc.generateKeyPair(Kodium.generateHighEntropyKey())
        val random = Kodium.pqc.generateKeyPair()
        assertEquals(random.classicalSecretKey.size, seeded.classicalSecretKey.size)
        assertEquals(random.pqcSecretKey.size, seeded.pqcSecretKey.size)
        assertEquals(random.exportToArray().size, seeded.exportToArray().size)
        assertEquals(random.getPublicKey().classicalPublicKey.size, seeded.getPublicKey().classicalPublicKey.size)
        assertEquals(random.getPublicKey().classicalSignPublicKey.size, seeded.getPublicKey().classicalSignPublicKey.size)
        assertEquals(random.getPublicKey().pqcPublicKey.size, seeded.getPublicKey().pqcPublicKey.size)
        assertEquals(random.mlKemVariant, seeded.mlKemVariant)
    }

    @Test
    fun seededKeysRoundTripThroughEveryExportFormat() {
        val key = Kodium.pqc.generateKeyPair(Kodium.generateHighEntropyKey())
        assertEquals(key, KodiumPqcPrivateKey.importFromArray(key.exportToArray()).getOrThrow())
        val wrapKey = Kodium.generateHighEntropyKey()
        assertEquals(key, KodiumPqcPrivateKey.importFromEncryptedString(key.exportToEncryptedString(wrapKey).getOrThrow(), wrapKey).getOrThrow())
        assertEquals(key.getPublicKey(), KodiumPqcPublicKey.importFromEncodedString(key.getPublicKey().exportToEncodedString()).getOrThrow())
    }

    @Test
    fun seededAndRandomKeysEncryptToEachOther() {
        val seeded = Kodium.pqc.generateKeyPair(Kodium.generateHighEntropyKey())
        val random = Kodium.pqc.generateKeyPair()
        val data = "seeded <-> random".encodeToByteArray()
        val toSeeded = Kodium.pqc.encrypt(random, seeded.getPublicKey(), data).getOrThrow()
        assertContentEquals(data, Kodium.pqc.decrypt(seeded, random.getPublicKey(), toSeeded).getOrThrow())
        val toRandom = Kodium.pqc.encrypt(seeded, random.getPublicKey(), data).getOrThrow()
        assertContentEquals(data, Kodium.pqc.decrypt(random, seeded.getPublicKey(), toRandom).getOrThrow())
        assertEquals(true, Kodium.pqc.verifyDetached(seeded.getPublicKey(), data, Kodium.pqc.signDetached(seeded, data).getOrThrow()))
    }

    @Test
    fun pqxdhAndRatchetWorkWithSeededKeysOnEitherSide() {
        for (seededInitiator in listOf(true, false)) {
            val seeded = Kodium.pqc.generateKeyPair(Kodium.generateHighEntropyKey())
            val random = Kodium.pqc.generateKeyPair()
            val initiatorPqc = if (seededInitiator) seeded else random
            val responderPqc = if (seededInitiator) random else seeded
            val initiatorId = Kodium.generateKeyPair()
            val responderId = Kodium.generateKeyPair()

            val bundle = PQXDH.PublicBundle(responderId.getPublicKey(), responderPqc.getPublicKey())
            val initiated = PQXDH.calculateSecretAsInitiator(initiatorId, initiatorPqc, bundle)
            val responderSecret = PQXDH.calculateSecretAsResponder(responderId, responderPqc, initiated.encapsulationPayload)
            assertContentEquals(initiated.masterSecret, responderSecret, "seededInitiator=$seededInitiator")

            val a = PQDoubleRatchetSession.initializeAsInitiator(initiated.masterSecret, responderPqc.getPublicKey(), initiatorPqc)
            val b = PQDoubleRatchetSession.initializeAsResponder(responderSecret, responderPqc, initiatorPqc.getPublicKey())
            assertEquals("ping", b.decryptFromEncodedString(a.encryptToEncodedString("ping".encodeToByteArray()).getOrThrow()).getOrThrow().decodeToString())
            assertEquals("pong", a.decryptFromEncodedString(b.encryptToEncodedString("pong".encodeToByteArray()).getOrThrow()).getOrThrow().decodeToString())
        }
    }

    @Test
    fun oneBitOfSeedChangesEveryComponent() {
        val seed = SeededHybridVectors.hybrid[2].seed.hexToByteArray()
        val base = Kodium.pqc.generateKeyPair(seed)
        for (bit in listOf(0, 7, 128, 255)) {
            val flipped = seed.copyOf().also { it[bit / 8] = (it[bit / 8].toInt() xor (1 shl (bit % 8))).toByte() }
            val other = Kodium.pqc.generateKeyPair(flipped)
            assertFalse(base.classicalSecretKey.contentEquals(other.classicalSecretKey), "classical secret, bit $bit")
            assertFalse(base.getPublicKey().classicalPublicKey.contentEquals(other.getPublicKey().classicalPublicKey), "X25519, bit $bit")
            assertFalse(base.getPublicKey().classicalSignPublicKey.contentEquals(other.getPublicKey().classicalSignPublicKey), "Ed25519, bit $bit")
            assertFalse(base.getPublicKey().pqcPublicKey.contentEquals(other.getPublicKey().pqcPublicKey), "ML-KEM ek, bit $bit")
            assertFalse(base.pqcSecretKey.contentEquals(other.pqcSecretKey), "ML-KEM dk, bit $bit")
        }
    }

    @Test
    fun derivedComponentsShareNoBytes() {
        for (v in SeededHybridVectors.hybrid) {
            val parts = listOf(v.seed, v.classicalSecret, v.mlkemD, v.mlkemZ)
            assertEquals(parts.size, parts.toSet().size, "distinct components, seed ${v.seed}")
            val key = Kodium.pqc.generateKeyPair(v.seed.hexToByteArray())
            assertEquals(v.classicalSecret, key.classicalSecretKey.toHexString())
            // z is stored verbatim at the end of the ML-KEM secret key; d is not stored at all.
            assertEquals(v.mlkemZ, key.pqcSecretKey.copyOfRange(MLKEM.SecretKeySize - 32, MLKEM.SecretKeySize).toHexString())
            val pqcHex = key.pqcSecretKey.toHexString()
            assertFalse(v.classicalSecret in pqcHex || v.mlkemD in pqcHex || v.seed in pqcHex, "seed ${v.seed}")
        }
    }

    @Test
    fun randomGenerationIsUnaffected() {
        val a = Kodium.pqc.generateKeyPair()
        val b = Kodium.pqc.generateKeyPair()
        assertNotEquals(a, b)
        assertEquals(MlKemVariant.FIPS_203, a.mlKemVariant)
    }
}
