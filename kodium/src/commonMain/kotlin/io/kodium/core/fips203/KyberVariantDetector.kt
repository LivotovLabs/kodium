package io.kodium.core.fips203

import io.kodium.MlKemVariant

/**
 * Recognises which [MlKemVariant] produced an ML-KEM encryption key, from the public key alone.
 *
 * A [MlKemVariant.LEGACY] key has t̂ = Â∘ŝ + ê where every entry of Â was sampled from 32 bytes of
 * SHAKE128 output followed by zeros, so it is zero from NTT coefficient 22 on (slot 11 and up), and
 * where the noise e was sampled from 64 bytes of SHAKE256 output, so its coefficients 128..255 are zero.
 * The upper half of t̂ (coefficients 128..255) is therefore exactly the upper half of NTT(e). The
 * forward NTT's first layer only mixes coefficient j with j + 128, so with e's upper half zero, the
 * inverse NTT of t̂'s upper half (lower half set to zero) yields e[0..127] / 2 in its lower half.
 *
 * A legacy key thus always gives coefficients in [-η₁, η₁] after doubling, for every polynomial of
 * t̂. For a FIPS 203 key t̂ is pseudorandom, and 128 coefficients all landing in that range has
 * probability below (5/3329)^128 per polynomial, so the classification is exact in practice.
 *
 * The check reads only public data and need not run in constant time.
 */
internal object KyberVariantDetector {
    private const val Q = KyberConstants.Q
    private const val N = KyberConstants.N
    private const val HALF = N / 2
    private const val N_INV_128 = 3303 // 128^-1 mod q

    // ζ^BitRev7(i) mod q with ζ = 17 (FIPS 203, Section 4.3), in plain (non-Montgomery) form.
    private val zetas = IntArray(128) { i -> powMod(17, bitRev7(i)) }

    fun detect(key: KyberEncryptionKey): MlKemVariant {
        val bound = key.parameter.ETA1
        for (i in 0 until key.parameter.K) {
            val t = KyberMath.fastByteDecode(key.keyBytes, 12, i * KyberConstants.ENCODE_SIZE, KyberConstants.ENCODE_SIZE)
            val f = IntArray(N)
            t.copyInto(f, HALF, HALF, N)
            inverseNtt(f)
            for (j in 0 until HALF) {
                val c = (2 * f[j]) % Q
                if (c in (bound + 1) until (Q - bound)) return MlKemVariant.FIPS_203
            }
        }
        return MlKemVariant.LEGACY
    }

    // FIPS 203 Algorithm 10 (NTT^-1) over plain integers mod q.
    private fun inverseNtt(f: IntArray) {
        var k = 127
        var len = 2
        while (len <= HALF) {
            var start = 0
            while (start < N) {
                val zeta = zetas[k--]
                for (j in start until start + len) {
                    val t = f[j]
                    f[j] = (t + f[j + len]) % Q
                    f[j + len] = ((zeta * (f[j + len] - t + Q)) % Q)
                }
                start += 2 * len
            }
            len *= 2
        }
        for (j in f.indices) f[j] = (f[j] * N_INV_128) % Q
    }

    private fun bitRev7(i: Int): Int {
        var r = 0
        for (b in 0 until 7) r = r or (((i shr b) and 1) shl (6 - b))
        return r
    }

    private fun powMod(base: Int, exp: Int): Int {
        var result = 1
        repeat(exp) { result = (result * base) % Q }
        return result
    }
}
