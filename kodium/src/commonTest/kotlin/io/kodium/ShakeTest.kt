package io.kodium

import io.kodium.core.MLKEM
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertContentEquals

class ShakeTest {
    @Test
    fun testMlKem() {
        val (pk, sk) = MLKEM.keyPair()
        val (ss1, ct) = MLKEM.encapsulate(pk)
        val ss2 = MLKEM.decapsulate(ct, sk)
        
        if (!ss1.contentEquals(ss2)) {
            println("SS1: ${ss1.joinToString("") { it.toUByte().toString(16).padStart(2, '0') }}")
            println("SS2: ${ss2?.joinToString("") { it.toUByte().toString(16).padStart(2, '0') }}")
        }
        
        assertContentEquals(ss1, ss2, "Shared secrets must match")
    }
}
