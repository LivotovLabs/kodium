package io.kodium.core.fips203

import io.kodium.MlKemVariant
import io.kodium.core.MLKEM
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters
import java.security.SecureRandom
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse

/**
 * ML-KEM-768 interoperability with BouncyCastle, the cross-check from issue #11.
 */
class BouncyCastleInteropTest {
    private val params = MLKEMParameters.ml_kem_768
    private val random = SecureRandom()

    private fun randomBytes(size: Int) = ByteArray(size).also { random.nextBytes(it) }

    @Test
    fun keyGenerationFromSeedMatches() {
        repeat(5) {
            val d = randomBytes(32)
            val z = randomBytes(32)
            val bc = MLKEMPrivateKeyParameters(params, d + z)
            val kodium = KyberKeyGenerator.generate(KyberParameter.ML_KEM_768, z.copyOf(), d.copyOf())
            assertContentEquals(bc.publicKey, kodium.encapsulationKey.fullBytes)
        }
    }

    @Test
    fun bouncyCastleEncapsulatesToKodium() {
        repeat(5) {
            val (pk, sk) = MLKEM.keyPair()
            val bc = MLKEMGenerator(random).generateEncapsulated(MLKEMPublicKeyParameters(params, pk))
            assertContentEquals(bc.secret, MLKEM.decapsulate(bc.encapsulation, sk))
        }
    }

    @Test
    fun kodiumEncapsulatesToBouncyCastle() {
        repeat(5) {
            val bcKey = MLKEMPrivateKeyParameters(params, randomBytes(64))
            val (secret, ct) = MLKEM.encapsulate(bcKey.publicKey)
            assertContentEquals(secret, MLKEMExtractor(bcKey).extractSecret(ct))
        }
    }

    @Test
    fun bouncyCastleDecapsulatesWithAKodiumSecretKey() {
        val (pk, sk) = MLKEM.keyPair()
        val (secret, ct) = MLKEM.encapsulate(pk)
        assertContentEquals(secret, MLKEMExtractor(MLKEMPrivateKeyParameters(params, sk)).extractSecret(ct))
    }

    @Test
    fun legacyKeysDoNotInteroperate() {
        // The reason LEGACY exists only for Kodium 1.0.0 peers: a conformant sender cannot reach such a key.
        val (pk, sk) = MLKEM.keyPair(MlKemVariant.LEGACY)
        val bc = MLKEMGenerator(random).generateEncapsulated(MLKEMPublicKeyParameters(params, pk))
        assertFalse(bc.secret.contentEquals(MLKEM.decapsulate(bc.encapsulation, sk)))
    }
}
