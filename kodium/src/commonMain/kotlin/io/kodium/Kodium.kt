package io.kodium

import io.kodium.core.KDF
import io.kodium.core.decodeBase64WithChecksum
import io.kodium.core.encodeToBase64WithChecksum
import io.kodium.core.MLKEM
import io.kodium.core.nacl
import io.kodium.ratchet.HKDF

/**
 * Kodium is a cryptographic utility object for key pair generation, encryption, and decryption.
 * It supports both asymmetric and symmetric encryption methods, allowing for secure communication
 * and data storage. Encoded data is managed using Base64 encoding with checksum for integrity verification.
 *
 * The object leverages NaCl for encryption backend and HMAC for password-based symmetric encryption.
 */
object Kodium {

    const val PBKDF2_ITERATIONS = 600_000 // A reasonable minimum. Increase for more security.
    const val SALT_SIZE_BYTES = 16

    /**
     * Generates a high-entropy, random symmetric key.
     * This key can be used for operations requiring a precomputed ByteArray key.
     *
     * @return A securely generated 32-byte (256-bit) array.
     */
    fun generateHighEntropyKey(): ByteArray {
        return nacl.randomBytes(nacl.SecretBox.KeySize)
    }

    /**
     * Generates a high-entropy, random salt to be used with key derivation functions (like PBKDF2).
     * The generated salt should be stored (usually alongside the encrypted data) to allow key reconstruction later.
     *
     * @return A securely generated 16-byte random array.
     */
    fun generateRandomSalt(): ByteArray {
        return nacl.randomBytes(SALT_SIZE_BYTES)
    }

    /**
     * Derives a symmetric key from a password and a salt using PBKDF2.
     * 
     * @param password The user-provided password.
     * @param salt The salt used for derivation. Must be securely stored alongside the encrypted data or randomly generated and preserved if derivation must be repeated.
     * @param keyDerivationIterations The number of PBKDF2 iterations to use.
     * @return A derived 32-byte (256-bit) symmetric key.
     */
    fun deriveKeyFromPassword(password: String, salt: ByteArray, keyDerivationIterations: Int = PBKDF2_ITERATIONS): ByteArray {
        return KDF.deriveKey(
            password = password.encodeToByteArray(),
            salt = salt,
            iterations = keyDerivationIterations,
            keyLengthBytes = nacl.SecretBox.KeySize
        )
    }

    /**
     * Post-Quantum Cryptography (PQC) operations.
     * This namespace provides hybrid cryptographic primitives that combine classical X25519
     * with Post-Quantum ML-KEM (Kyber) to ensure security against both classical and future quantum computers.
     */
    object pqc {

        /**
         * Generates a hybrid post-quantum key pair.
         *
         * @return A KodiumPqcPrivateKey instance containing both classical and PQC keys.
         */
        fun generateKeyPair(): KodiumPqcPrivateKey {
            return KodiumPqcPrivateKey.generate()
        }

        /**
         * Encrypts data using a hybrid approach (X25519 + ML-KEM).
         *
         * **WARNING: Lack of Forward Secrecy.**
         * This method uses static keys for encryption. If either the sender's or
         * receiver's long-term key is compromised, all past messages encrypted between them can be
         * decrypted. For continuous, secure communication with forward secrecy and break-in recovery,
         * use [io.kodium.ratchet.PQDoubleRatchetSession] instead.
         *
         * @param mySecretKey The sender's hybrid private key.
         * @param theirPublicKey The recipient's hybrid public key.
         * @param data The data to be encrypted.
         * @return A Result containing the encrypted data.
         */
        fun encrypt(
            mySecretKey: KodiumPqcPrivateKey,
            theirPublicKey: KodiumPqcPublicKey,
            data: ByteArray
        ): Result<ByteArray> {
            return try {
                val nonce = nacl.randomBytes(nacl.Box.NonceSize)

                // 1. Perform ML-KEM encapsulation
                val (sharedSecretPqc, kemCiphertext) = MLKEM.encapsulate(theirPublicKey.pqcPublicKey)

                // 2. Compute classical shared secret
                val sharedSecretClassical = nacl.Box.beforenm(
                    theirPublicKey.classicalPublicKey,
                    mySecretKey.classicalSecretKey
                )

                // 3. Mix secrets using HKDF
                val mixedKey = HKDF.extract(salt = kemCiphertext, ikm = sharedSecretClassical + sharedSecretPqc)

                // 4. Seal data using the mixed key
                val box = nacl.SecretBox.seal(data, nonce, mixedKey)

                Result.success(nonce + kemCiphertext + box)
            } catch (err: Throwable) {
                Result.failure(err)
            }
        }

