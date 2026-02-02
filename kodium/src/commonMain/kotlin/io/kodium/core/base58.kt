package io.kodium.core

import org.kotlincrypto.hash.sha2.SHA256


/**
 * Base58 is a way to encode addresses (or arbitrary data) as alphanumeric strings.
 * Compared to base64, this encoding eliminates ambiguities created by O0Il and potential splits from punctuation
 *
 * The basic idea of the encoding is to treat the data bytes as a large number represented using
 * base-256 digits, convert the number to be represented using base-58 digits, preserve the exact
 * number of leading zeros (which are otherwise lost during the mathematical operations on the
 * numbers), and finally represent the resulting base-58 digits as alphanumeric ASCII characters.
 *
 * This is the Kotlin implementation of base58 - it is based implementation of base58 in java
 * in bitcoinj (https://bitcoinj.github.io) - thanks to  Google Inc. and Andreas Schildbach
 *
 */

private const val ENCODED_ZERO = '1'
private const val CHECKSUM_SIZE = 4

private const val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
private val alphabetIndices by lazy {
    IntArray(128) { alphabet.indexOf(it.toChar()) }
}

/**
 * Encodes a byte array into a Base58-encoded string. Base58 encoding is typically used for
 * encoding binary data in a format that is easier for humans to work with, such as in the case
 * of cryptocurrency addresses or other identifiers.
 *
 * The encoding process skips leading zeroes, encodes the data to Base58, and preserves the number
 * of leading zeroes by representing them as the special Base58 character for zero.
 *
 * @return a Base58-encoded string representation of the byte array
 */
fun ByteArray.encodeToBase58String(): String {

    val input = copyOf(size) // since we modify it in-place
    if (input.isEmpty()) {
        return ""
    }
    // Count leading zeros.
    var zeros = 0
    while (zeros < input.size && input[zeros].toInt() == 0) {
        ++zeros
    }
    // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
    val encoded = CharArray(input.size * 2) // upper bound
    var outputStart = encoded.size
    var inputStart = zeros
    while (inputStart < input.size) {
        encoded[--outputStart] =
            alphabet[divmod(input, inputStart.toUInt(), 256.toUInt(), 58.toUInt()).toInt()]
        if (input[inputStart].toInt() == 0) {
            ++inputStart // optimization - skip leading zeros
        }
    }
    // Preserve exactly as many leading encoded zeros in output as there were leading zeros in data.
    while (outputStart < encoded.size && encoded[outputStart] == ENCODED_ZERO) {
        ++outputStart
    }
    while (--zeros >= 0) {
        encoded[--outputStart] = ENCODED_ZERO
    }
    // Return encoded string (including encoded leading zeros).
    return encoded.concatToString(outputStart, outputStart + (encoded.size - outputStart))
}

/**
 * Decodes the current string, which is encoded in Base58 format, into a byte array.
 * Base58 is a binary-to-text encoding designed to create shorter and more human-readable
 * strings without problematic characters that are easily confused (e.g., '0' and 'O').
 *
 * @throws NumberFormatException if the input string contains characters not valid in Base58 encoding.
 * @return A byte array representing the decoded data of the Base58-encoded string.
 */
@Throws(NumberFormatException::class)
fun String.decodeBase58(): ByteArray {
    if (isEmpty()) {
        return ByteArray(0)
    }
    // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
    val input58 = ByteArray(length)
    for (i in 0 until length) {
        val c = this[i]
        val digit = if (c.code < 128) alphabetIndices[c.code] else -1
        if (digit < 0) {
            throw NumberFormatException("Illegal character $c at position $i")
        }
        input58[i] = digit.toByte()
    }
    // Count leading zeros.
    var zeros = 0
    while (zeros < input58.size && input58[zeros].toInt() == 0) {
        ++zeros
    }
    // Convert base-58 digits to base-256 digits.
    val decoded = ByteArray(length)
    var outputStart = decoded.size
    var inputStart = zeros
    while (inputStart < input58.size) {
        decoded[--outputStart] =
            divmod(input58, inputStart.toUInt(), 58.toUInt(), 256.toUInt()).toByte()
        if (input58[inputStart].toInt() == 0) {
            ++inputStart // optimization - skip leading zeros
        }
    }
    // Ignore extra leading zeroes that were added during the calculation.
    while (outputStart < decoded.size && decoded[outputStart].toInt() == 0) {
        ++outputStart
    }
    // Return decoded data (including original number of leading zeros).
    return decoded.copyOfRange(outputStart - zeros, decoded.size)
}

