# Seeded Keys & Mnemonics

Normally Kodium generates keys from the platform's secure random generator. Sometimes an application needs
a key it can **re-create** instead: the classic case is a recovery or "paper" key, whose 32 bytes of entropy
the user writes down so that the key can be derived again on a new device after every other device is lost.

Two building blocks make this possible:

*   **Seeded key derivation** turns 32 bytes into a key pair deterministically.
*   **BIP-39 mnemonics** turn those 32 bytes into 24 words a person can write down and type back, with a checksum
    that catches mistakes.

## Seeded Key Derivation

```kotlin
val seed: ByteArray = Kodium.generateHighEntropyKey()   // 32 bytes, keep it secret

val hybridKey: KodiumPqcPrivateKey = Kodium.pqc.generateKeyPair(seed)
val classicalKey: KodiumPrivateKey = Kodium.generateKeyPair(seed)
```

The same seed always produces a byte-identical key: the same `exportToArray()` and the same
`getPublicKey().exportToEncodedString()`, on every platform and in every Kodium release from 1.1.0 on. A seeded
key is an ordinary key otherwise, and works with encryption, signatures, PQXDH and the Double Ratchet.

The seed must be exactly 32 bytes; anything else throws `IllegalArgumentException`. There is deliberately no
overload for shorter seeds or passwords.

### Derivation (version 1)

| Step | Hybrid key (`Kodium.pqc.generateKeyPair(seed)`) | Classical key (`Kodium.generateKeyPair(seed)`) |
| :--- | :--- | :--- |
| Extract | `prk = HKDF-Extract-SHA-256(salt = "kodium-seeded-hybrid-v1", ikm = seed)` | `prk = HKDF-Extract-SHA-256(salt = "kodium-seeded-ed25519-v1", ikm = seed)` |
| Classical secret | `HKDF-Expand(prk, "ed25519-seed", 32)`, used as the X25519 secret key and the Ed25519 seed | same |
| ML-KEM-768 | `d = HKDF-Expand(prk, "mlkem-d", 32)`, `z = HKDF-Expand(prk, "mlkem-z", 32)`, then FIPS 203 `ML-KEM.KeyGen_internal(d, z)` | — |

The salts are the version. Keys that users have written down depend on version 1, so it never changes: a
future derivation would use a new salt and a new function. The golden vectors in
`kodium/src/commonTest/resources/seeded-hybrid-vectors.json` pin version 1 on every platform in the test
suite. They were computed with independent implementations, so any HKDF-SHA-256, X25519, Ed25519 and FIPS 203
ML-KEM library can reproduce them.

The ML-KEM half of a seeded key is always `MlKemVariant.FIPS_203`, so peers still running Kodium 1.0.0 cannot
encrypt to it (they can verify its signatures). See
[ML-KEM Variants](../pqc/post-quantum-cryptography.md#3-ml-kem-variants-and-kodium-100-compatibility).

## BIP-39 Mnemonics

```kotlin
import io.kodium.mnemonic.Bip39

val words: List<String> = Bip39.encode(seed)                 // 32 bytes -> 24 words

val decoded: ByteArray? = Bip39.decode(words)                  // or Bip39.decode("word1 word2 ...")
if (decoded == null) {
    // unknown word, wrong number of words, or checksum mismatch
}

Bip39.isWord("abandon")      // true: validate each word while typing
Bip39.complete("ab")         // [abandon, ability, able, ...]: autocomplete
```

*   Entropy of 16, 20, 24, 28 or 32 bytes encodes to 12, 15, 18, 21 or 24 words from the official English list
    (`Bip39.english`).
*   Input is normalised before lookup. It is decomposed with Unicode NFKD, lower-cased and trimmed, and phrases
    are split on any whitespace. `"ABANDON"`, `" abandon"` and a fullwidth `"ａｂａｎｄｏｎ"` all read as
    `abandon`, identically on every platform.
*   Only exact words are accepted. A prefix such as `"aban"` is a completion, never a word.
*   There is deliberately **no** mnemonic-to-seed step (the BIP-39 PBKDF2 derivation with a passphrase). Keys are
    derived from the entropy that `decode` returns, as shown above.

## Handling the Secret

The seed, the entropy and the words are as sensitive as the private key. Keep them out of logs and strings you
keep around, and zero byte arrays when you no longer need them:

```kotlin
val entropy = Bip39.decode(typedWords) ?: return showChecksumError()
val key = Kodium.pqc.generateKeyPair(entropy)
entropy.fill(0)
```