        /**
         * Decrypts data using a hybrid approach (X25519 + ML-KEM).
         *
         * @param mySecretKey The recipient's hybrid private key.
         * @param theirPublicKey The sender's hybrid public key.
         * @param cipher The encrypted data.
         * @return A Result containing the decrypted data.
         */
        fun decrypt(
            mySecretKey: KodiumPqcPrivateKey,
            theirPublicKey: KodiumPqcPublicKey,
            cipher: ByteArray
        ): Result<ByteArray> {
            return try {
                val minSize = nacl.Box.NonceSize + MLKEM.CiphertextSize + nacl.SecretBox.MacSize
                if (cipher.size < minSize) {
                    return Result.failure(Error("Invalid cipher size"))
                }

                val nonce = cipher.copyOfRange(0, nacl.Box.NonceSize)
                val kemCiphertext = cipher.copyOfRange(nacl.Box.NonceSize, nacl.Box.NonceSize + MLKEM.CiphertextSize)
                val box = cipher.copyOfRange(nacl.Box.NonceSize + MLKEM.CiphertextSize, cipher.size)

                // 1. Perform ML-KEM decapsulation
                val sharedSecretPqc = MLKEM.decapsulate(kemCiphertext, mySecretKey.pqcSecretKey)
                    ?: return Result.failure(Error("ML-KEM decapsulation failed"))

                // 2. Compute classical shared secret
                val sharedSecretClassical = nacl.Box.beforenm(
                    theirPublicKey.classicalPublicKey,
                    mySecretKey.classicalSecretKey
                )

                // 3. Mix secrets using HKDF
                val mixedKey = HKDF.extract(salt = kemCiphertext, ikm = sharedSecretClassical + sharedSecretPqc)

                // 4. Open the box
                val res = nacl.SecretBox.open(box, nonce, mixedKey)

                if (res != null) {
                    Result.success(res)
                } else {
                    Result.failure(Error("Failed to decrypt data, check your keys and try again"))
                }
            } catch (err: Throwable) {
                Result.failure(err)
            }
        }

        /**
         * Encrypts the given data using hybrid PQC and encodes the result to a Base64 string with a checksum.
         *
         * **WARNING: Lack of Forward Secrecy.**
         * This method uses static keys for encryption. If either the sender's or
         * receiver's long-term key is compromised, all past messages encrypted between them can be
         * decrypted. For continuous, secure communication with forward secrecy and break-in recovery,
         * use [io.kodium.ratchet.PQDoubleRatchetSession] instead.
         */
        fun encryptToEncodedString(
            mySecretKey: KodiumPqcPrivateKey,
            theirPublicKey: KodiumPqcPublicKey,
            data: ByteArray
        ): Result<String> {
            return encrypt(mySecretKey, theirPublicKey, data).mapCatching { it.encodeToBase64WithChecksum() }
        }

        /**
         * Decrypts an encoded string using hybrid PQC.
         */
        fun decryptFromEncodedString(
            mySecretKey: KodiumPqcPrivateKey,
            theirPublicKey: KodiumPqcPublicKey,
            data: String
        ): Result<ByteArray> {
            return decrypt(mySecretKey, theirPublicKey, data.decodeBase64WithChecksum())
        }

        /**
         * Signs data using the classical Ed25519 component of the PQC Private Key.
         * Returns the detached signature as a Base64 encoded string.
         * Note: This signature is purely classical (Ed25519) as PQC signatures (e.g. ML-DSA) are not yet integrated.
         */
        fun signDetachedToEncodedString(mySecretKey: KodiumPqcPrivateKey, data: ByteArray): Result<String> {
            return signDetached(mySecretKey, data).mapCatching { it.encodeToBase64WithChecksum() }
        }

        /**
         * Signs data using the classical Ed25519 component of the PQC Private Key.
         * Returns the detached signature as a raw ByteArray.
         */
        fun signDetached(mySecretKey: KodiumPqcPrivateKey, data: ByteArray): Result<ByteArray> {
            return Kodium.signDetached(KodiumPrivateKey.fromRaw(mySecretKey.classicalSecretKey), data)
        }

        /**
         * Verifies a Base64 encoded detached signature against a message using the classical Ed25519 public key.
         */
        fun verifyDetachedFromEncodedString(theirPublicKey: KodiumPqcPublicKey, data: ByteArray, signatureBase64: String): Boolean {
            return try {
                verifyDetached(theirPublicKey, data, signatureBase64.decodeBase64WithChecksum())
            } catch (err: Throwable) {
                false
            }
        }

        /**
         * Verifies a raw detached signature against a message using the classical Ed25519 public key.
         */
        fun verifyDetached(theirPublicKey: KodiumPqcPublicKey, data: ByteArray, signature: ByteArray): Boolean {
            return Kodium.verifyDetached(theirPublicKey.classicalSignPublicKey, data, signature)
        }
    }

    /**
     * Signs data using the Ed25519 Secret Key.
     * Returns the detached signature as a Base64 encoded string.
     */
    fun signDetachedToEncodedString(mySecretKey: KodiumPrivateKey, data: ByteArray): Result<String> {
        return signDetached(mySecretKey, data).mapCatching { it.encodeToBase64WithChecksum() }
    }

    /**
     * Signs data using the Ed25519 Secret Key.
     * Returns the detached signature as a raw ByteArray.
     */
    fun signDetached(mySecretKey: KodiumPrivateKey, data: ByteArray): Result<ByteArray> {
        return try {
            val (_, sk) = nacl.Sign.keyPairFromSeed(mySecretKey.secretKey)
            val signature = nacl.Sign.signDetached(data, sk)
            Result.success(signature)
        } catch (err: Throwable) {
            Result.failure(err)
        }
    }

