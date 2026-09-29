package io.kodium.mnemonic

/**
 * BIP-39 input normalisation (NFKD, lower case, trimmed) for a wordlist made of ASCII letters only.
 *
 * Kotlin common code has no Unicode normaliser, and platform normalisers differ in Unicode version. A word
 * can only match an ASCII wordlist if its NFKD form is ASCII, so it is enough to know, for every non-ASCII
 * code point, whether its NFKD form consists of ASCII letters and which ones. That table (from the Unicode
 * 18.0 character database: fullwidth and mathematical letters, ligatures such as ﬁ, letterlike symbols
 * such as the Kelvin sign, and so on) is embedded below, so the result is identical on every platform.
 * Any other non-ASCII code point makes the word unmatchable, which is what full NFKD would lead to as well:
 * its decomposition keeps a non-ASCII character. Canonical reordering only moves combining marks, which
 * make a word unmatchable anyway.
 */
internal object Bip39Normalizer {

    /**
     * Normalises one word: [word] folded, lower-cased and trimmed, or null when it cannot equal any
     * ASCII word. Whitespace inside the result is kept (as a space), so such a word matches nothing.
     */
    fun word(word: String): String? {
        val out = StringBuilder(word.length)
        if (!appendFolded(word, out)) return null
        return out.toString().trim(' ')
    }

    /**
     * Normalises a phrase and splits it on runs of Unicode whitespace, or returns null when any word
     * cannot equal an ASCII word.
     */
    fun phrase(phrase: String): List<String>? {
        val out = StringBuilder(phrase.length)
        if (!appendFolded(phrase, out)) return null
        return out.split(' ').filter { it.isNotEmpty() }
    }

    private fun appendFolded(text: String, out: StringBuilder): Boolean {
        var i = 0
        while (i < text.length) {
            val c = text[i]
            val cp: Int
            if (c.isHighSurrogate() && i + 1 < text.length && text[i + 1].isLowSurrogate()) {
                cp = 0x10000 + ((c.code - 0xD800) shl 10) + (text[i + 1].code - 0xDC00)
                i += 2
            } else {
                cp = c.code
                i += 1
            }
            when {
                isWhiteSpace(cp) -> out.append(' ')
                cp < 0x80 -> out.append(lowerAscii(cp))
                else -> {
                    val folded = fold(cp) ?: return false
                    for (f in folded) out.append(lowerAscii(f.code))
                }
            }
        }
        return true
    }

    private fun lowerAscii(cp: Int): Char = if (cp in 'A'.code..'Z'.code) (cp + 32).toChar() else cp.toChar()

    // Unicode White_Space property (PropList.txt); NFKD maps each of these to White_Space again.
    private fun isWhiteSpace(cp: Int): Boolean =
        cp in 0x09..0x0D || cp == 0x20 || cp == 0x85 || cp == 0xA0 || cp == 0x1680 || cp in 0x2000..0x200A ||
            cp == 0x2028 || cp == 0x2029 || cp == 0x202F || cp == 0x205F || cp == 0x3000

    /** The NFKD form of [cp] when it consists of ASCII letters only, otherwise null. */
    internal fun fold(cp: Int): String? {
        var lo = 0
        var hi = singleLetterRuns.size / 3 - 1
        while (lo <= hi) {
            val mid = (lo + hi) ushr 1
            val start = singleLetterRuns[3 * mid]
            when {
                cp < start -> hi = mid - 1
                cp >= start + singleLetterRuns[3 * mid + 1] -> lo = mid + 1
                else -> return (singleLetterRuns[3 * mid + 2] + (cp - start)).toChar().toString()
            }
        }
        return multiLetter[cp]
    }

