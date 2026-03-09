//[kodium](../../index.md)/[io.kodium.ratchet](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [DoubleRatchetSession](-double-ratchet-session/index.md) | [common]<br>class [DoubleRatchetSession](-double-ratchet-session/index.md)<br>The core implementation of the Double Ratchet Algorithm. |
| [HKDF](-h-k-d-f/index.md) | [common]<br>object [HKDF](-h-k-d-f/index.md)<br>An implementation of the HMAC-based Extract-and-Expand Key Derivation Function (HKDF) using HMAC-SHA256 as the underlying pseudorandom function (PRF). |
| [PQDoubleRatchetSession](-p-q-double-ratchet-session/index.md) | [common]<br>class [PQDoubleRatchetSession](-p-q-double-ratchet-session/index.md)<br>The core implementation of the Post-Quantum Double Ratchet Algorithm. |
| [PQRatchetHeader](-p-q-ratchet-header/index.md) | [common]<br>data class [PQRatchetHeader](-p-q-ratchet-header/index.md)(val dh: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val kemCiphertext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val pn: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html))<br>Represents the plaintext header attached to every message sent within a Post-Quantum Double Ratchet session. |
| [PQRatchetMessage](-p-q-ratchet-message/index.md) | [common]<br>data class [PQRatchetMessage](-p-q-ratchet-message/index.md)(val header: [PQRatchetHeader](-p-q-ratchet-header/index.md), val ciphertext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html))<br>A composite structure containing both the plaintext PQC Ratchet header and the encrypted payload. |
| [PQXDH](-p-q-x-d-h/index.md) | [common]<br>object [PQXDH](-p-q-x-d-h/index.md)<br>PQXDH (Post-Quantum Extended Triple Diffie-Hellman) Key Agreement Protocol. |
| [RatchetHeader](-ratchet-header/index.md) | [common]<br>data class [RatchetHeader](-ratchet-header/index.md)(val dh: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html), val pn: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html))<br>Represents the plaintext header attached to every message sent within a Double Ratchet session. |
| [RatchetMessage](-ratchet-message/index.md) | [common]<br>data class [RatchetMessage](-ratchet-message/index.md)(val header: [RatchetHeader](-ratchet-header/index.md), val ciphertext: [ByteArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-byte-array/index.html))<br>A composite structure containing both the plaintext Ratchet header and the encrypted payload. |
| [X3DH](-x3-d-h/index.md) | [common]<br>object [X3DH](-x3-d-h/index.md)<br>X3DH (Extended Triple Diffie-Hellman) Key Agreement Protocol. |