    /**
     * Verifies a Base64 encoded detached signature against a message.
     */
    fun verifyDetachedFromEncodedString(theirPublicKey: KodiumPublicKey, data: ByteArray, signatureBase64: String): Boolean {
        return try {
            verifyDetached(theirPublicKey, data, signatureBase64.decodeBase64WithChecksum())
        } catch (err: Throwable) {
            false
        }
    }

    /**
     * Verifies a raw detached signature against a message.
     */
    fun verifyDetached(theirPublicKey: KodiumPublicKey, data: ByteArray, signature: ByteArray): Boolean {
        return verifyDetached(theirPublicKey.signingKey, data, signature)
    }

    /**
     * Verifies a raw detached signature against a message using a raw public key.
     */
    fun verifyDetached(theirSigningPublicKey: ByteArray, data: ByteArray, signature: ByteArray): Boolean {
        return try {
            nacl.Sign.verifyDetached(signature, data, theirSigningPublicKey)
        } catch (err: Throwable) {
            false
        }
    }

    /**
     * Generates a cryptographic key pair consisting of a public and private key.
     *
     * @return A KodiumPrivateKey instance containing the generated public and private keys.
     */
    fun generateKeyPair(): KodiumPrivateKey {
        return KodiumPrivateKey.generate()
    }

    /**
     * Encrypts the given data using the sender's private key and the receiver's public key,
     * and then encodes the encrypted data to a Base64 string with a checksum.
     *
     * **WARNING: Lack of Forward Secrecy.**
     * This method uses a static-static Diffie-Hellman key exchange. If either the sender's or
     * receiver's long-term key is compromised, all past messages encrypted between them can be
     * decrypted. For continuous, secure communication with forward secrecy and break-in recovery,
     * use [io.kodium.ratchet.DoubleRatchetSession] instead.
     *
     * This method performs encryption by generating a combined nonce and cipher
     * from the data and keys provided, ensuring secure communication.
     * The output is a Base64-encoded string for easy transmission and integrity verification.
     *
     * @param mySecretKey The private key of the sender used for encryption.
     * @param theirPublicKey The public key of the receiver used for encryption.
     * @param data The data to be encrypted, provided as a byte array.
     * @return A `Result` containing the encrypted and encoded string on success, or an error on failure.
     */
    fun encryptToEncodedString(
        mySecretKey: KodiumPrivateKey,
        theirPublicKey: KodiumPublicKey,
        data: ByteArray
    ): Result<String> {
        return encrypt(mySecretKey, theirPublicKey, data).mapCatching { it.encodeToBase64WithChecksum() }
    }

    /**
     * Encrypts data using the sender's private key and the receiver's public key.
     * The encryption utilizes a secure combination of key pairs, a randomly generated nonce,
     * and the NaCl library for cryptographic operations.
     *
     * **WARNING: Lack of Forward Secrecy.**
     * This method uses a static-static Diffie-Hellman key exchange. If either the sender's or
     * receiver's long-term key is compromised, all past messages encrypted between them can be
     * decrypted. For continuous, secure communication with forward secrecy and break-in recovery,
     * use [io.kodium.ratchet.DoubleRatchetSession] instead.
     *
     * @param mySecretKey The private key of the sender, used to compute the shared secret for encryption.
     * @param theirPublicKey The public key of the receiver, used to compute the shared secret for encryption.
     * @param data The plaintext data to be encrypted, provided as a byte array.
     * @return A `Result` containing the encrypted data as a byte array on success, or an error on failure.
     */
    fun encrypt(mySecretKey: KodiumPrivateKey, theirPublicKey: KodiumPublicKey, data: ByteArray): Result<ByteArray> {
        return try {
            val nonce = nacl.randomBytes(nacl.Box.NonceSize)
            val cipher = nacl.Box.seal(
                message = data,
                nonce = nonce,
                theirPublicKey = theirPublicKey.encryptionKey,
                mySecretKey = mySecretKey.secretKey
            )

            if (cipher.isNotEmpty()) {
                Result.success(
                    (nonce + cipher)
                )
            } else {
                Result.failure(Error("Failed to encrypt data, check your keys and try again"))
            }
        } catch (err: Throwable) {
            Result.failure(err)
        }
    }

    /**
     * Decrypts an encoded string using the recipient's private key and the sender's public key.
     *
     * This method takes a Base64-encoded string (with checksum) representing encrypted data,
     * decodes it to a byte array, and decrypts it using a combination of the receiver's private key
     * and the sender's public key.
     *
     * @param mySecretKey The private key of the recipient used for decryption.
     * @param theirPublicKey The public key of the sender used for decryption.
     * @param data The Base64-encoded string containing the encrypted data with a checksum.
     * @return A `Result` object containing the decrypted data as a `ByteArray` on success,
     *         or an error on failure.
     */
    fun decryptFromEncodedString(
        mySecretKey: KodiumPrivateKey,
        theirPublicKey: KodiumPublicKey,
        data: String
    ): Result<ByteArray> {
        return decrypt(mySecretKey, theirPublicKey, data.decodeBase64WithChecksum())
    }