    // start, count, first letter: code points start until start + count fold to consecutive letters
    private val singleLetterRuns = intArrayOf(
        0xAA, 1, 'a'.code, 0xBA, 1, 'o'.code, 0x17F, 1, 's'.code, 0x2B0, 1, 'h'.code, 0x2B2, 1, 'j'.code, 0x2B3, 1, 'r'.code,
        0x2B7, 1, 'w'.code, 0x2B8, 1, 'y'.code, 0x2E1, 1, 'l'.code, 0x2E2, 1, 's'.code, 0x2E3, 1, 'x'.code, 0x1D2C, 1, 'A'.code,
        0x1D2E, 1, 'B'.code, 0x1D30, 2, 'D'.code, 0x1D33, 8, 'G'.code, 0x1D3C, 1, 'O'.code, 0x1D3E, 1, 'P'.code, 0x1D3F, 1, 'R'.code,
        0x1D40, 2, 'T'.code, 0x1D42, 1, 'W'.code, 0x1D43, 1, 'a'.code, 0x1D47, 1, 'b'.code, 0x1D48, 2, 'd'.code, 0x1D4D, 1, 'g'.code,
        0x1D4F, 1, 'k'.code, 0x1D50, 1, 'm'.code, 0x1D52, 1, 'o'.code, 0x1D56, 1, 'p'.code, 0x1D57, 2, 't'.code, 0x1D5B, 1, 'v'.code,
        0x1D62, 1, 'i'.code, 0x1D63, 1, 'r'.code, 0x1D64, 2, 'u'.code, 0x1D9C, 1, 'c'.code, 0x1DA0, 1, 'f'.code, 0x1DBB, 1, 'z'.code,
        0x2071, 1, 'i'.code, 0x207F, 1, 'n'.code, 0x2090, 1, 'a'.code, 0x2091, 1, 'e'.code, 0x2092, 1, 'o'.code, 0x2093, 1, 'x'.code,
        0x2095, 1, 'h'.code, 0x2096, 4, 'k'.code, 0x209A, 1, 'p'.code, 0x209B, 2, 's'.code, 0x209D, 1, 'w'.code, 0x209E, 2, 'y'.code,
        0x2102, 1, 'C'.code, 0x210A, 1, 'g'.code, 0x210B, 1, 'H'.code, 0x210C, 1, 'H'.code, 0x210D, 1, 'H'.code, 0x210E, 1, 'h'.code,
        0x2110, 1, 'I'.code, 0x2111, 1, 'I'.code, 0x2112, 1, 'L'.code, 0x2113, 1, 'l'.code, 0x2115, 1, 'N'.code, 0x2119, 3, 'P'.code,
        0x211C, 1, 'R'.code, 0x211D, 1, 'R'.code, 0x2124, 1, 'Z'.code, 0x2128, 1, 'Z'.code, 0x212A, 1, 'K'.code, 0x212C, 2, 'B'.code,
        0x212F, 1, 'e'.code, 0x2130, 2, 'E'.code, 0x2133, 1, 'M'.code, 0x2134, 1, 'o'.code, 0x2139, 1, 'i'.code, 0x2145, 1, 'D'.code,
        0x2146, 2, 'd'.code, 0x2148, 2, 'i'.code, 0x2160, 1, 'I'.code, 0x2164, 1, 'V'.code, 0x2169, 1, 'X'.code, 0x216C, 1, 'L'.code,
        0x216D, 2, 'C'.code, 0x216F, 1, 'M'.code, 0x2170, 1, 'i'.code, 0x2174, 1, 'v'.code, 0x2179, 1, 'x'.code, 0x217C, 1, 'l'.code,
        0x217D, 2, 'c'.code, 0x217F, 1, 'm'.code, 0x24B6, 26, 'A'.code, 0x24D0, 26, 'a'.code, 0x2C7C, 1, 'j'.code, 0x2C7D, 1, 'V'.code,
        0xA7F1, 1, 'S'.code, 0xA7F2, 1, 'C'.code, 0xA7F3, 1, 'F'.code, 0xA7F4, 1, 'Q'.code, 0xFF21, 26, 'A'.code, 0xFF41, 26, 'a'.code,
        0x107A5, 1, 'q'.code, 0x1CCD6, 26, 'A'.code, 0x1D400, 26, 'A'.code, 0x1D41A, 26, 'a'.code, 0x1D434, 26, 'A'.code, 0x1D44E, 7, 'a'.code,
        0x1D456, 18, 'i'.code, 0x1D468, 26, 'A'.code, 0x1D482, 26, 'a'.code, 0x1D49C, 1, 'A'.code, 0x1D49E, 2, 'C'.code, 0x1D4A2, 1, 'G'.code,
        0x1D4A5, 2, 'J'.code, 0x1D4A9, 4, 'N'.code, 0x1D4AE, 8, 'S'.code, 0x1D4B6, 4, 'a'.code, 0x1D4BB, 1, 'f'.code, 0x1D4BD, 7, 'h'.code,
        0x1D4C5, 11, 'p'.code, 0x1D4D0, 26, 'A'.code, 0x1D4EA, 26, 'a'.code, 0x1D504, 2, 'A'.code, 0x1D507, 4, 'D'.code, 0x1D50D, 8, 'J'.code,
        0x1D516, 7, 'S'.code, 0x1D51E, 26, 'a'.code, 0x1D538, 2, 'A'.code, 0x1D53B, 4, 'D'.code, 0x1D540, 5, 'I'.code, 0x1D546, 1, 'O'.code,
        0x1D54A, 7, 'S'.code, 0x1D552, 26, 'a'.code, 0x1D56C, 26, 'A'.code, 0x1D586, 26, 'a'.code, 0x1D5A0, 26, 'A'.code, 0x1D5BA, 26, 'a'.code,
        0x1D5D4, 26, 'A'.code, 0x1D5EE, 26, 'a'.code, 0x1D608, 26, 'A'.code, 0x1D622, 26, 'a'.code, 0x1D63C, 26, 'A'.code, 0x1D656, 26, 'a'.code,
        0x1D670, 26, 'A'.code, 0x1D68A, 26, 'a'.code, 0x1F12B, 1, 'C'.code, 0x1F12C, 1, 'R'.code, 0x1F130, 26, 'A'.code,
    )

