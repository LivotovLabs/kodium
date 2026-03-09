package io.kodium.core

import io.kodium.KodiumPqcPrivateKey
import io.kodium.KodiumPqcPublicKey
import io.kodium.KodiumPrivateKey
import io.kodium.core.fips203.KyberEncapsulationKey
import io.kodium.core.fips203.MLKEM_768
import io.kodium.core.fips203.RandomProvider
import io.kodium.core.nacl
import io.kodium.ratchet.HKDF
import io.kodium.ratchet.PQDoubleRatchetSession
import io.kodium.ratchet.RatchetUtils

fun MLKEM.keyPairForTesting(seed: ByteArray): Pair<ByteArray, ByteArray> {
    val generator = MLKEM_768(object : RandomProvider {
        var offset = 0
        override fun fillWithRandom(byteArray: ByteArray) {
            for (i in byteArray.indices) {
                byteArray[i] = seed[offset % seed.size]
                offset++
            }
        }
    })
    val kp = generator.generate()
    return Pair(kp.encapsulationKey.fullBytes, kp.decapsulationKey.fullBytes)
}

fun MLKEM.encapsulateForTesting(publicKey: ByteArray, seed: ByteArray): Pair<ByteArray, ByteArray> {
    require(publicKey.size == MLKEM.PublicKeySize) { "Invalid public key size" }
    val key = KyberEncapsulationKey.fromBytes(publicKey)
    val result = key.encapsulate(object : RandomProvider {
        var offset = 0
        override fun fillWithRandom(byteArray: ByteArray) {
            for (i in byteArray.indices) {
                byteArray[i] = seed[offset % seed.size]
                offset++
            }
        }
    })
    return Pair(result.sharedSecretKey, result.cipherText.fullBytes)
}

fun KodiumPqcPrivateKey.Companion.generateForTesting(seed: ByteArray): KodiumPqcPrivateKey {
    require(seed.size >= nacl.Box.SecretKeySize) { "Seed must be at least ${nacl.Box.SecretKeySize} bytes" }
    val classicalSk = seed.copyOf(nacl.Box.SecretKeySize)
    val (classicalPk, _) = nacl.Box.keyPairFromSecretKey(classicalSk)

    val (pqcPk, pqcSk) = MLKEM.keyPairForTesting(seed)

    return KodiumPqcPrivateKey.fromRaw(classicalSk, pqcSk)
}

fun PQDoubleRatchetSession.Companion.initializeAsInitiatorForTesting(
    sharedSecret: ByteArray,
    responderPqcPublicKey: KodiumPqcPublicKey,
    ourPqcPrivateKey: KodiumPqcPrivateKey,
    kemSeed: ByteArray,
    applicationInfo: ByteArray = "KodiumKdfRk".encodeToByteArray()
): PQDoubleRatchetSession {
    val dhs = KodiumPrivateKey.generate()
    
    val (sharedSecretPqc, kemCiphertext) = MLKEM.encapsulateForTesting(responderPqcPublicKey.pqcPublicKey, kemSeed)
    
    val dhOut = RatchetUtils.dh(dhs.secretKey, responderPqcPublicKey.classicalPublicKey)
    
    val kdfResult = HKDF.deriveSecrets(
        salt = sharedSecret,
        ikm = dhOut + sharedSecretPqc,
        info = applicationInfo,
        length = 64
    )

    val rk = kdfResult.sliceArray(0 until 32)
    val cks = kdfResult.sliceArray(32 until 64)

    return PQDoubleRatchetSession(
        ourPqcKey = ourPqcPrivateKey,
        theirPqcPublicKey = responderPqcPublicKey,
        DHs = dhs,
        CTs = kemCiphertext,
        DHr = responderPqcPublicKey.classicalPublicKey,
        RK = rk,
        CKs = cks,
        CKr = null,
        applicationInfo = applicationInfo
    )
}
