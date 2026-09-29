package io.kodium.mnemonic

import io.kodium.Kodium
import io.kodium.SeededHybridVectors
import org.kotlincrypto.hash.sha2.SHA256
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Bip39Test {

    // --- Wordlist ---------------------------------------------------------------------------------

    @Test
    fun wordlistIsTheOfficialEnglishList() {
        val words = Bip39.english
        assertEquals(2048, words.size)
        assertEquals(2048, words.toSet().size)
        assertEquals(words.sorted(), words)
        assertTrue(words.all { it.length in 3..8 && it.all { c -> c in 'a'..'z' } })
        assertEquals(2048, words.map { it.take(4) }.toSet().size)
        // SHA-256 of bip-0039/english.txt (newline-terminated) in github.com/bitcoin/bips.
        val fileHash = SHA256().digest((words.joinToString("\n") + "\n").encodeToByteArray()).toHexString()
        assertEquals("2f5eed53a4727b4bf8880d8f3f199efc90e58503646d9ff8eff3a2ed3b24dbda", fileHash)
    }

    // --- Reference vectors ------------------------------------------------------------------------

    @Test
    fun referenceVectorsEncodeAndDecode() {
        for ((entropy, mnemonic) in Bip39ReferenceVectors.english) {
            assertEquals(mnemonic, Bip39.encode(entropy.hexToByteArray()).joinToString(" "), "encode $entropy")
            assertEquals(entropy, Bip39.decode(mnemonic)?.toHexString(), "decode phrase $entropy")
            assertEquals(entropy, Bip39.decode(mnemonic.split(" "))?.toHexString(), "decode words $entropy")
        }
    }

    // --- Checksum ---------------------------------------------------------------------------------

    @Test
    fun replacingAnySingleWordBreaksTheChecksum() {
        val words = SeededHybridVectors.hybrid[3].mnemonic.split(" ")
        assertEquals(24, words.size)
        assertTrue(Bip39.decode(words) != null)
        for (i in words.indices) {
            val changed = words.toMutableList().also { it[i] = "zoo" }
            assertNull(Bip39.decode(changed), "position $i")
        }
    }

    @Test
    fun flippingOneEntropyBitChangesTheLastWord() {
        val entropy = SeededHybridVectors.hybrid[3].seed.hexToByteArray()
        val flipped = entropy.copyOf().also { it[0] = (it[0].toInt() xor 0x80).toByte() }
        assertEquals("obscure", Bip39.encode(entropy).last())
        assertEquals("meat", Bip39.encode(flipped).last())
    }

    // --- Normalisation ----------------------------------------------------------------------------

    private val allAbandonArt = List(23) { "abandon" } + "art"
    private val allAbandonArtEntropy = ByteArray(32)

    @Test
    fun caseAndSurroundingWhitespaceAreIgnored() {
        val words = allAbandonArt.toMutableList().apply { this[0] = "Abandon"; this[1] = "abandon "; this[2] = "ABANDON"; this[3] = "\tabandon " }
        assertContentEquals(allAbandonArtEntropy, Bip39.decode(words))
        val phrase = "  ABANDON　abandon\n\n" + allAbandonArt.drop(2).joinToString("  ") + " \r\n"
        assertContentEquals(allAbandonArtEntropy, Bip39.decode(phrase))
    }

    @Test
    fun compatibilityCharactersDecomposeToTheirLetters() {
        val words = allAbandonArt.toMutableList().apply {
            this[0] = "ａｂａｎｄｏｎ" // fullwidth "abandon"
            this[1] = "𝐚𝐛𝐚𝐧𝐝𝐨𝐧" // mathematical bold "abandon"
        }
        assertContentEquals(allAbandonArtEntropy, Bip39.decode(words))
        assertTrue(Bip39.isWord("ﬁnal")) // "ﬁ" ligature + "nal" = "final"
        assertTrue(Bip39.isWord("Kind")) // KELVIN SIGN + "ind" = "kind"
        assertEquals(listOf("final", "find", "fine", "finger", "finish"), Bip39.complete("ﬁn"))
    }

    @Test
    fun charactersThatDoNotDecomposeToLettersNeverMatch() {
        assertFalse(Bip39.isWord("abandoné"))
        assertFalse(Bip39.isWord("ab́andon"))
        assertFalse(Bip39.isWord("aban don"))
        assertFalse(Bip39.isWord("abandon1"))
        val words = allAbandonArt.toMutableList().apply { this[5] = "abandón" }
        assertNull(Bip39.decode(words))
        assertNull(Bip39.decode(allAbandonArt.joinToString(" ") + " ¨"))
    }

    @Test
    fun wordsOutsideTheListAndPrefixesAreRejected() {
        assertNull(Bip39.decode(allAbandonArt.toMutableList().apply { this[4] = "bitcoin" }))
        assertNull(Bip39.decode(allAbandonArt.toMutableList().apply { this[4] = "aban" }))
        assertFalse(Bip39.isWord("aban"))
    }

    // --- Lengths ----------------------------------------------------------------------------------

    @Test
    fun everyValidLengthRoundTrips() {
        for ((bytes, count) in listOf(16 to 12, 20 to 15, 24 to 18, 28 to 21, 32 to 24)) {
            val entropy = Kodium.generateHighEntropyKey().copyOf(bytes)
            val words = Bip39.encode(entropy)
            assertEquals(count, words.size)
            assertContentEquals(entropy, Bip39.decode(words))
            assertContentEquals(entropy, Bip39.decode(words.joinToString(" ")))
        }
    }

    @Test
    fun invalidLengthsAreRejected() {
        val twelve = Bip39ReferenceVectors.english[0].second.split(" ")
        assertNull(Bip39.decode(twelve + "abandon"))
        assertNull(Bip39.decode(emptyList()))
        assertNull(Bip39.decode(""))
        assertNull(Bip39.decode("   "))
        for (size in listOf(0, 15, 17, 31, 33, 64)) {
            assertFailsWith<IllegalArgumentException>("size $size") { Bip39.encode(ByteArray(size)) }
        }
    }

    // --- Autocomplete -----------------------------------------------------------------------------

    @Test
    fun completeListsMatchingWordsInOrder() {
        assertEquals(
            listOf("abandon", "ability", "able", "about", "above", "absent", "absorb", "abstract", "absurd", "abuse"),
            Bip39.complete("ab")
        )
        assertEquals(Bip39.complete("ab"), Bip39.complete(" AB "))
        assertEquals(Bip39.english, Bip39.complete(""))
        assertEquals(emptyList(), Bip39.complete("zzz"))
        assertEquals(listOf("zoo"), Bip39.complete("zoo"))
    }

    @Test
    fun isWordAgreesWithDecode() {
        for (word in Bip39.english) assertTrue(Bip39.isWord(word), word)
        for (word in listOf("", "aban", "bitcoin", "zoos", "abandon about")) {
            assertFalse(Bip39.isWord(word), word)
            assertNull(Bip39.decode(allAbandonArt.toMutableList().apply { this[0] = word }), word)
        }
    }

    // --- Mnemonics and seeded keys ----------------------------------------------------------------

    @Test
    fun wordsRestoreTheSeededKeyVectors() {
        for (v in SeededHybridVectors.hybrid) {
            val words = Bip39.encode(v.seed.hexToByteArray())
            assertEquals(v.mnemonic, words.joinToString(" "))
            val entropy = Bip39.decode(words)!!
            assertEquals(v.seed, entropy.toHexString())
            assertEquals(v.privateKey, Kodium.pqc.generateKeyPair(entropy).exportToArray().toHexString())
        }
    }

    @Test
    fun decodedEntropyIsAFreshArray() {
        val words = Bip39.encode(ByteArray(32) { 7 })
        val first = Bip39.decode(words)!!
        first.fill(0)
        assertNotEquals(first.toHexString(), Bip39.decode(words)!!.toHexString())
    }
}