    /**
     * Decrypts a cipher text using the receiver's private key and the sender's public key.
     *
     * This function extracts the nonce and encrypted message from the provided cipher text,
     * then attempts to decrypt the message using the provided keys. If the decryption fails,
     * an error message will be returned.
     *
     * @param mySecretKey The private key of the recipient used for decryption.
     * @param theirPublicKey The public key of the sender used for decryption.
     * @param cipher A byte array containing the cipher text to be decrypted. This includes the nonce and encrypted message.
     * @return A `Result` object containing the decrypted message as a `ByteArray` on success,
     *         or an error message on failure.
     */
    fun decrypt(mySecretKey: KodiumPrivateKey, theirPublicKey: KodiumPublicKey, cipher: ByteArray): Result<ByteArray> {
        val minCipherSize = nacl.Box.NonceSize + nacl.Box.MacSize

        if (cipher.size < minCipherSize) {
            return Result.failure(Error("Failed to decrypt data, invalid cipher size ${cipher.size} (must be at least $minCipherSize bytes)"))
        }

        val nonce = cipher.copyOfRange(0, nacl.Box.NonceSize)
        val msg = cipher.copyOfRange(nacl.Box.NonceSize, cipher.size)

        val res = nacl.Box.open(
            nonce = nonce,
            theirPublicKey = theirPublicKey.encryptionKey,
            mySecretKey = mySecretKey.secretKey,
            box = msg
        )

        return if (res == null || res.isEmpty()) {
            Result.failure(Error("Failed to decrypt data, check your keys and try again"))
        } else {
            Result.success(res)
        }
    }

    /**
     * Encrypts the given data using a symmetric encryption algorithm and encodes the result as a Base64 string with a checksum.
     *
     * The encryption process combines the given password and data, producing a secure encrypted output.
     * The resulting encrypted byte array is then encoded into a Base64-encoded string with a checksum for safe storage or transmission.
     *
     * @param password The password used for encryption. It should be a secure string that the user must remember to decrypt the data.
     * @param data The data to be encrypted, provided as a byte array.
     * @return A `Result` object that contains the Base64-encoded string on successful encryption, or an error if the encryption fails.
     */
    fun encryptSymmetricToEncodedString(password: String, data: ByteArray, keyDerivationIterations: Int = PBKDF2_ITERATIONS): Result<String> {
        return encryptSymmetric(password, data, keyDerivationIterations).mapCatching { it.encodeToBase64WithChecksum() }
    }

    /**
     * Encrypts the given data using a symmetric encryption algorithm with a precomputed key and encodes the result as a Base64 string with a checksum.
     *
     * @param key The precomputed symmetric key (must be exactly 32 bytes).
     * @param data The data to be encrypted, provided as a byte array.
     * @return A `Result` object that contains the Base64-encoded string on successful encryption, or an error if the encryption fails.
     */
    fun encryptSymmetricToEncodedString(key: ByteArray, data: ByteArray): Result<String> {
        return encryptSymmetric(key, data).mapCatching { it.encodeToBase64WithChecksum() }
    }

    /**
     * Encrypts the given data using a symmetric encryption algorithm.
     *
     * **Post-Quantum Security:** Symmetric encryption using 256-bit keys and XSalsa20
     * (as implemented here) is considered resistant to attacks by future quantum computers.
     * Unlike asymmetric algorithms, Grover's algorithm only provides a square-root speedup
     * for symmetric key searches, effectively meaning a 256-bit key remains as secure
     * against a quantum computer as a 128-bit key is against a classical computer today.
     *
     * This method generates a unique nonce for every encryption process,
     * derives a cryptographic key using HMAC-SHA256, and encrypts the data
     * with the derived key and the nonce using a secret box encryption mechanism.
     *
     * @param password The password used to derive the cryptographic key. It should be a secure string.
     * @param data The data to be encrypted, provided as a byte array.
     * @return A `Result` object containing the encrypted byte array on success (nonce and encrypted message concatenated),
     *         or an error in case the encryption process fails.
     */
    fun encryptSymmetric(password: String, data: ByteArray, keyDerivationIterations: Int = PBKDF2_ITERATIONS): Result<ByteArray> {
        return try {
            // 1. Check the min requirements for key derivation iterations
            if (keyDerivationIterations < 1) {
                return Result.failure(Error("The key derivation iterations must be at least 1, but you provided $keyDerivationIterations"))
            }

            // 2. Generate a new, random salt for every encryption
            val salt = nacl.randomBytes(SALT_SIZE_BYTES)

            // 3. Derive the encryption key using PBKDF2
            val key = KDF.deriveKey(
                password = password.encodeToByteArray(),
                salt = salt,
                iterations = keyDerivationIterations,
                keyLengthBytes = nacl.SecretBox.KeySize
            )

            // 4. Generate a nonce for the encryption itself
            val nonce = nacl.randomBytes(nacl.SecretBox.NonceSize)

            // 5. Encrypt the data
            val box = nacl.SecretBox.seal(
                message = data,
                nonce = nonce,
                key = key
            )

            // 6. Return the combined output: [salt][nonce][box]
            Result.success(salt + nonce + box)
        } catch (err: Throwable) {
            Result.failure(err)
        }
    }

