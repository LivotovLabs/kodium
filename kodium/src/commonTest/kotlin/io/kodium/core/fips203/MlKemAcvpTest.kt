package io.kodium.core.fips203

import io.kodium.MlKemVariant
import org.kotlincrypto.hash.sha2.SHA256
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * FIPS 203 conformance against NIST ACVP known answers (issue #11). A round trip cannot catch a
 * construction error that both sides share; only an external reference can.
 */
class MlKemAcvpTest {

    private fun sha256Hex(bytes: ByteArray) = SHA256().digest(bytes).toHexString()

    @Test
    fun keyGenMatchesAcvp() {
        for (v in MlKemAcvpVectors.keyGen) {
            val kp = KyberKeyGenerator.generate(v.parameter, v.z.hexToByteArray(), v.d.hexToByteArray())
            assertEquals(v.ekSha256, sha256Hex(kp.encapsulationKey.fullBytes), "ek, tcId ${v.tcId}")
            assertEquals(v.dkSha256, sha256Hex(kp.decapsulationKey.fullBytes), "dk, tcId ${v.tcId}")
            assertEquals(MlKemVariant.FIPS_203, kp.encapsulationKey.key.variant, "variant, tcId ${v.tcId}")
        }
    }

    @Test
    fun encapsulationMatchesAcvp() {
        for (v in MlKemAcvpVectors.encap) {
            val ek = KyberEncapsulationKey.fromBytes(v.ek.hexToByteArray())
            assertEquals(MlKemVariant.FIPS_203, ek.key.variant, "variant, tcId ${v.tcId}")
            val result = KyberAgreement.encapsulate(ek, v.m.hexToByteArray())
            assertEquals(v.c, result.cipherText.fullBytes.toHexString(), "c, tcId ${v.tcId}")
            assertEquals(v.k, result.sharedSecretKey.toHexString(), "k, tcId ${v.tcId}")
        }
    }

    @Test
    fun decapsulationMatchesAcvp() {
        for (v in MlKemAcvpVectors.decap) {
            val dk = KyberDecapsulationKey.fromBytes(v.dk.hexToByteArray())
            val k = dk.decapsulate(KyberCipherText.fromBytes(v.c.hexToByteArray()))
            assertEquals(v.k, k.toHexString(), "k (${v.reason}), tcId ${v.tcId}")
        }
    }
}
