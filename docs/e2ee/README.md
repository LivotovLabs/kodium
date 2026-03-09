# End-to-End Encryption (E2EE)

Kodium provides a complete, pure-Kotlin implementation of the Double Ratchet Algorithm along with the X3DH (Extended Triple Diffie-Hellman) key agreement protocol.

This combination allows you to build secure, end-to-end encrypted (E2EE) peer-to-peer applications, such as secure chat, where even the server forwarding the messages cannot read them.

## Key Properties

*   **Forward Secrecy:** If a user's current keys are compromised, past messages remain secure and cannot be decrypted.
*   **Break-in Recovery (Future Secrecy):** If a user's current keys are compromised, future messages will eventually become secure again as the conversation continues and new keys are ratcheted.
*   **Asynchronous:** The X3DH protocol allows parties to establish a shared secret even if the other party is currently offline.