    /**
     * Encrypts the given data using a symmetric encryption algorithm with a precomputed key.
     *
     * This method generates a unique nonce for every encryption process
     * and encrypts the data with the provided key and the nonce using a secret box encryption mechanism.
     *
     * @param key The precomputed symmetric key (must be exactly 32 bytes).
     * @param data The data to be encrypted, provided as a byte array.
     * @return A `Result` object containing the encrypted byte array on success (nonce and encrypted message concatenated),
     *         or an error in case the encryption process fails.
     */
    fun encryptSymmetric(key: ByteArray, data: ByteArray): Result<ByteArray> {
        return try {
            if (key.size != nacl.SecretBox.KeySize) {
                return Result.failure(Error("Invalid key size. Must be ${nacl.SecretBox.KeySize} bytes."))
            }

            // Generate a nonce for the encryption itself
            val nonce = nacl.randomBytes(nacl.SecretBox.NonceSize)

            // Encrypt the data
            val box = nacl.SecretBox.seal(
                message = data,
                nonce = nonce,
                key = key
            )

            // Return the combined output: [nonce][box]
            Result.success(nonce + box)
        } catch (err: Throwable) {
            Result.failure(err)
        }
    }

    /**
     * Decrypts an encoded Base64 string that was encrypted using symmetric encryption.
     *
     * This function first decodes the input string from Base64 with checksum verification. Then,
     * it attempts to decrypt the decoded data using the provided password and the symmetric
     * decryption method. The decryption result is returned as a success or an error.
     *
     * @param password The password used to decrypt the encoded string.
     * @param data The Base64-encoded string containing the encrypted data with a checksum.
     * @return A `Result` object containing the decrypted data as a `ByteArray` on success,
     *         or an error on failure.
     */
    fun decryptSymmetricFromEncodedString(password: String, data: String, keyDerivationIterations: Int = PBKDF2_ITERATIONS): Result<ByteArray> {
        return decryptSymmetric(password, data.decodeBase64WithChecksum(), keyDerivationIterations)
    }

    /**
     * Decrypts an encoded Base64 string that was encrypted using symmetric encryption with a precomputed key.
     *
     * @param key The precomputed symmetric key (must be exactly 32 bytes).
     * @param data The Base64-encoded string containing the encrypted data with a checksum.
     * @return A `Result` object containing the decrypted data as a `ByteArray` on success,
     *         or an error on failure.
     */
    fun decryptSymmetricFromEncodedString(key: ByteArray, data: String): Result<ByteArray> {
        return decryptSymmetric(key, data.decodeBase64WithChecksum())
    }

    /**
     * Decrypts a byte array that was encrypted using symmetric encryption with a provided password.
     *
     * This function extracts the nonce from the beginning of the cipher, derives a secret key using
     * HMAC-SHA256 with the password and nonce, and then attempts to decrypt the remaining data.
     *
     * If the decryption is successful, the decrypted data is returned as a `Result.success`. If it fails
     * (e.g., due to an incorrect password or tampered cipher data), a `Result.failure` is returned.
     *
     * @param password The password used to derive the secret key for decryption.
     * @param cipher The encrypted data, which consists of the nonce followed by the encrypted message.
     * @return A `Result` object containing the decrypted byte array on success, or an error on failure.
     */
    fun decryptSymmetric(password: String, cipher: ByteArray, keyDerivationIterations: Int = PBKDF2_ITERATIONS): Result<ByteArray> {
        return try {
            // 1. Check the min requirements for key derivation iterations
            if (keyDerivationIterations < 1) {
                return Result.failure(Error("The key derivation iterations must be at least 1, but you provided $keyDerivationIterations"))
            }

            // 2. Check if the cipher is large enough to contain salt, nonce, and MAC
            val minSize = SALT_SIZE_BYTES + nacl.SecretBox.NonceSize + nacl.SecretBox.MacSize
            if (cipher.size < minSize) {
                return Result.failure(Error("Invalid cipher size"))
            }

            // 3. Deconstruct the combined cipher: [salt][nonce][box]
            val salt = cipher.copyOfRange(0, SALT_SIZE_BYTES)
            val nonce = cipher.copyOfRange(SALT_SIZE_BYTES, SALT_SIZE_BYTES + nacl.SecretBox.NonceSize)
            val box = cipher.copyOfRange(SALT_SIZE_BYTES + nacl.SecretBox.NonceSize, cipher.size)

            // 4. Re-derive the key using the extracted salt
            val key = KDF.deriveKey(
                password = password.encodeToByteArray(),
                salt = salt,
                iterations = keyDerivationIterations,
                keyLengthBytes = nacl.SecretBox.KeySize
            )

            // 5. Decrypt the box
            val res = nacl.SecretBox.open(
                box = box,
                nonce = nonce,
                key = key
            )

            if (res != null) { // Note: isNotEmpty check is redundant, open returns non-empty or null
                Result.success(res)
            } else {
                Result.failure(Error("Failed to decrypt data, check your password or cipher and try again"))
            }
        } catch (err: Throwable) {
            Result.failure(err)
        }
    }

