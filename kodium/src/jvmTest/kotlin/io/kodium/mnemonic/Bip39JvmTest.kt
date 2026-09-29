package io.kodium.mnemonic

import java.text.Normalizer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Bip39JvmTest {

    /**
     * The embedded fold table must agree with a full NFKD implementation on every code point the JDK
     * knows: a code point folds to letters exactly when its NFKD form is ASCII letters. Code points newer
     * than the JDK's Unicode version are skipped.
     */
    @Test
    fun foldTableAgreesWithJavaNfkd() {
        val letters = Regex("[A-Za-z]+")
        var checked = 0
        for (cp in 0x80..0x10FFFF) {
            if (cp in 0xD800..0xDFFF || !Character.isDefined(cp)) continue
            val nfkd = Normalizer.normalize(String(Character.toChars(cp)), Normalizer.Form.NFKD)
            val expected = nfkd.takeIf { letters.matches(it) }
            assertEquals(expected, Bip39Normalizer.fold(cp), "U+%04X".format(cp))
            checked++
        }
        println("NFKD fold table checked against the JDK for $checked code points")
    }

    @Test
    fun englishListIsUnmodifiable() {
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UNCHECKED_CAST")
        val javaList = Bip39.english as java.util.List<String>
        assertFailsWith<UnsupportedOperationException> { javaList.set(0, "zzz") }
        assertFailsWith<UnsupportedOperationException> { javaList.add("zzz") }
        assertEquals("abandon", Bip39.english[0])
    }
}
