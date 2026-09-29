package io.kodium.mnemonic

import org.kotlincrypto.hash.sha2.SHA256

/**
 * BIP-39 mnemonic encoding of entropy with the English wordlist.
 *
 * A mnemonic is a human-typeable form of 16, 20, 24, 28 or 32 bytes of entropy with a checksum that
 * catches almost every mistyped or swapped word. Kodium uses it as the paper form of a seed for
 * [io.kodium.Kodium.pqc.generateKeyPair]: `Bip39.decode(words)` returns the exact entropy that
 * [encode] was given, and the key is derived from that entropy.
 *
 * Deliberately not included: the BIP-39 mnemonic-to-seed step (PBKDF2-HMAC-SHA512 over the words and a
 * passphrase). Keys are derived from the entropy bytes, never from a BIP-39 seed.
 *
 * Input to [decode], [isWord] and [complete] is normalised first: NFKD, lower case, trimmed, and phrases
 * are split on any run of Unicode whitespace. Only exact words are accepted; a prefix such as "aban" is
 * a completion for [complete], never a word for [decode].
 *
 * Entropy and mnemonics are key material. Do not log them or keep them longer than needed.
 */
object Bip39 {
    private val words: Array<String> = BIP39_ENGLISH_WORDLIST.split(' ').toTypedArray()
    private val indexOf: Map<String, Int> = HashMap<String, Int>(words.size * 2).apply {
        words.forEachIndexed { i, w -> put(w, i) }
    }

    private val entropySizes = setOf(16, 20, 24, 28, 32)
    private val wordCounts = setOf(12, 15, 18, 21, 24)

    /** The 2048-word English list, in BIP-39 order. Immutable. */
    val english: List<String> = object : AbstractList<String>() {
        override val size: Int get() = words.size
        override fun get(index: Int): String = words[index]
    }

    /**
     * Encodes [entropy] as a mnemonic.
     *
     * @param entropy 16, 20, 24, 28 or 32 bytes.
     * @return 12, 15, 18, 21 or 24 words from [english].
     * @throws IllegalArgumentException when [entropy] has any other length.
     */
    fun encode(entropy: ByteArray): List<String> {
        require(entropy.size in entropySizes) { "Entropy must be 16, 20, 24, 28 or 32 bytes" }
        val entropyBits = entropy.size * 8
        val totalBits = entropyBits + entropyBits / 32
        val hash = SHA256().digest(entropy)
        try {
            return List(totalBits / 11) { w ->
                var index = 0
                for (b in w * 11 until w * 11 + 11) {
                    val bit = if (b < entropyBits) bitAt(entropy, b) else bitAt(hash, b - entropyBits)
                    index = (index shl 1) or bit
                }
                words[index]
            }
        } finally {
            hash.fill(0)
        }
    }

    /**
     * Decodes a mnemonic back to its entropy.
     *
     * @return The entropy, or null when a word is not in [english], the number of words is not 12, 15,
     * 18, 21 or 24, or the checksum does not match.
     */
    fun decode(words: List<String>): ByteArray? {
        if (words.size !in wordCounts) return null
        val indices = IntArray(words.size)
        try {
            for (i in words.indices) {
                indices[i] = indexOf[Bip39Normalizer.word(words[i]) ?: return null] ?: return null
            }
            val totalBits = words.size * 11
            val checksumBits = totalBits / 33
            val entropy = ByteArray((totalBits - checksumBits) / 8)
            for (b in 0 until entropy.size * 8) {
                if (indexBit(indices, b) == 1) entropy[b / 8] = (entropy[b / 8].toInt() or (0x80 ushr (b % 8))).toByte()
            }
            val hash = SHA256().digest(entropy)
            var diff = 0
            for (c in 0 until checksumBits) diff = diff or (indexBit(indices, entropy.size * 8 + c) xor bitAt(hash, c))
            hash.fill(0)
            if (diff != 0) {
                entropy.fill(0)
                return null
            }
            return entropy
        } finally {
            indices.fill(0)
        }
    }

    /**
     * Decodes a mnemonic given as one string of whitespace-separated words.
     *
     * @see decode
     */
    fun decode(phrase: String): ByteArray? = decode(Bip39Normalizer.phrase(phrase) ?: return null)

    /** True when [word], after normalisation, is in [english]. For validating each word while typing. */
    fun isWord(word: String): Boolean = Bip39Normalizer.word(word)?.let { it in indexOf } ?: false

    /** The words of [english] starting with [prefix] (after normalisation), in list order. For autocomplete. */
    fun complete(prefix: String): List<String> {
        val normalised = Bip39Normalizer.word(prefix) ?: return emptyList()
        return words.filter { it.startsWith(normalised) }
    }

    private fun bitAt(bytes: ByteArray, bit: Int): Int = (bytes[bit / 8].toInt() ushr (7 - bit % 8)) and 1

    private fun indexBit(indices: IntArray, bit: Int): Int = (indices[bit / 11] ushr (10 - bit % 11)) and 1
}