    /**
     * Decrypts a byte array that was encrypted using symmetric encryption with a precomputed key.
     *
     * @param key The precomputed symmetric key (must be exactly 32 bytes).
     * @param cipher The encrypted data, which consists of the nonce followed by the encrypted message.
     * @return A `Result` object containing the decrypted byte array on success, or an error on failure.
     */
    fun decryptSymmetric(key: ByteArray, cipher: ByteArray): Result<ByteArray> {
        return try {
            if (key.size != nacl.SecretBox.KeySize) {
                return Result.failure(Error("Invalid key size. Must be ${nacl.SecretBox.KeySize} bytes."))
            }

            val minSize = nacl.SecretBox.NonceSize + nacl.SecretBox.MacSize
            if (cipher.size < minSize) {
                return Result.failure(Error("Invalid cipher size"))
            }

            // Deconstruct the combined cipher: [nonce][box]
            val nonce = cipher.copyOfRange(0, nacl.SecretBox.NonceSize)
            val box = cipher.copyOfRange(nacl.SecretBox.NonceSize, cipher.size)

            // Decrypt the box
            val res = nacl.SecretBox.open(
                box = box,
                nonce = nonce,
                key = key
            )

            if (res != null) {
                Result.success(res)
            } else {
                Result.failure(Error("Failed to decrypt data, check your key or cipher and try again"))
            }
        } catch (err: Throwable) {
            Result.failure(err)
        }
    }

    /**
     * Encodes the given byte array into a Base64 string, appending a checksum at the end.
     *
     * @param data The byte array to be encoded.
     * @return A [Result] object containing the encoded Base64 string if successful, or the encountered error otherwise.
     */
    fun encodeArrayToString(data: ByteArray): Result<String> {
        return try {
            Result.success(data.encodeToBase64WithChecksum())
        } catch (err: Throwable) {
            Result.failure(err)
        }
    }

    /**
     * Decodes a base64 encoded string with checksum into a byte array.
     *
     * @param data The base64 encoded string with checksum to be decoded.
     * @return A [Result] containing the decoded byte array if successful, or the error if decoding fails.
     */
    fun decodeArrayFromString(data: String): Result<ByteArray> {
        return try {
            Result.success(data.decodeBase64WithChecksum())
        } catch (err: Throwable) {
            Result.failure(err)
        }
    }
}

/**
 * Represents a public key used in cryptographic operations within the KMP CryptoKit.
 * This class facilitates the handling of public keys, including importing from
 * and exporting to encoded formats with checksum validation.
 */
data class KodiumPublicKey(val encryptionKey: ByteArray, val signingKey: ByteArray) {

    companion object {

        /**
         * Imports a `KodiumPublicKey` from a Base64-encoded string with a checksum.
         *
         * This method decodes the input string using Base64 encoding with checksum validation and
         * attempts to construct a `KodiumPublicKey` instance from the decoded data. If the
         * input string is invalid or the checksum validation fails, the method returns a `Result`
         * containing the failure.
         *
         * @param data A Base64-encoded string with an appended checksum, representing the public key material.
         * @return A `Result` containing a successfully imported `KodiumPublicKey` or an error if the import fails.
         */
        fun importFromEncodedString(data: String): Result<KodiumPublicKey> {
            return try {
                val material = data.decodeBase64WithChecksum()
                val encryption = material.copyOfRange(0, nacl.Box.PublicKeySize)
                val signing = material.copyOfRange(nacl.Box.PublicKeySize, material.size)
                Result.success(KodiumPublicKey(encryption, signing))
            } catch (err: Throwable) {
                Result.failure(err)
            }
        }
    }

    /**
     * Exports the raw public key material to a Base64-encoded string with a checksum appended.
     *
     * This method encodes the byte array representing the public key (`material`) using Base64
     * encoding and appends a checksum to ensure data integrity. The checksum is calculated
     * using a double SHA-256 hash of the original byte array, with the first four bytes of the
     * hash serving as the checksum.
     *
     * The resulting string is suitable for secure storage or transmission and can subsequently
     * be imported back into a KodiumKodiumPublicKey object using `importFromEncodedString`.
     *
     * @return A Base64-encoded string representation of the public key material with a checksum attached.
     */
    fun exportToEncodedString() = (encryptionKey + signingKey).encodeToBase64WithChecksum()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KodiumPublicKey

        if (!encryptionKey.contentEquals(other.encryptionKey)) return false
        if (!signingKey.contentEquals(other.signingKey)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryptionKey.contentHashCode()
        result = 31 * result + signingKey.contentHashCode()
        return result
    }

}

