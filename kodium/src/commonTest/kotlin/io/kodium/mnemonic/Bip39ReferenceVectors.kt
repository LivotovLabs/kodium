package io.kodium.mnemonic

/**
 * The English (entropy, mnemonic) pairs of the BIP-39 reference implementation's test vectors,
 * https://github.com/trezor/python-mnemonic/blob/master/vectors.json. The seed and xprv columns are
 * omitted: they need the mnemonic-to-seed step, which Kodium deliberately does not provide.
 */
internal object Bip39ReferenceVectors {
    val english = listOf(
        "00000000000000000000000000000000" to "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about",
        "7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f" to "legal winner thank year wave sausage worth useful legal winner thank yellow",
        "80808080808080808080808080808080" to "letter advice cage absurd amount doctor acoustic avoid letter advice cage above",
        "ffffffffffffffffffffffffffffffff" to "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo wrong",
        "000000000000000000000000000000000000000000000000" to "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon agent",
        "7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f" to "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will",
        "808080808080808080808080808080808080808080808080" to "letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd amount doctor acoustic avoid letter always",
        "ffffffffffffffffffffffffffffffffffffffffffffffff" to "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo when",
        "0000000000000000000000000000000000000000000000000000000000000000" to "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon art",
        "7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f" to "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth title",
        "8080808080808080808080808080808080808080808080808080808080808080" to "letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd amount doctor acoustic avoid letter advice cage absurd amount doctor acoustic bless",
        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" to "zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo zoo vote",
        "9e885d952ad362caeb4efe34a8e91bd2" to "ozone drill grab fiber curtain grace pudding thank cruise elder eight picnic",
        "6610b25967cdcca9d59875f5cb50b0ea75433311869e930b" to "gravity machine north sort system female filter attitude volume fold club stay feature office ecology stable narrow fog",
        "68a79eaca2324873eacc50cb9c6eca8cc68ea5d936f98787c60c7ebc74e6ce7c" to "hamster diagram private dutch cause delay private meat slide toddler razor book happy fancy gospel tennis maple dilemma loan word shrug inflict delay length",
        "c0ba5a8e914111210f2bd131f3d5e08d" to "scheme spot photo card baby mountain device kick cradle pact join borrow",
        "6d9be1ee6ebd27a258115aad99b7317b9c8d28b6d76431c3" to "horn tenant knee talent sponsor spell gate clip pulse soap slush warm silver nephew swap uncle crack brave",
        "9f6a2878b2520799a44ef18bc7df394e7061a224d2c33cd015b157d746869863" to "panda eyebrow bullet gorilla call smoke muffin taste mesh discover soft ostrich alcohol speed nation flash devote level hobby quick inner drive ghost inside",
        "23db8160a31d3e0dca3688ed941adbf3" to "cat swing flag economy stadium alone churn speed unique patch report train",
        "8197a4a47f0425faeaa69deebc05ca29c0a5b5cc76ceacc0" to "light rule cinnamon wrap drastic word pride squirrel upgrade then income fatal apart sustain crack supply proud access",
        "066dca1a2bb7e8a1db2832148ce9933eea0f3ac9548d793112d9a95c9407efad" to "all hour make first leader extend hole alien behind guard gospel lava path output census museum junior mass reopen famous sing advance salt reform",
        "f30f8c1da665478f49b001d94c5fc452" to "vessel ladder alter error federal sibling chat ability sun glass valve picture",
        "c10ec20dc3cd9f652c7fac2f1230f7a3c828389a14392f05" to "scissors invite lock maple supreme raw rapid void congress muscle digital elegant little brisk hair mango congress clump",
        "f585c11aec520db57dd353c69554b21a89b20fb0650966fa0a9d6f74fd989d8f" to "void come effort suffer camp survey warrior heavy shoot primary clutch crush open amazing screen patrol group space point ten exist slush involve unfold"
    )
}
