@file:OptIn(ExperimentalUnsignedTypes::class)

package io.kodium.core

/**
 * Provides a high-level, safe, and convenient API for the TweetNaCl cryptographic library.
 * This object handles all necessary padding, buffer management, and length checks,
 * wrapping the low-level functions from NaClLowLevel.
 */
object nacl {

    // --- Private Implementation Constants ---
    // These are for managing the zero-padding required by the low-level API.
    private const val ZEROBYTES = 32
    private const val BOXZEROBYTES = 16

    /**
     * Generates a specified number of secure random bytes.
     */
    fun randomBytes(size: Int): ByteArray = NaClLowLevel.randombytes(size)

    /**
     * Authenticated Symmetric Encryption (secretbox)
     */
    object SecretBox {
        const val KeySize = 32
        const val NonceSize = 24
        const val MacSize = 16

        /**
         * Encrypts and authenticates a message using a secret key and a nonce.
         */
        fun seal(message: ByteArray, nonce: ByteArray, key: ByteArray): ByteArray {
            require(key.size == KeySize) { "Invalid key size" }
            require(nonce.size == NonceSize) { "Invalid nonce size" }

            // 1. Create a padded message buffer: [32 zeros][message]
            val m = ByteArray(ZEROBYTES + message.size)
            message.copyInto(m, destinationOffset = ZEROBYTES)

            // 2. Create an output buffer of the same size.
            val c = ByteArray(m.size)

            // 3. Call the low-level seal function.
            NaClLowLevel.crypto_secretbox(c, m, m.size.toLong(), nonce, key)

            // 4. The result in `c` is [16-byte MAC][16 bytes garbage][ciphertext].
            // We return only the MAC and the ciphertext.
            return c.copyOfRange(BOXZEROBYTES, c.size)
        }

        /**
         * Verifies and decrypts a ciphertext.
         */
        fun open(box: ByteArray, nonce: ByteArray, key: ByteArray): ByteArray? {
            require(key.size == KeySize) { "Invalid key size" }
            require(nonce.size == NonceSize) { "Invalid nonce size" }

            // The user provides the `box`, which is [MAC][ciphertext].
            // We need to re-create the format the low-level function expects.
            // 1. Create a buffer for the ciphertext: [16 empty bytes][box]
            val c = ByteArray(BOXZEROBYTES + box.size)
            box.copyInto(c, destinationOffset = BOXZEROBYTES)

            // 2. Create an output buffer for the decrypted, padded message.
            val m = ByteArray(c.size)

            // 3. Call the low-level open function.
            val status = NaClLowLevel.crypto_secretbox_open(m, c, c.size.toLong(), nonce, key)
            if (status != 0) return null

            // 4. If successful, `m` contains [32 bytes garbage][decrypted message].
            // We must slice off the padding before returning.
            return m.copyOfRange(ZEROBYTES, m.size)
        }
    }

    /**
     * Authenticated Public-Key Encryption (box)
     */
    object Box {
        const val PublicKeySize = 32
        const val SecretKeySize = 32
        const val NonceSize = 24
        const val MacSize = 16

        /**
         * Generates a new public/private key pair.
         */
        fun keyPair(): Pair<ByteArray, ByteArray> {
            val pk = ByteArray(PublicKeySize)
            val sk = ByteArray(SecretKeySize)
            NaClLowLevel.crypto_box_keypair(pk, sk)
            return Pair(pk, sk)
        }

        /**
         * Generates a key pair from a given secret key.
         */
        fun keyPairFromSecretKey(secretKey: ByteArray): Pair<ByteArray, ByteArray> {
            require(secretKey.size == SecretKeySize) { "Invalid secret key size" }
            val pk = ByteArray(PublicKeySize)
            NaClLowLevel.crypto_scalarmult_base(pk, secretKey)
            return Pair(pk, secretKey)
        }

        /**
         * Computes the shared secret between a public key and a private key.
         */
        fun beforenm(theirPublicKey: ByteArray, mySecretKey: ByteArray): ByteArray {
            require(theirPublicKey.size == PublicKeySize) { "Invalid public key size" }
            require(mySecretKey.size == SecretKeySize) { "Invalid secret key size" }
            val k = ByteArray(SecretBox.KeySize)
            NaClLowLevel.crypto_box_beforenm(k, theirPublicKey, mySecretKey)
            return k
        }

        /**
         * The full box operation: Encrypts a message for a recipient.
         */
        fun seal(message: ByteArray, nonce: ByteArray, theirPublicKey: ByteArray, mySecretKey: ByteArray): ByteArray {
            // Use the exposed beforenm function
            val k = beforenm(theirPublicKey, mySecretKey)
            return SecretBox.seal(message, nonce, k)
        }

        /**
         * The full open operation: Decrypts a message from a sender.
         */
        fun open(box: ByteArray, nonce: ByteArray, theirPublicKey: ByteArray, mySecretKey: ByteArray): ByteArray? {
            // Use the exposed beforenm function
            val k = beforenm(theirPublicKey, mySecretKey)
            return SecretBox.open(box, nonce, k)
        }
    }
}