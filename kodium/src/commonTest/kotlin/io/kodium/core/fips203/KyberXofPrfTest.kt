package io.kodium.core.fips203

import io.kodium.MlKemVariant
import org.kotlincrypto.hash.sha3.SHAKE128
import org.kotlincrypto.hash.sha3.SHAKE256
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

/**
 * Regression tests for issue #11: XOF and PRF must deliver the full SHAKE output, not the Digest
 * face's fixed-length digest padded with zeros.
 */
class KyberXofPrfTest {
    private val seed = ByteArray(32) { (it * 7 + 3).toByte() }

    @Test
    fun xofStreamsTheFullShake128Output() {
        val stream = KyberMath.xof(seed, 1, 2, MlKemVariant.FIPS_203)
        val streamed = ByteArray(3 * 700).also { out ->
            val chunk = ByteArray(3)
            for (i in 0 until 700) { stream.nextBytes(chunk); chunk.copyInto(out, 3 * i) }
        }
        val expected = SHAKE128(streamed.size).apply { update(seed); update(1); update(2) }.digest()
        assertContentEquals(expected, streamed)
    }

    @Test
    fun prfReturnsSixtyFourEtaBytesOfShake256() {
        for (eta in 2..3) {
            val expected = SHAKE256(64 * eta).apply { update(seed); update(5) }.digest()
            assertContentEquals(expected, KyberMath.prf(eta, seed, 5, MlKemVariant.FIPS_203), "eta=$eta")
        }
    }

    @Test
    fun legacyVariantKeepsTheKodium100Truncation() {
        val stream = KyberMath.xof(seed, 1, 2, MlKemVariant.LEGACY)
        val first = ByteArray(32).also { stream.nextBytes(it) }
        val rest = ByteArray(1024 - 32).also { stream.nextBytes(it) }
        assertContentEquals(SHAKE128().apply { update(seed); update(1); update(2) }.digest(), first)
        assertTrue(rest.all { it == 0.toByte() })

        val prf = KyberMath.prf(2, seed, 5, MlKemVariant.LEGACY)
        assertContentEquals(SHAKE256().apply { update(seed); update(5) }.digest(), prf.copyOf(64))
        assertTrue(prf.copyOfRange(64, 128).all { it == 0.toByte() })
    }
}