class KodiumPrivateKey private constructor(
    val secretKey: ByteArray,
    val publicKeyInstance: KodiumPublicKey
) {

    companion object {
        /**
         * Generates a new, random cryptographic key pair.
         */
        fun generate(): KodiumPrivateKey {
            val keyPair = nacl.Box.keyPair()
            val (signPk, _) = nacl.Sign.keyPairFromSeed(keyPair.second)
            return KodiumPrivateKey(
                secretKey = keyPair.second,
                publicKeyInstance = KodiumPublicKey(keyPair.first, signPk)
            )
        }

        /**
         * Imports a private key from its raw 32-byte representation.
         * The corresponding public key will be derived.
         */
        fun fromRaw(secretKey: ByteArray): KodiumPrivateKey {
            require(secretKey.size == nacl.Box.SecretKeySize) { "Invalid raw secret key size" }
            val (encryptionPk, _) = nacl.Box.keyPairFromSecretKey(secretKey)
            val (signingPk, _) = nacl.Sign.keyPairFromSeed(secretKey)
            return KodiumPrivateKey(
                secretKey = secretKey,
                publicKeyInstance = KodiumPublicKey(encryptionPk, signingPk)
            )
        }

        /**
         * Imports a private key from an encrypted, Base64-encoded string.
         * A password MUST be provided.
         */
        fun importFromEncryptedString(data: String, password: String, keyDerivationIterations: Int = Kodium.PBKDF2_ITERATIONS): Result<KodiumPrivateKey> {
            return Kodium.decryptSymmetric(password, data.decodeBase64WithChecksum(), keyDerivationIterations)
                .mapCatching { rawSecretKey -> fromRaw(rawSecretKey) }
        }

        /**
         * Imports a private key from an encrypted, Base64-encoded string.
         * A precomputed symmetric key MUST be provided.
         */
        fun importFromEncryptedString(data: String, key: ByteArray): Result<KodiumPrivateKey> {
            return Kodium.decryptSymmetric(key, data.decodeBase64WithChecksum())
                .mapCatching { rawSecretKey -> fromRaw(rawSecretKey) }
        }

        /**
         * Imports a private key from a raw, unprotected ByteArray.
         */
        fun importFromArray(data: ByteArray): Result<KodiumPrivateKey> {
            return try {
                Result.success(fromRaw(data))
            } catch (err: Throwable) {
                Result.failure(err)
            }
        }
    }

    /**
     * Returns the public key component of this key pair.
     * Note: This now returns a unified public key containing both encryption and signing components.
     */
    fun getPublicKey(): KodiumPublicKey {
        return publicKeyInstance
    }

    /**
     * Exports the private key to an encrypted, Base64-encoded string.
     * A password MUST be provided.
     */
    fun exportToEncryptedString(password: String, keyDerivationIterations: Int = Kodium.PBKDF2_ITERATIONS): Result<String> {
        return Kodium.encryptSymmetric(password, this.secretKey, keyDerivationIterations)
            .mapCatching { it.encodeToBase64WithChecksum() }
    }

    /**
     * Exports the private key to an encrypted, Base64-encoded string.
     * A precomputed symmetric key MUST be provided.
     */
    fun exportToEncryptedString(key: ByteArray): Result<String> {
        return Kodium.encryptSymmetric(key, this.secretKey)
            .mapCatching { it.encodeToBase64WithChecksum() }
    }

    /**
     * Exports the raw private key material to an unprotected ByteArray.
     * This is useful when the application using Kodium manages its own encryption or key protection.
     */
    fun exportToArray(): ByteArray {
        return secretKey.copyOf()
    }

    // equals and hashCode should now consider both keys if available,
    // or just the secret key as it's the source of truth.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KodiumPrivateKey) return false
        return secretKey.contentEquals(other.secretKey)
    }

    override fun hashCode(): Int {
        return secretKey.contentHashCode()
    }
}

/**
 * Represents a Hybrid Post-Quantum Public Key.
 */
data class KodiumPqcPublicKey(
    val classicalPublicKey: ByteArray,
    val classicalSignPublicKey: ByteArray,
    val pqcPublicKey: ByteArray
) {
    companion object {
        fun importFromEncodedString(data: String): Result<KodiumPqcPublicKey> {
            return try {
                val material = data.decodeBase64WithChecksum()
                val classical = material.copyOfRange(0, nacl.Box.PublicKeySize)
                val classicalSign = material.copyOfRange(nacl.Box.PublicKeySize, nacl.Box.PublicKeySize + nacl.Sign.PublicKeySize)
                val pqc = material.copyOfRange(nacl.Box.PublicKeySize + nacl.Sign.PublicKeySize, material.size)
                Result.success(KodiumPqcPublicKey(classical, classicalSign, pqc))
            } catch (err: Throwable) {
                Result.failure(err)
            }
        }
    }

    fun exportToEncodedString(): String = (classicalPublicKey + classicalSignPublicKey + pqcPublicKey).encodeToBase64WithChecksum()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KodiumPqcPublicKey) return false
        return classicalPublicKey.contentEquals(other.classicalPublicKey) &&
                classicalSignPublicKey.contentEquals(other.classicalSignPublicKey) &&
                pqcPublicKey.contentEquals(other.pqcPublicKey)
    }

    override fun hashCode(): Int {
        var result = classicalPublicKey.contentHashCode()
        result = 31 * result + classicalSignPublicKey.contentHashCode()
        result = 31 * result + pqcPublicKey.contentHashCode()
        return result
    }
}