    private val multiLetter = mapOf(
        0x132 to "IJ", 0x133 to "ij", 0x1C7 to "LJ", 0x1C8 to "Lj", 0x1C9 to "lj", 0x1CA to "NJ",
        0x1CB to "Nj", 0x1CC to "nj", 0x1F1 to "DZ", 0x1F2 to "Dz", 0x1F3 to "dz", 0x20A8 to "Rs",
        0x2116 to "No", 0x2120 to "SM", 0x2121 to "TEL", 0x2122 to "TM", 0x213B to "FAX", 0x2161 to "II",
        0x2162 to "III", 0x2163 to "IV", 0x2165 to "VI", 0x2166 to "VII", 0x2167 to "VIII", 0x2168 to "IX",
        0x216A to "XI", 0x216B to "XII", 0x2171 to "ii", 0x2172 to "iii", 0x2173 to "iv", 0x2175 to "vi",
        0x2176 to "vii", 0x2177 to "viii", 0x2178 to "ix", 0x217A to "xi", 0x217B to "xii", 0x3250 to "PTE",
        0x32CC to "Hg", 0x32CD to "erg", 0x32CE to "eV", 0x32CF to "LTD", 0x3371 to "hPa", 0x3372 to "da",
        0x3373 to "AU", 0x3374 to "bar", 0x3375 to "oV", 0x3376 to "pc", 0x3377 to "dm", 0x337A to "IU",
        0x3380 to "pA", 0x3381 to "nA", 0x3383 to "mA", 0x3384 to "kA", 0x3385 to "KB", 0x3386 to "MB",
        0x3387 to "GB", 0x3388 to "cal", 0x3389 to "kcal", 0x338A to "pF", 0x338B to "nF", 0x338E to "mg",
        0x338F to "kg", 0x3390 to "Hz", 0x3391 to "kHz", 0x3392 to "MHz", 0x3393 to "GHz", 0x3394 to "THz",
        0x3396 to "ml", 0x3397 to "dl", 0x3398 to "kl", 0x3399 to "fm", 0x339A to "nm", 0x339C to "mm",
        0x339D to "cm", 0x339E to "km", 0x33A9 to "Pa", 0x33AA to "kPa", 0x33AB to "MPa", 0x33AC to "GPa",
        0x33AD to "rad", 0x33B0 to "ps", 0x33B1 to "ns", 0x33B3 to "ms", 0x33B4 to "pV", 0x33B5 to "nV",
        0x33B7 to "mV", 0x33B8 to "kV", 0x33B9 to "MV", 0x33BA to "pW", 0x33BB to "nW", 0x33BD to "mW",
        0x33BE to "kW", 0x33BF to "MW", 0x33C3 to "Bq", 0x33C4 to "cc", 0x33C5 to "cd", 0x33C8 to "dB",
        0x33C9 to "Gy", 0x33CA to "ha", 0x33CB to "HP", 0x33CC to "in", 0x33CD to "KK", 0x33CE to "KM",
        0x33CF to "kt", 0x33D0 to "lm", 0x33D1 to "ln", 0x33D2 to "log", 0x33D3 to "lx", 0x33D4 to "mb",
        0x33D5 to "mil", 0x33D6 to "mol", 0x33D7 to "PH", 0x33D9 to "PPM", 0x33DA to "PR", 0x33DB to "sr",
        0x33DC to "Sv", 0x33DD to "Wb", 0x33FF to "gal", 0xFB00 to "ff", 0xFB01 to "fi", 0xFB02 to "fl",
        0xFB03 to "ffi", 0xFB04 to "ffl", 0xFB05 to "st", 0xFB06 to "st", 0x1F12D to "CD", 0x1F12E to "WZ",
        0x1F14A to "HV", 0x1F14B to "MV", 0x1F14C to "SD", 0x1F14D to "SS", 0x1F14E to "PPV", 0x1F14F to "WC",
        0x1F16A to "MC", 0x1F16B to "MD", 0x1F16C to "MR", 0x1F190 to "DJ",
    )
}
