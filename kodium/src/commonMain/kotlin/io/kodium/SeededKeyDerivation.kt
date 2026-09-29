package io.kodium

import io.kodium.core.MLKEM
import io.kodium.ratchet.HKDF

/**
 * Deterministic key derivation from a 32-byte seed (issue #12). Versioned by its HKDF salts: a derivation
 * that ever has to change gets a new salt and a new entry point, and the `v1` derivations stay forever,
 * because keys printed on paper must keep reproducing.
 */
internal object SeededKeyDerivation {
    const val SEED_SIZE = 32

    private val HYBRID_SALT_V1 = "kodium-seeded-hybrid-v1".encodeToByteArray()
    private val ED25519_SALT_V1 = "kodium-seeded-ed25519-v1".encodeToByteArray()
    private val INFO_CLASSICAL = "ed25519-seed".encodeToByteArray()
    private val INFO_MLKEM_D = "mlkem-d".encodeToByteArray()
    private val INFO_MLKEM_Z = "mlkem-z".encodeToByteArray()

    fun hybridV1(seed: ByteArray): KodiumPqcPrivateKey {
        require(seed.size == SEED_SIZE) { "Seed must be exactly $SEED_SIZE bytes" }
        val prk = HKDF.extract(HYBRID_SALT_V1, seed)
        val classical = HKDF.expand(prk, INFO_CLASSICAL, 32)
        val d = HKDF.expand(prk, INFO_MLKEM_D, 32)
        val z = HKDF.expand(prk, INFO_MLKEM_Z, 32)
        try {
            val pqcSk = MLKEM.secretKeyFromSeed(d, z)
            return KodiumPqcPrivateKey.fromRaw(classical.copyOf(), pqcSk)
        } finally {
            prk.fill(0)
            classical.fill(0)
            d.fill(0)
            z.fill(0)
        }
    }

    fun ed25519V1(seed: ByteArray): KodiumPrivateKey {
        require(seed.size == SEED_SIZE) { "Seed must be exactly $SEED_SIZE bytes" }
        val prk = HKDF.extract(ED25519_SALT_V1, seed)
        val secret = HKDF.expand(prk, INFO_CLASSICAL, 32)
        try {
            return KodiumPrivateKey.fromRaw(secret.copyOf())
        } finally {
            prk.fill(0)
            secret.fill(0)
        }
    }
}
