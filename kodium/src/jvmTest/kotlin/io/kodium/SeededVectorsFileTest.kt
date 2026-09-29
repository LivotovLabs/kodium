package io.kodium

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * `seeded-hybrid-vectors.json` is the contract; [SeededHybridVectors] mirrors it for the non-JVM targets.
 * This keeps the two identical, field by field.
 */
class SeededVectorsFileTest {
    private val file = File("src/commonTest/resources/seeded-hybrid-vectors.json")
    private val field = Regex("\"(\\w+)\": \"([^\"]*)\"")

    private fun objectsIn(array: String): List<Map<String, String>> {
        val text = file.readText()
        val body = text.substringAfter("\"$array\": [").substringBefore("]")
        return Regex("\\{[^{}]*\\}").findAll(body).map { o -> field.findAll(o.value).associate { it.groupValues[1] to it.groupValues[2] } }.toList()
    }

    @Test
    fun jsonFileMatchesTheKotlinMirror() {
        assertEquals(SeededHybridVectors.signatureMessage, field.findAll(file.readText()).first { it.groupValues[1] == "signatureMessage" }.groupValues[2])

        val hybrid = objectsIn("hybrid")
        assertEquals(SeededHybridVectors.hybrid.size, hybrid.size)
        for ((v, json) in SeededHybridVectors.hybrid.zip(hybrid)) {
            assertEquals(
                mapOf(
                    "seed" to v.seed, "mnemonic" to v.mnemonic, "classicalSecret" to v.classicalSecret, "mlkemD" to v.mlkemD,
                    "mlkemZ" to v.mlkemZ, "privateKey" to v.privateKey, "publicKey" to v.publicKey, "signature" to v.signature
                ),
                json
            )
        }

        val ed25519 = objectsIn("ed25519")
        assertEquals(SeededHybridVectors.ed25519.size, ed25519.size)
        for ((v, json) in SeededHybridVectors.ed25519.zip(ed25519)) {
            assertEquals(mapOf("seed" to v.seed, "secretKey" to v.secretKey, "publicKey" to v.publicKey, "signature" to v.signature), json)
        }
    }
}