/**
 * Performs a division and modulus operation on a given byte array representing a number in a specified base.
 * The result of the division is stored back in the `number` array, and the remainder is returned.
 *
 * @param number The array of bytes representing the number in a specified base. Modified in-place to store the result of the division.
 * @param firstDigit The index in the `number` array from which the division should begin.
 * @param base The base of the number representation.
 * @param divisor The value by which the number is divided.
 * @return The remainder of the division operation as an unsigned integer.
 */
private fun divmod(number: ByteArray, firstDigit: UInt, base: UInt, divisor: UInt): UInt {
    // this is just long division which accounts for the base of the input digits
    var remainder = 0.toUInt()
    for (i in firstDigit until number.size.toUInt()) {
        val digit = number[i.toInt()].toUByte()
        val temp = remainder * base + digit
        number[i.toInt()] = (temp / divisor).toByte()
        remainder = temp % divisor
    }
    return remainder
}

/**
 * Encodes the current byte array to a Base58-encoded string with an appended checksum. The checksum
 * is generated by applying a double SHA-256 hash to the byte array and then appending the first four
 * bytes of the result to the original array before encoding.
 *
 * The resulting string is useful for cases where integrity verification is required, such as in
 * cryptocurrency transactions or identifiers.
 *
 * @return A Base58-encoded string representation of the byte array with the checksum appended.
 */
fun ByteArray.encodeToBase58WithChecksum(): String = ByteArray(size + CHECKSUM_SIZE).apply {
    this@encodeToBase58WithChecksum.copyInto(this, 0, 0, this@encodeToBase58WithChecksum.size)
    val checksum = this@encodeToBase58WithChecksum.sha256().sha256()
    checksum.copyInto(this, this@encodeToBase58WithChecksum.size, 0, CHECKSUM_SIZE)
}.encodeToBase58String()

/**
 * Decodes a Base58-encoded string with a checksum validation. The method first decodes the Base58 string,
 * separates the checksum from the payload, computes the checksum of the payload, and verifies it against the
 * decoded checksum. If the checksum validation fails, an exception is thrown.
 *
 * @throws IllegalArgumentException if the input string is too short for containing a checksum or if
 * the checksum validation fails.
 * @return A byte array representing the decoded payload of the Base58-encoded string after verifying the checksum.
 */
fun String.decodeBase58WithChecksum(): ByteArray {
    val rawBytes = decodeBase58()
    if (rawBytes.size < CHECKSUM_SIZE) {
        throw IllegalArgumentException("Too short for checksum: $this l:  ${rawBytes.size}")
    }
    val checksum = rawBytes.copyOfRange(rawBytes.size - CHECKSUM_SIZE, rawBytes.size)

    val payload = rawBytes.copyOfRange(0, rawBytes.size - CHECKSUM_SIZE)

    val hash = payload.sha256().sha256()
    val computedChecksum = hash.copyOfRange(0, CHECKSUM_SIZE)

    if (checksum.contentEquals(computedChecksum)) {
        return payload
    } else {
        throw IllegalArgumentException("Checksum mismatch: $checksum is not computed checksum $computedChecksum")
    }
}


/**
 * Computes the SHA-256 cryptographic hash of the current byte array.
 *
 * This method transforms the input byte array into a fixed-size, 32-byte hash value using
 * the SHA-256 algorithm, which is widely used for secure hashing. The resulting hash
 * is suitable for verifying data integrity or storing password hashes.
 *
 * @return A new byte array containing the SHA-256 hash of the input byte array.
 */
private fun ByteArray.sha256(): ByteArray = SHA256().digest(this)

/**
 * Computes the SHA-256 cryptographic hash of the current string.
 *
 * This method transforms the input string into a fixed-size, 32-byte hash value using
 * the SHA-256 algorithm. The string is first encoded into a byte array using UTF-8
 * encoding, and then the hash is computed. The resulting hash is suitable for verifying
 * data integrity or storing hashed representations of sensitive data.
 *
 * @return A byte array containing the SHA-256 hash of the input string.
 */
private fun String.sha256(): ByteArray = this.encodeToByteArray().sha256()