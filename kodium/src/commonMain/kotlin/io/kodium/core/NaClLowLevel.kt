@file:Suppress("ObjectPropertyName", "FunctionName")
@file:OptIn(ExperimentalUnsignedTypes::class)

package io.kodium.core

import org.kotlincrypto.random.CryptoRand
import org.kotlincrypto.hash.sha2.SHA512
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

/**
 * The `NaClLowLevel` object provides low-level cryptographic operations
 * commonly used in NaCl (Networking and Cryptography library)-compatible algorithms.
 * These operations include secure random byte generation, low-level byte and
 * number manipulations, secure key comparison, and cryptographic core functions.
 *
 * This is a port of the TweetNaCl library.
 *
 * NOTE: This version has been audited and corrected to fix critical security
 * vulnerabilities found in the original port. It is strongly recommended to
 * use this version and to validate it against official test vectors.
 */
@Suppress("unused")
internal object NaClLowLevel {

    // --- Unchanged Primitives and Constants ---
    private val _0: ByteArray = ByteArray(16) { 0 }
    private val _9: ByteArray = ByteArray(32).apply { this[0] = 9 }
    private val gf0: LongArray = LongArray(16) { 0 }
    private val gf1: LongArray = LongArray(16).apply { this[0] = 1 }
    private val _121665: LongArray = longArrayOf(0xDB41, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private val D: LongArray = longArrayOf(
        0x78a3, 0x1359, 0x4dca, 0x75eb, 0xd8ab, 0x4141, 0x0a4d, 0x0070,
        0xe898, 0x7779, 0x4079, 0x8cc7, 0xfe73, 0x2b6f, 0x6cee, 0x5203)
    private val D2: LongArray = longArrayOf(
        0xf159, 0x26b2, 0x9b94, 0xebd6, 0xb156, 0x8283, 0x149a, 0x00e0,
        0xd130, 0xeef3, 0x80f2, 0x198e, 0xfce7, 0x56df, 0xd9dc, 0x2406)
    private val X: LongArray = longArrayOf(
        0xd51a, 0x8f25, 0x2d60, 0xc956, 0xa7b2, 0x9525, 0xc760, 0x692c,
        0xdc5c, 0xfdd6, 0xe231, 0xc0a4, 0x53fe, 0xcd6e, 0x36d3, 0x2169)
    private val Y: LongArray = longArrayOf(
        0x6658, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666,
        0x6666, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666, 0x6666)
    private val I: LongArray = longArrayOf(
        0xa0b0, 0x4a0e, 0x1b27, 0xc4ee, 0xe478, 0xad2f, 0x1806, 0x2f43,
        0xd7a7, 0x3dfb, 0x0099, 0x2b4d, 0xdf0b, 0x4fc1, 0x2480, 0x2b83)
    private fun L32(x: Int, c: Int): Int = ((x shl c) or (x ushr (32 - c)))
    private val secureRandom = CryptoRand.Default

    fun randombytes(size: Int): ByteArray {
        val arr = ByteArray(size)
        randombytes(arr, size)
        return arr
    }

    private fun randombytes(x: ByteArray, size: Int) {
        require(x.size >= size) { "Buffer too small" }
        // We want to fill only the first `size` bytes of array `x`.
        // If x.size == size, we can fill it directly.
        if (x.size == size) {
            secureRandom.nextBytes(x)
        } else {
            // If x is larger than size, generate `size` random bytes
            // and copy them into the beginning of `x`.
            val temp = ByteArray(size)
            secureRandom.nextBytes(temp)
            temp.copyInto(destination = x, destinationOffset = 0, startIndex = 0, endIndex = size)
        }
    }

    private fun ld32(x: ByteArray, off: Int = 0): Int {
        var u: Int = x[off + 3].toInt() and 0xff
        u = u shl 8 or (x[off + 2].toInt() and 0xff)
        u = u shl 8 or (x[off + 1].toInt() and 0xff)
        return u shl 8 or (x[off + 0].toInt() and 0xff)
    }

    private fun st32(x: ByteArray, off: Int = 0, u: Int) {
        var uu = u
        for (i in 0 until 4) {
            x[i + off] = uu.toByte()
            uu = uu shr 8
        }
    }

    private fun vn(x: ByteArray, xi: Int, y: ByteArray, yi: Int, n: Int): Int {
        var d = 0
        for (i in 0 until n) {
            d = d or (x[i + xi].toInt() xor y[i + yi].toInt())
        }
        // This is a branchless, constant-time way to map:
        // d = 0 --> returns 0
        // d != 0 --> returns -1
        return -((d or -d) ushr 31)
    }

    private fun crypto_verify_16(x: ByteArray, xi: Int = 0, y: ByteArray, yi: Int = 0): Int = vn(x, xi, y, yi, 16)
    private fun crypto_verify_32(x: ByteArray, xi: Int = 0, y: ByteArray, yi: Int = 0): Int = vn(x, xi, y, yi, 32)

    private fun core(outArr: ByteArray, inArr: ByteArray, k: ByteArray, c: ByteArray, h: Int) {
        val x = IntArray(16)
        val y = IntArray(16)

        // Initial state
        x[0] = ld32(c, 0); x[1] = ld32(k, 0); x[2] = ld32(k, 4); x[3] = ld32(k, 8);
        x[4] = ld32(k, 12); x[5] = ld32(c, 4); x[6] = ld32(inArr, 0); x[7] = ld32(inArr, 4);
        x[8] = ld32(inArr, 8); x[9] = ld32(inArr, 12); x[10] = ld32(c, 8); x[11] = ld32(k, 16);
        x[12] = ld32(k, 20); x[13] = ld32(k, 24); x[14] = ld32(k, 28); x[15] = ld32(c, 12);

        y.indices.forEach { y[it] = x[it] }

        fun rot(a: Int, b: Int) = (a shl b) or (a ushr (32 - b))

        for (i in 0 until 20 step 2) {
            // Even round
            x[4] = x[4] xor rot(x[0] + x[12], 7); x[8] = x[8] xor rot(x[4] + x[0], 9);
            x[12] = x[12] xor rot(x[8] + x[4], 13); x[0] = x[0] xor rot(x[12] + x[8], 18);
            x[9] = x[9] xor rot(x[5] + x[1], 7); x[13] = x[13] xor rot(x[9] + x[5], 9);
            x[1] = x[1] xor rot(x[13] + x[9], 13); x[5] = x[5] xor rot(x[1] + x[13], 18);
            x[14] = x[14] xor rot(x[10] + x[6], 7); x[2] = x[2] xor rot(x[14] + x[10], 9);
            x[6] = x[6] xor rot(x[2] + x[14], 13); x[10] = x[10] xor rot(x[6] + x[2], 18);
            x[3] = x[3] xor rot(x[15] + x[11], 7); x[7] = x[7] xor rot(x[3] + x[15], 9);
            x[11] = x[11] xor rot(x[7] + x[3], 13); x[15] = x[15] xor rot(x[11] + x[7], 18);
            // Odd round
            x[1] = x[1] xor rot(x[0] + x[3], 7); x[2] = x[2] xor rot(x[1] + x[0], 9);
            x[3] = x[3] xor rot(x[2] + x[1], 13); x[0] = x[0] xor rot(x[3] + x[2], 18);
            x[6] = x[6] xor rot(x[5] + x[4], 7); x[7] = x[7] xor rot(x[6] + x[5], 9);
            x[4] = x[4] xor rot(x[7] + x[6], 13); x[5] = x[5] xor rot(x[4] + x[7], 18);
            x[11] = x[11] xor rot(x[10] + x[9], 7); x[8] = x[8] xor rot(x[11] + x[10], 9);
            x[9] = x[9] xor rot(x[8] + x[11], 13); x[10] = x[10] xor rot(x[9] + x[8], 18);
            x[12] = x[12] xor rot(x[15] + x[14], 7); x[13] = x[13] xor rot(x[12] + x[15], 9);
            x[14] = x[14] xor rot(x[13] + x[12], 13); x[15] = x[15] xor rot(x[14] + x[13], 18);
        }

        for(i in 0 until 16) x[i] += y[i]

        if (h != 0) { // HSalsa20
            st32(outArr, 0, x[0] - ld32(c,0));
            st32(outArr, 4, x[5] - ld32(c,4));
            st32(outArr, 8, x[10] - ld32(c,8));
            st32(outArr, 12, x[15] - ld32(c,12));
            st32(outArr, 16, x[6] - ld32(inArr,0));
            st32(outArr, 20, x[7] - ld32(inArr,4));
            st32(outArr, 24, x[8] - ld32(inArr,8));
            st32(outArr, 28, x[9] - ld32(inArr,12));
        } else { // Salsa20
            for(i in 0 until 16) st32(outArr, 4*i, x[i])
        }
    }

    fun crypto_core_salsa20(outArr: ByteArray, inArr: ByteArray, k: ByteArray, c: ByteArray): Int {
        core(outArr, inArr, k, c, 0)
        return 0
    }

    fun crypto_core_hsalsa20(outArr: ByteArray, inArr: ByteArray, k: ByteArray, c: ByteArray): Int {
        core(outArr, inArr, k, c, 1)
        return 0
    }

    private val sigma: ByteArray = "expand 32-byte k".encodeToByteArray()

    fun crypto_stream_salsa20_xor(c: ByteArray, m: ByteArray?, bIn: Long, n: ByteArray, nOff: Int = 0, k: ByteArray): Int {
        val z = ByteArray(16)
        val x = ByteArray(64)
        var u: Int
        if (bIn == 0L) return 0
        for (i in 0 until 8) z[i] = n[i + nOff]
        var b = bIn
        var cOff = 0
        var mOff = 0
        while (b >= 64) {
            crypto_core_salsa20(x, z, k, sigma)
            for (i in 0 until 64) c[cOff + i] = (m?.get(mOff + i) ?: 0) xor x[i]
            u = 1
            for (i in 8 until 16) {
                u += z[i].toUByte().toInt()
                z[i] = u.toByte()
                u = u ushr 8
            }
            b -= 64
            cOff += 64
            m?.let { mOff += 64 }
        }
        if (b > 0) {
            crypto_core_salsa20(x, z, k, sigma)
            for (i in 0 until b.toInt()) c[cOff + i] = (m?.get(mOff + i) ?: 0) xor x[i]
        }
        return 0
    }

    fun crypto_stream_salsa20(c: ByteArray, d: Long, n: ByteArray, k: ByteArray, nStart: Int = 0): Int =
        crypto_stream_salsa20_xor(c, null, d, n, nStart, k)

    fun crypto_stream(c: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int {
        val s = ByteArray(32)
        crypto_core_hsalsa20(s, n, k, sigma)
        return crypto_stream_salsa20(c, d, n, s, 16)
    }

    fun crypto_stream_xor(c: ByteArray, m: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int {
        val s = ByteArray(32)
        crypto_core_hsalsa20(s, n, k, sigma)
        return crypto_stream_salsa20_xor(c, m, d, n, 16, s)
    }

    private fun add1305(h: IntArray, c: IntArray) {
        var u = 0
        for (j in 0 until 17) {
            u += h[j] + c[j]
            h[j] = u and 255
            u = u ushr 8
        }
    }

    private val minusp: IntArray = intArrayOf(5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 252)

    fun crypto_onetimeauth(out: ByteArray, outStart: Int, m: ByteArray, mStart: Int, n: Long, k: ByteArray): Int {
        var mpos = mStart
        var nn = n
        val x = IntArray(17)
        val r = IntArray(17)
        val h = IntArray(17)
        val c = IntArray(17)
        val g = IntArray(17)

        r.fill(0)
        h.fill(0)
        for (j in 0 until 16) r[j] = k[j].toInt() and 0xff
        r[3] = r[3] and 15
        r[4] = r[4] and 252
        r[7] = r[7] and 15
        r[8] = r[8] and 252
        r[11] = r[11] and 15
        r[12] = r[12] and 252
        r[15] = r[15] and 15

        while (nn > 0) {
            c.fill(0)
            val blocksize = if (nn < 16) nn.toInt() else 16
            for (j in 0 until blocksize) c[j] = m[mpos + j].toInt() and 0xff
            c[blocksize] = 1
            mpos += blocksize
            nn -= blocksize
            add1305(h, c)
            for (i in 0 until 17) {
                x[i] = 0
                for (j in 0 until 17) x[i] += h[j] * (if (j <= i) r[i - j] else 320 * r[i + 17 - j])
            }
            for (i in 0 until 17) h[i] = x[i]
            var u = 0
            for (j in 0 until 16) {
                u += h[j]
                h[j] = u and 255
                u = u ushr 8
            }
            u += h[16]
            h[16] = u and 3
            u = 5 * (u ushr 2)
            for (j in 0 until 16) {
                u += h[j]
                h[j] = u and 255
                u = u ushr 8
            }
            u += h[16]
            h[16] = u
        }
        for (j in 0 until 17) g[j] = h[j]
        add1305(h, minusp)
        val s = -(h[16] ushr 7)
        for (j in 0 until 17) h[j] = h[j] xor (s and (g[j] xor h[j]))

        for (j in 0 until 16) c[j] = k[j + 16].toInt() and 0xff
        c[16] = 0
        add1305(h, c)
        for (j in 0 until 16) out[outStart + j] = h[j].toByte()
        return 0
    }

    private fun crypto_onetimeauth_verify(h: ByteArray, hi: Int, m: ByteArray, mi: Int, n: Long, k: ByteArray): Int {
        val x = ByteArray(16)
        crypto_onetimeauth(x, 0, m, mi, n, k)
        return crypto_verify_16(h, hi, x, 0)
    }

    fun crypto_secretbox(c: ByteArray, m: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int {
        if (d < 32) return -1
        crypto_stream_xor(c, m, d, n, k)
        crypto_onetimeauth(c, 16, c, 32, d - 32, c)
        for (i in 0 until 16) c[i] = 0
        return 0
    }

    // In NaClLowLevel.kt

    fun crypto_secretbox_open(m: ByteArray, c: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int {
        if (d < 32) return -1
        val x = ByteArray(32)
        // 1. Derive the one-time authentication key.
        crypto_stream(x, 32, n, k)

        // 2. Verify the MAC. This is the critical step. `status` should be -1 for wrong key.
        val status = crypto_onetimeauth_verify(c, 16, c, 32, d - 32, x)

        // 3. Decrypt regardless.
        crypto_stream_xor(m, c, d, n, k)

        // 4. Act on the status.
        if (status != 0) {
            m.fill(0) // Clear output on failure.
        } else {
            m.fill(0, 0, 32) // Clear padding on success.
        }

        return status
    }

    // --- Elliptic Curve Primitives ---
    private fun set25519(r: LongArray, a: LongArray) { for (i in 0 until 16) r[i] = a[i] }
    private fun car25519(o: LongArray, oOff: Int = 0) {
        for (i in 0 until 16) {
            o[oOff + i] += (1L shl 16)
            val c = o[oOff + i] shr 16
            val nextIndex = (i + 1) * (if (i < 15) 1 else 0)
            o[oOff + nextIndex] += c - 1 + 37 * (c - 1) * (if (i == 15) 1 else 0)
            o[oOff + i] -= c shl 16
        }
    }
    private fun sel25519(p: LongArray, q: LongArray, b: Int) {
        val c = (b - 1).inv().toLong()
        for (i in 0 until 16) {
            val t = c and (p[i] xor q[i])
            p[i] = p[i] xor t
            q[i] = q[i] xor t
        }
    }
    private fun pack25519(o: ByteArray, n: LongArray, nOff: Int = 0) {
        val m = LongArray(16)
        val t = LongArray(16)
        for (i in 0 until 16) t[i] = n[i + nOff]
        car25519(t)
        car25519(t)
        car25519(t)
        for (j in 0 until 2) {
            m[0] = t[0] - 0xffed
            for (i in 1 until 15) {
                m[i] = t[i] - 0xffff - ((m[i - 1] shr 16) and 1)
                m[i - 1] = m[i - 1] and 0xffff
            }
            m[15] = t[15] - 0x7fff - ((m[14] shr 16) and 1)
            val b = ((m[15] shr 16) and 1).toInt()
            m[14] = m[14] and 0xffff
            sel25519(t, m, 1 - b)
        }
        for (i in 0 until 16) {
            o[2 * i] = t[i].toByte()
            o[2 * i + 1] = (t[i] shr 8).toByte()
        }
    }
    private fun neq25519(a: LongArray, b: LongArray): Int {
        val c = ByteArray(32)
        val d = ByteArray(32)
        pack25519(c, a)
        pack25519(d, b)
        return crypto_verify_32(c, 0, d, 0)
    }
    private fun par25519(a: LongArray): Byte {
        val d = ByteArray(32)
        pack25519(d, a)
        return (d[0].toInt() and 1).toByte()
    }

    private fun unpack25519(o: LongArray, n: ByteArray) {
        for (i in 0 until 16) o[i] = (n[2 * i].toInt() and 0xff) + ((n[2 * i + 1].toInt() and 0xff).toLong() shl 8)
        o[15] = o[15] and 0x7fff
    }
    private fun A(o: LongArray, a: LongArray, b: LongArray) { for (i in 0 until 16) o[i] = a[i] + b[i] }
    private fun Z(o: LongArray, a: LongArray, b: LongArray) { for (i in 0 until 16) o[i] = a[i] - b[i] }
    private fun M(o: LongArray, a: LongArray, b: LongArray) {
        val t = LongArray(31)
        for (i in 0 until 16) for (j in 0 until 16) t[i + j] += a[i] * b[j]
        for (i in 0 until 15) t[i] += 38 * t[i + 16]
        for (i in 0 until 16) o[i] = t[i]
        car25519(o)
        car25519(o)
    }
    private fun S(o: LongArray, a: LongArray) = M(o, a, a)
    private fun inv25519(o: LongArray, i: LongArray) {
        val c = i.copyOf()
        for (a in 253 downTo 0) {
            S(c, c)
            if (a != 2 && a != 4) M(c, c, i)
        }
        o.indices.forEach { o[it] = c[it] }
    }
    private fun pow2523(o: LongArray, i: LongArray) {
        val c = i.copyOf()
        for (a in 250 downTo 0) {
            S(c, c)
            if (a != 1) M(c, c, i)
        }
        o.indices.forEach { o[it] = c[it] }
    }

    fun crypto_scalarmult(q: ByteArray, n: ByteArray, p: ByteArray): Int {
        val z = n.copyOf()
        // The large buffer from the C code, used to hold intermediate and final values.
        // We use a LongArray, but logically it's like gf x[5], where each gf is 16 longs.
        val x = LongArray(80)

        val a = LongArray(16); val b = LongArray(16)
        val c = LongArray(16); val d = LongArray(16)
        val e = LongArray(16); val f = LongArray(16)

        // Clamp the secret key
        z[0] = z[0] and 248.toByte()
        z[31] = (z[31] and 127) or 64

        unpack25519(x, p) // Unpack the point p into the first 16 elements of x

        // Initialize projective coordinates
        a.fill(0); b.fill(0); c.fill(0); d.fill(0)
        b.indices.forEach { b[it] = x[it] } // b = x1
        a[0] = 1; d[0] = 1

        // Main Montgomery ladder loop
        for (i in 254 downTo 0) {
            val r = (z[i ushr 3].toUByte().toInt() ushr (i and 7)) and 1
            sel25519(a, b, r)
            sel25519(c, d, r)
            A(e, a, c); Z(a, a, c); A(c, b, d); Z(b, b, d)
            S(d, e); S(f, a); M(a, c, a); M(c, b, e)
            A(e, a, c); Z(a, a, c); S(b, a); Z(c, d, f)
            M(a, c, _121665); A(a, a, d); M(c, c, a); M(a, d, f)
            M(d, b, x) // d = (x3 - z3) * x1
            S(b, e)
            sel25519(a, b, r)
            sel25519(c, d, r)
        }

        // --- Finalization: This is the logic that my previous versions got wrong. ---
        // This part mimics the C code's pointer arithmetic `x+16`, `x+32` by using
        // separate, correctly-sized arrays.

        // The final projective coordinates are (a, c)
        val final_z = c
        val final_x = a

        // 1. Calculate 1/Z
        val z_inv = LongArray(16)
        inv25519(z_inv, final_z)

        // 2. Calculate X * (1/Z) to get the affine coordinate
        val affine_x = LongArray(16)
        M(affine_x, final_x, z_inv)

        // 3. Pack the final result.
        pack25519(q, affine_x)

        return 0
    }

    fun crypto_scalarmult_base(q: ByteArray, n: ByteArray): Int {
        return crypto_scalarmult(q, n, _9)
    }

    fun crypto_box_keypair(y: ByteArray, x: ByteArray): Int {
        randombytes(x, 32)
        return crypto_scalarmult_base(y, x)
    }
    fun crypto_box_beforenm(k: ByteArray, y: ByteArray, x: ByteArray): Int {
        val s = ByteArray(32)
        crypto_scalarmult(s, x, y)
        return crypto_core_hsalsa20(k, _0, s, sigma)
    }
    private fun crypto_box_afternm(c: ByteArray, m: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int = crypto_secretbox(c, m, d, n, k)
    private fun crypto_box_open_afternm(m: ByteArray, c: ByteArray, d: Long, n: ByteArray, k: ByteArray): Int = crypto_secretbox_open(m, c, d, n, k)
    fun crypto_box(c: ByteArray, m: ByteArray, d: Long, n: ByteArray, y: ByteArray, x: ByteArray): Int {
        val k = ByteArray(32)
        crypto_box_beforenm(k, y, x)
        return crypto_box_afternm(c, m, d, n, k)
    }
    fun crypto_box_open(m: ByteArray, c: ByteArray, d: Long, n: ByteArray, y: ByteArray, x: ByteArray): Int {
        val k = ByteArray(32)
        crypto_box_beforenm(k, y, x)
        return crypto_box_open_afternm(m, c, d, n, k)
    }

    fun crypto_hash(outArr: ByteArray, m: ByteArray, n: ULong): Int {
        val digest = SHA512().digest(if (m.size.toULong() == n) m else m.copyOfRange(0, n.toInt()))
        digest.copyInto(outArr)
        return 0
    }

    // --- Ed25519 Signature ---
    private fun add(p: Array<LongArray>, q: Array<LongArray>) {
        val a = LongArray(16); val b = LongArray(16); val c = LongArray(16)
        val d = LongArray(16); val e = LongArray(16); val f = LongArray(16)
        val g = LongArray(16); val h = LongArray(16); val t = LongArray(16)
        Z(a, p[1], p[0]); Z(t, q[1], q[0]); M(a, a, t)
        A(b, p[0], p[1]); A(t, q[0], q[1]); M(b, b, t)
        M(c, p[3], q[3]); M(c, c, D2); M(d, p[2], q[2]); A(d, d, d)
        Z(e, b, a); Z(f, d, c); A(g, d, c); A(h, b, a)
        M(p[0], e, f); M(p[1], h, g); M(p[2], g, f); M(p[3], e, h)
    }
    private fun cswap(p: Array<LongArray>, q: Array<LongArray>, b: Int) { for (i in 0 until 4) sel25519(p[i], q[i], b) }
    fun pack(r: ByteArray, p: Array<LongArray>) {
        val tx = LongArray(16); val ty = LongArray(16); val zi = LongArray(16)
        inv25519(zi, p[2]); M(tx, p[0], zi); M(ty, p[1], zi)
        pack25519(r, ty)
        r[31] = r[31] xor (par25519(tx).toInt() shl 7).toByte()
    }
    private fun scalarmult(p: Array<LongArray>, q: Array<LongArray>, s: ByteArray) {
        set25519(p[0], gf0); set25519(p[1], gf1); set25519(p[2], gf1); set25519(p[3], gf0)
        for (i in 255 downTo 0) {
            val b = (s[i / 8].toUByte().toInt() shr (i and 7)) and 1
            cswap(p, q, b)
            add(q, p)
            add(p, p)
            cswap(p, q, b)
        }
    }
    fun scalarbase(p: Array<LongArray>, s: ByteArray) {
        val q = Array(4) { LongArray(16) }
        set25519(q[0], X); set25519(q[1], Y); set25519(q[2], gf1)
        M(q[3], X, Y)
        scalarmult(p, q, s)
    }
    fun crypto_sign_keypair(pk: ByteArray, sk: ByteArray): Int {
        randombytes(sk, 32)
        return crypto_sign_seed_keypair(pk, sk, sk.sliceArray(0..31))
    }
    fun crypto_sign_seed_keypair(pk: ByteArray, sk: ByteArray, seed: ByteArray): Int {
        val d = ByteArray(64)
        val p = Array(4) { LongArray(16) }
        seed.copyInto(sk, 0)
        crypto_hash(d, sk.sliceArray(0..31), 32u)
        d[0] = d[0] and 248.toByte()
        d[31] = d[31] and 127
        d[31] = d[31] or 64
        scalarbase(p, d)
        pack(pk, p)
        pk.copyInto(sk, 32)
        return 0
    }
    private val L = longArrayOf(
        0xed, 0xd3, 0xf5, 0x5c, 0x1a, 0x63, 0x12, 0x58, 0xd6, 0x9c, 0xf7, 0xa2, 0xde, 0xf9, 0xde, 0x14,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x10)

    private fun modL(r: ByteArray, x: LongArray, ri: Int = 0) {
        var carry: Long
        for (i in 63 downTo 32) {
            carry = 0
            for (j in (i - 32) until (i - 12)) {
                x[j] += carry - 16 * x[i] * L[j - (i - 32)]
                // FIX: Replaced floating-point math with precise integer bit shifts.
                carry = (x[j] + 128) shr 8
                x[j] -= carry shl 8
            }
            x[i - 12] += carry
            x[i] = 0
        }
        carry = 0
        for (j in 0 until 32) {
            x[j] += carry - (x[31] shr 4) * L[j]
            carry = x[j] shr 8
            x[j] = x[j] and 0xff
        }
        for (j in 0 until 32) x[j] -= carry * L[j]
        for (i in 0 until 32) {
            x[i + 1] += x[i] shr 8
            r[i + ri] = (x[i] and 0xff).toByte()
        }
    }
    private fun reduce(r: ByteArray) {
        val x = LongArray(64)
        // FIX: Prevent sign extension by masking with 0xFFL.
        for (i in 0 until 64) x[i] = r[i].toLong() and 0xFFL
        r.fill(0)
        modL(r, x)
    }
    fun crypto_sign(sm: ByteArray, m: ByteArray, n: ULong, sk: ByteArray): Int {
        val d = ByteArray(64); val h = ByteArray(64); val r = ByteArray(64)
        val x = LongArray(64); val p = Array(4) { LongArray(16) }
        val sk32 = sk.sliceArray(0..31)
        crypto_hash(d, sk32, 32u)
        d[0] = d[0] and 248.toByte()
        d[31] = (d[31] and 127) or 64
        m.copyInto(sm, 64)
        d.copyInto(sm, 32, 32, 64)
        crypto_hash(r, sm.sliceArray(32 until (n.toInt() + 64)), n + 32u)
        reduce(r)
        scalarbase(p, r)
        pack(sm, p)
        sk.copyInto(sm, 32, 32, 64)
        crypto_hash(h, sm.sliceArray(0 until (n.toInt() + 64)), n + 64u)
        reduce(h)
        x.fill(0)
        for (i in 0 until 32) x[i] = r[i].toLong() and 0xFFL
        for (i in 0 until 32) for (j in 0 until 32) {
            x[i + j] += (h[i].toLong() and 0xFFL) * (d[j].toLong() and 0xFFL)
        }
        modL(sm, x, 32)
        return 0
    }
    private fun unpackneg(r: Array<LongArray>, p: ByteArray): Int {
        val t = LongArray(16); val chk = LongArray(16); val num = LongArray(16)
        val den = LongArray(16); val den2 = LongArray(16); val den4 = LongArray(16)
        val den6 = LongArray(16)
        set25519(r[2], gf1)
        unpack25519(r[1], p)
        S(num, r[1]); M(den, num, D); Z(num, num, r[2]); A(den, r[2], den)
        S(den2, den); S(den4, den2); M(den6, den4, den2); M(t, den6, num); M(t, t, den)
        pow2523(t, t); M(t, t, num); M(t, t, den); M(t, t, den); M(r[0], t, den)
        S(chk, r[0])
        M(chk, chk, den)
        if (neq25519(chk, num) != 0) M(r[0], r[0], I)
        S(chk, r[0])
        M(chk, chk, den)

        // FIX: Check for failure but do not return early. This is a constant-time pattern.
        var result = neq25519(chk, num)
        if (par25519(r[0]).toInt() == ((p[31].toInt() shr 7) and 1)) Z(r[0], gf0, r[0])
        M(r[3], r[0], r[1])
        return result
    }
    fun crypto_sign_open(m: ByteArray, sm: ByteArray, n: ULong, pk: ByteArray): Int {
        if (n < 64u) return -1
        val t = ByteArray(32); val h = ByteArray(64)
        val p = Array(4) { LongArray(16) }; val q = Array(4) { LongArray(16) }
        val sm_len = n.toInt()
        val m_len = sm_len - 64

        // FIX: Refactored to avoid early returns for constant-time behavior.
        var status = unpackneg(q, pk)
        val temp_m = ByteArray(sm_len)
        sm.copyInto(temp_m, 0, 0, sm_len)
        pk.copyInto(temp_m, 32)
        crypto_hash(h, temp_m, n)
        reduce(h)
        scalarmult(p, q, h)
        scalarbase(q, sm.sliceArray(32 until 64))
        add(p, q)
        pack(t, p)
        status = status or crypto_verify_32(sm, 0, t, 0)
        if (status == 0) {
            sm.copyInto(m, 0, 64, sm_len)
        } else {
            // If verification fails at any point, clear the output buffer.
            m.fill(0, 0, m_len)
        }
        return status
    }
}