/**
 * Represents a Hybrid Post-Quantum Private Key.
 */
class KodiumPqcPrivateKey private constructor(
    val classicalSecretKey: ByteArray,
    val pqcSecretKey: ByteArray,
    private val publicKeyInstance: KodiumPqcPublicKey
) {
    companion object {
        fun generate(): KodiumPqcPrivateKey {
            val (classicalPk, classicalSk) = nacl.Box.keyPair()
            val (pqcPk, pqcSk) = MLKEM.keyPair()
            val (classicalSignPk, _) = nacl.Sign.keyPairFromSeed(classicalSk)
            
            return KodiumPqcPrivateKey(
                classicalSk,
                pqcSk,
                KodiumPqcPublicKey(classicalPk, classicalSignPk, pqcPk)
            )
        }

        /**
         * Imports a PQC private key from an encrypted, Base64-encoded string.
         * A password MUST be provided.
         */
        fun importFromEncryptedString(data: String, password: String, keyDerivationIterations: Int = Kodium.PBKDF2_ITERATIONS): Result<KodiumPqcPrivateKey> {
            return Kodium.decryptSymmetric(password, data.decodeBase64WithChecksum(), keyDerivationIterations).mapCatching { material ->
                val classicalSk = material.copyOfRange(0, nacl.Box.SecretKeySize)
                val pqcSk = material.copyOfRange(nacl.Box.SecretKeySize, material.size)

                fromRaw(classicalSk, pqcSk)
            }
        }

        /**
         * Imports a PQC private key from an encrypted, Base64-encoded string.
         * A precomputed symmetric key MUST be provided.
         */
        fun importFromEncryptedString(data: String, key: ByteArray): Result<KodiumPqcPrivateKey> {
            return Kodium.decryptSymmetric(key, data.decodeBase64WithChecksum()).mapCatching { material ->
                val classicalSk = material.copyOfRange(0, nacl.Box.SecretKeySize)
                val pqcSk = material.copyOfRange(nacl.Box.SecretKeySize, material.size)

                fromRaw(classicalSk, pqcSk)
            }
        }

        /**
         * Imports a PQC private key from a raw, unprotected ByteArray.
         */
        fun importFromArray(data: ByteArray): Result<KodiumPqcPrivateKey> {
            return try {
                val classicalSk = data.copyOfRange(0, nacl.Box.SecretKeySize)
                val pqcSk = data.copyOfRange(nacl.Box.SecretKeySize, data.size)
                Result.success(fromRaw(classicalSk, pqcSk))
            } catch (err: Throwable) {
                Result.failure(err)
            }
        }

        internal fun fromRaw(classicalSk: ByteArray, pqcSk: ByteArray): KodiumPqcPrivateKey {
            val (classicalPk, _) = nacl.Box.keyPairFromSecretKey(classicalSk)
            val (classicalSignPk, _) = nacl.Sign.keyPairFromSeed(classicalSk)
            val pqcPk = MLKEM.getPublicKeyFromSecretKey(pqcSk)
            return KodiumPqcPrivateKey(classicalSk, pqcSk, KodiumPqcPublicKey(classicalPk, classicalSignPk, pqcPk))
        }

    }

    fun getPublicKey(): KodiumPqcPublicKey = publicKeyInstance

    /**
     * Exports the PQC private key to an encrypted, Base64-encoded string.
     * A password MUST be provided.
     */
    fun exportToEncryptedString(password: String, keyDerivationIterations: Int = Kodium.PBKDF2_ITERATIONS): Result<String> {
        return Kodium.encryptSymmetric(password, classicalSecretKey + pqcSecretKey, keyDerivationIterations)
            .mapCatching { it.encodeToBase64WithChecksum() }
    }

    /**
     * Exports the PQC private key to an encrypted, Base64-encoded string.
     * A precomputed symmetric key MUST be provided.
     */
    fun exportToEncryptedString(key: ByteArray): Result<String> {
        return Kodium.encryptSymmetric(key, classicalSecretKey + pqcSecretKey)
            .mapCatching { it.encodeToBase64WithChecksum() }
    }

    /**
     * Exports the raw PQC private key material to an unprotected ByteArray.
     * This is useful when the application using Kodium manages its own encryption or key protection.
     */
    fun exportToArray(): ByteArray {
        return classicalSecretKey + pqcSecretKey
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KodiumPqcPrivateKey) return false
        return classicalSecretKey.contentEquals(other.classicalSecretKey) &&
                pqcSecretKey.contentEquals(other.pqcSecretKey)
    }

    override fun hashCode(): Int {
        var result = classicalSecretKey.contentHashCode()
        result = 31 * result + pqcSecretKey.contentHashCode()
        return result
    }
}
