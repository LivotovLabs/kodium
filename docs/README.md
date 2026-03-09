# Kodium

**Kodium** is a comprehensive, pure Kotlin Multiplatform (KMP) cryptography library. It acts as a faithful port of the renowned **TweetNaCl** C library, providing high-speed, high-security cryptographic primitives, advanced **Double Ratchet** session management, and **Post-Quantum Cryptography (PQC)** protocols without *any* native dependencies.

Write once, encrypt everywhere. Even in a post-quantum world.

## Kodium Highlights

*   **Pure Kotlin:** No JNI, no C interop headaches, no complex build scripts for native binaries. Just pure Kotlin code running everywhere.
*   **TweetNaCl Core:** Built on the solid foundation of the TweetNaCl crypto suite, known for its security and simplicity.
*   **Post-Quantum Ready:** Hybrid cryptographic primitives combining classical X25519 with FIPS 203 (ML-KEM) to protect against future quantum computers. Note that symmetric encryption (SecretBox) is naturally quantum-resistant.
*   **Double Ratchet:** Includes a full implementation of the Double Ratchet Algorithm and X3DH for secure End-to-End Encryption (E2EE), featuring a built-in LRU policy for skipped message keys to prevent memory exhaustion.
*   **Multiplatform Native:** First-class support for Android, iOS, JVM, JavaScript (Browser/Node), Wasm, Linux, macOS, and Windows.
*   **Developer Friendly:** Simple, opinionated APIs for common tasks (Box, SecretBox, Signatures).

## Supported Platforms

| Platform | Support |
| :--- | :---: |
| **Android** | ✅ |
| **iOS** (Arm64, X64, Sim) | ✅ |
| **JVM** (Java 17+) | ✅ |
| **JavaScript** (Browser/Node) | ✅ |
| **Wasm** (WebAssembly) | ✅ |
| **macOS** (Arm64, X64) | ✅ |
| **Linux** (X64) | ✅ |
| **Windows** (MinGW X64) | ✅ |

## Features

*   **End-to-End Encryption:** Double Ratchet Algorithm & X3DH (Extended Triple Diffie-Hellman).
*   **Post-Quantum Hybrid Encryption:** FIPS 203 (ML-KEM-768) + Curve25519 authenticated encryption.
*   **Public-Key Cryptography (Box):** Authenticated encryption using Curve25519, XSalsa20, and Poly1305.
*   **Secret-Key Cryptography (SecretBox):** Authenticated encryption using XSalsa20 and Poly1305.
*   **Digital Signatures:** Ed25519 high-speed, high-security signatures.
*   **Key Management:** Secure generation, import, and export of keys (Raw & Base58Check).
*   **Utils:** Robust Base58Check encoding/decoding and HKDF (RFC 5869).
