from typing import Dict, List, Tuple
from unittest import TestCase

from electrum_gui.common.secret import exceptions
from electrum_gui.common.secret.data import CurveEnum
from electrum_gui.common.secret.registry import bip32_class_on_curve


class TestBIP32(TestCase):
    @classmethod
    def setUpClass(cls):
        cls._master_seed = "000102030405060708090a0b0c0d0e0f"

    @staticmethod
    def vector1_from_slip0010() -> Dict[CurveEnum, dict]:
        return {
            CurveEnum.SECP256K1: {
                "m": {
                    "parent_fingerprint": "00000000",
                    "chain_code": "873dff81c02f525623fd1fe5167eac3a55a049de3d314bb42ee227ffed37d508",
                    "prvkey": "e8f32e723decf4051aefac8e2c93c9c5b214313817cdb01a1494b917c8436b35",
                    "pubkey": "0339a36013301597daef41fbe593a02cc513d0b55527ec2df1050e2e8ff49c85c2",
                },
                "m/0'": {
                    "parent_fingerprint": "3442193e",
                    "chain_code": "47fdacbd0f1097043b78c63c20c34ef4ed9a111d980047ad16282c7ae6236141",
                    "prvkey": "edb2e14f9ee77d26dd93b4ecede8d16ed408ce149b6cd80b0715a2d911a0afea",
                    "pubkey": "035a784662a4a20a65bf6aab9ae98a6c068a81c52e4b032c0fb5400c706cfccc56",
                },
                "m/0'/1": {
                    "parent_fingerprint": "5c1bd648",
                    "chain_code": "2a7857631386ba23dacac34180dd1983734e444fdbf774041578e9b6adb37c19",
                    "prvkey": "3c6cb8d0f6a264c91ea8b5030fadaa8e538b020f0a387421a12de9319dc93368",
                    "pubkey": "03501e454bf00751f24b1b489aa925215d66af2234e3891c3b21a52bedb3cd711c",
                },
                "m/0'/1/2'": {
                    "parent_fingerprint": "bef5a2f9",
                    "chain_code": "04466b9cc8e161e966409ca52986c584f07e9dc81f735db683c3ff6ec7b1503f",
                    "prvkey": "cbce0d719ecf7431d88e6a89fa1483e02e35092af60c042b1df2ff59fa424dca",
                    "pubkey": "0357bfe1e341d01c69fe5654309956cbea516822fba8a601743a012a7896ee8dc2",
                },
                "m/0'/1/2'/2": {
                    "parent_fingerprint": "ee7ab90c",
                    "chain_code": "cfb71883f01676f587d023cc53a35bc7f88f724b1f8c2892ac1275ac822a3edd",
                    "prvkey": "0f479245fb19a38a1954c5c7c0ebab2f9bdfd96a17563ef28a6a4b1a2a764ef4",
                    "pubkey": "02e8445082a72f29b75ca48748a914df60622a609cacfce8ed0e35804560741d29",
                },
                "m/0'/1/2'/2/1000000000": {
                    "parent_fingerprint": "d880d7d8",
                    "chain_code": "c783e67b921d2beb8f6b389cc646d7263b4145701dadd2161548a8b078e65e9e",
                    "prvkey": "471b76e389e528d6de6d816857e012c5455051cad6660850e58372a6c3e6e7c8",
                    "pubkey": "022a471424da5e657499d1ff51cb43c47481a03b1e77f951fe64cec9f5a48f7011",
                },
            },
            CurveEnum.SECP256R1: {
                "m": {
                    "parent_fingerprint": "00000000",
                    "chain_code": "beeb672fe4621673f722f38529c07392fecaa61015c80c34f29ce8b41b3cb6ea",
                    "prvkey": "612091aaa12e22dd2abef664f8a01a82cae99ad7441b7ef8110424915c268bc2",
                    "pubkey": "0266874dc6ade47b3ecd096745ca09bcd29638dd52c2c12117b11ed3e458cfa9e8",
                },
                "m/0'": {
                    "parent_fingerprint": "be6105b5",
                    "chain_code": "3460cea53e6a6bb5fb391eeef3237ffd8724bf0a40e94943c98b83825342ee11",
                    "prvkey": "6939694369114c67917a182c59ddb8cafc3004e63ca5d3b84403ba8613debc0c",
                    "pubkey": "0384610f5ecffe8fda089363a41f56a5c7ffc1d81b59a612d0d649b2d22355590c",
                },
                "m/0'/1": {
                    "parent_fingerprint": "9b02312f",
                    "chain_code": "4187afff1aafa8445010097fb99d23aee9f599450c7bd140b6826ac22ba21d0c",
                    "prvkey": "284e9d38d07d21e4e281b645089a94f4cf5a5a81369acf151a1c3a57f18b2129",
                    "pubkey": "03526c63f8d0b4bbbf9c80df553fe66742df4676b241dabefdef67733e070f6844",
                },
                "m/0'/1/2'": {
                    "parent_fingerprint": "b98005c1",
                    "chain_code": "98c7514f562e64e74170cc3cf304ee1ce54d6b6da4f880f313e8204c2a185318",
                    "prvkey": "694596e8a54f252c960eb771a3c41e7e32496d03b954aeb90f61635b8e092aa7",
                    "pubkey": "0359cf160040778a4b14c5f4d7b76e327ccc8c4a6086dd9451b7482b5a4972dda0",
                },
                "m/0'/1/2'/2": {
                    "parent_fingerprint": "0e9f3274",
                    "chain_code": "ba96f776a5c3907d7fd48bde5620ee374d4acfd540378476019eab70790c63a0",
                    "prvkey": "5996c37fd3dd2679039b23ed6f70b506c6b56b3cb5e424681fb0fa64caf82aaa",
                    "pubkey": "029f871f4cb9e1c97f9f4de9ccd0d4a2f2a171110c61178f84430062230833ff20",
                },
                "m/0'/1/2'/2/1000000000": {
                    "parent_fingerprint": "8b2b5c4b",
                    "chain_code": "b9b7b82d326bb9cb5b5b121066feea4eb93d5241103c9e7a18aad40f1dde8059",
                    "prvkey": "21c4f269ef0a5fd1badf47eeacebeeaa3de22eb8e5b0adcd0f27dd99d34d0119",
                    "pubkey": "02216cd26d31147f72427a453c443ed2cde8a1e53c9cc44e5ddf739725413fe3f4",
                },
            },
            CurveEnum.ED25519: {
                "m": {
                    "parent_fingerprint": "00000000",
                    "chain_code": "90046a93de5380a72b5e45010748567d5ea02bbf6522f979e05c0d8d8ca9fffb",
                    "prvkey": "2b4be7f19ee27bbf30c667b642d5f4aa69fd169872f8fc3059c08ebae2eb19e7",
                    "pubkey": "a4b2856bfec510abab89753fac1ac0e1112364e7d250545963f135f2a33188ed",
                },
                "m/0'": {
                    "parent_fingerprint": "6ff8a136",
                    "chain_code": "8b59aa11380b624e81507a27fedda59fea6d0b779a778918a2fd3590e16e9c69",
                    "prvkey": "68e0fe46dfb67e368c75379acec591dad19df3cde26e63b93a8e704f1dade7a3",
                    "pubkey": "8c8a13df77a28f3445213a0f432fde644acaa215fc72dcdf300d5efaa85d350c",
                },
                "m/0'/1'": {
                    "parent_fingerprint": "ede132cd",
                    "chain_code": "a320425f77d1b5c2505a6b1b27382b37368ee640e3557c315416801243552f14",
                    "prvkey": "b1d0bad404bf35da785a64ca1ac54b2617211d2777696fbffaf208f746ae84f2",
                    "pubkey": "1932a5270f335bed617d5b935c80aedb1a35bd9fc1e31acafd5372c30f5c1187",
                },
                "m/0'/1'/2'": {
                    "parent_fingerprint": "204218a5",
                    "chain_code": "2e69929e00b5ab250f49c3fb1c12f252de4fed2c1db88387094a0f8c4c9ccd6c",
                    "prvkey": "92a5b23c0b8a99e37d07df3fb9966917f5d06e02ddbd909c7e184371463e9fc9",
                    "pubkey": "ae98736566d30ed0e9d2f4486a64bc95740d89c7db33f52121f8ea8f76ff0fc1",
                },
                "m/0'/1'/2'/2'": {
                    "parent_fingerprint": "7dd2fab1",
                    "chain_code": "8f6d87f93d750e0efccda017d662a1b31a266e4a6f5993b15f5c1f07f74dd5cc",
                    "prvkey": "30d1dc7e5fc04c31219ab25a27ae00b50f6fd66622f6e9c913253d6511d1e662",
                    "pubkey": "8abae2d66361c879b900d204ad2cc4984fa2aa344dd7ddc46007329ac76c429c",
                },
                "m/0'/1'/2'/2'/1000000000'": {
                    "parent_fingerprint": "e4d754d1",
                    "chain_code": "68789923a0cac2cd5a29172a475fe9e0fb14cd6adb5ad98a3fa70333e7afa230",
                    "prvkey": "8f94d394a8e8fd6b1bc2f3f49f5c47e385281d5c17e65324b0f62483e37e8793",
                    "pubkey": "3c24da049451555d51a7014a37337aa4e12d41e485abccfa46b47dfb2af54b7a",
                },
            },
        }

    def test_slip0010_vector1(self):
        for curve, cases in self.vector1_from_slip0010().items():
            node = bip32_class_on_curve(curve).from_master_seed(bytes.fromhex(self._master_seed))

            for i, (path, result) in enumerate(cases.items()):
                with self.subTest(f"Case-{i}-{curve.name}-{path}"):
                    sub_node = node.derive_path(path)

                    self.assertEqual(sub_node.parent_fingerprint.hex(), result["parent_fingerprint"])
                    self.assertEqual(sub_node.chain_code.hex(), result["chain_code"])
                    self.assertEqual(sub_node._prvkey.hex(), result["prvkey"])
                    self.assertEqual(sub_node._pubkey.hex(), result["pubkey"])

    def test_slip0010_ed25519_vector2(self):
        master_seed = "fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542"
        cases = {
            "m": {
                "parent_fingerprint": "00000000",
                "chain_code": "ef70a74db9c3a5af931b5fe73ed8e1a53464133654fd55e7a66f8570b8e33c3b",
                "prvkey": "171cb88b1b3c1db25add599712e36245d75bc65a1a5c9e18d76f9f2b1eab4012",
                "pubkey": "8fe9693f8fa62a4305a140b9764c5ee01e455963744fe18204b4fb948249308a",
            },
            "m/0'": {
                "parent_fingerprint": "48ba9eef",
                "chain_code": "0b78a3226f915c082bf118f83618a618ab6dec793752624cbeb622acb562862d",
                "prvkey": "1559eb2bbec5790b0c65d8693e4d0875b1747f4970ae8b650486ed7470845635",
                "pubkey": "86fab68dcb57aa196c77c5f264f215a112c22a912c10d123b0d03c3c28ef1037",
            },
            "m/0'/2147483647'": {
                "parent_fingerprint": "3a1528bf",
                "chain_code": "138f0b2551bcafeca6ff2aa88ba8ed0ed8de070841f0c4ef0165df8181eaad7f",
                "prvkey": "ea4f5bfe8694d8bb74b7b59404632fd5968b774ed545e810de9c32a4fb4192f4",
                "pubkey": "5ba3b9ac6e90e83effcd25ac4e58a1365a9e35a3d3ae5eb07b9e4d90bcf7506d",
            },
            "m/0'/2147483647'/1'": {
                "parent_fingerprint": "b04982e6",
                "chain_code": "73bd9fff1cfbde33a1b846c27085f711c0fe2d66fd32e139d3ebc28e5a4a6b90",
                "prvkey": "3757c7577170179c7868353ada796c839135b3d30554bbb74a4b1e4a5a58505c",
                "pubkey": "2e66aa57069c86cc18249aecf5cb5a9cebbfd6fadeab056254763874a9352b45",
            },
            "m/0'/2147483647'/1'/2147483646'": {
                "parent_fingerprint": "b4c462dc",
                "chain_code": "0902fe8a29f9140480a00ef244bd183e8a13288e4412d8389d140aac1794825a",
                "prvkey": "5837736c89570de861ebc173b1086da4f505d4adb387c6a1b1342d5e4ac9ec72",
                "pubkey": "e33c0f7d81d843c572275f287498e8d408654fdf0d1e065b84e2e6f157aab09b",
            },
            "m/0'/2147483647'/1'/2147483646'/2'": {
                "parent_fingerprint": "0f2efabd",
                "chain_code": "5d70af781f3a37b829f0d060924d5e960bdc02e85423494afc0b1a41bbe196d4",
                "prvkey": "551d333177df541ad876a60ea71f00447931c0a9da16f227c11ea080d7391b8d",
                "pubkey": "47150c75db263559a70d5778bf36abbab30fb061ad69f69ece61a72b0cfa4fc0",
            },
        }

        node = bip32_class_on_curve(CurveEnum.ED25519).from_master_seed(bytes.fromhex(master_seed))
        for i, (path, result) in enumerate(cases.items()):
            with self.subTest(f"Case-{i}-{CurveEnum.ED25519.name}-{path}"):
                sub_node = node.derive_path(path)

                self.assertEqual(sub_node.parent_fingerprint.hex(), result["parent_fingerprint"])
                self.assertEqual(sub_node.chain_code.hex(), result["chain_code"])
                self.assertEqual(sub_node._prvkey.hex(), result["prvkey"])
                self.assertEqual(sub_node._pubkey.hex(), result["pubkey"])

    def test_slip0010_secp256r1_derivation_retry(self):
        node = bip32_class_on_curve(CurveEnum.SECP256R1).from_master_seed(bytes.fromhex(self._master_seed))
        sub_node = node.derive_path("m/28578'")
        self.assertEqual(sub_node.parent_fingerprint.hex(), "be6105b5")
        self.assertEqual(sub_node.chain_code.hex(), "e94c8ebe30c2250a14713212f6449b20f3329105ea15b652ca5bdfc68f6c65c2")
        self.assertEqual(sub_node._prvkey.hex(), "06f0db126f023755d0b8d86d4591718a5210dd8d024e3e14b6159d63f53aa669")
        self.assertEqual(sub_node._pubkey.hex(), "02519b5554a4872e8c9c1c847115363051ec43e93400e030ba3c36b52a3e70a5b7")

        with self.assertRaises(exceptions.InvalidECPointException):
            # different from slip-0010, leave it raise refer to trezor bip32 impl
            node.derive_path("m/28578'/33941")

    def test_slip0010_secp256r1_seed_retry(self):
        master_seed = "a7305bc8df8d0951f0cb224c0e95d7707cbdf2c6ce7e8d481fec69c7ff5e9446"
        node = bip32_class_on_curve(CurveEnum.SECP256R1).from_master_seed(bytes.fromhex(master_seed))
        self.assertEqual(node.parent_fingerprint.hex(), "00000000")
        self.assertEqual(node.chain_code.hex(), "7762f9729fed06121fd13f326884c82f59aa95c57ac492ce8c9654e60efd130c")
        self.assertEqual(node._prvkey.hex(), "3b8c18469a4634517d6d0b65448f8e6c62091b45540a1743c5846be55d47d88f")
        self.assertEqual(node._pubkey.hex(), "0383619fadcde31063d8c5cb00dbfe1713f3e6fa169d8541a798752a1c1ca0cb20")

    @staticmethod
    def vectors_from_bip0032() -> List[Tuple[str, dict]]:
        return [
            (
                "000102030405060708090a0b0c0d0e0f",
                {
                    "m": (
                        "xprv9s21ZrQH143K3QTDL4LXw2F7HEK3wJUD2nW2nRk4stbPy6cq3jPPqjiChkVvvNKmPGJxWUtg6LnF5kejMRNNU3TGtRBeJgk33yuGBxrMPHi",
                        "xpub661MyMwAqRbcFtXgS5sYJABqqG9YLmC4Q1Rdap9gSE8NqtwybGhePY2gZ29ESFjqJoCu1Rupje8YtGqsefD265TMg7usUDFdp6W1EGMcet8",
                    ),
                    "m/0'": (
                        "xprv9uHRZZhk6KAJC1avXpDAp4MDc3sQKNxDiPvvkX8Br5ngLNv1TxvUxt4cV1rGL5hj6KCesnDYUhd7oWgT11eZG7XnxHrnYeSvkzY7d2bhkJ7",
                        "xpub68Gmy5EdvgibQVfPdqkBBCHxA5htiqg55crXYuXoQRKfDBFA1WEjWgP6LHhwBZeNK1VTsfTFUHCdrfp1bgwQ9xv5ski8PX9rL2dZXvgGDnw",
                    ),
                    "m/0'/1": (
                        "xprv9wTYmMFdV23N2TdNG573QoEsfRrWKQgWeibmLntzniatZvR9BmLnvSxqu53Kw1UmYPxLgboyZQaXwTCg8MSY3H2EU4pWcQDnRnrVA1xe8fs",
                        "xpub6ASuArnXKPbfEwhqN6e3mwBcDTgzisQN1wXN9BJcM47sSikHjJf3UFHKkNAWbWMiGj7Wf5uMash7SyYq527Hqck2AxYysAA7xmALppuCkwQ",
                    ),
                    "m/0'/1/2'": (
                        "xprv9z4pot5VBttmtdRTWfWQmoH1taj2axGVzFqSb8C9xaxKymcFzXBDptWmT7FwuEzG3ryjH4ktypQSAewRiNMjANTtpgP4mLTj34bhnZX7UiM",
                        "xpub6D4BDPcP2GT577Vvch3R8wDkScZWzQzMMUm3PWbmWvVJrZwQY4VUNgqFJPMM3No2dFDFGTsxxpG5uJh7n7epu4trkrX7x7DogT5Uv6fcLW5",
                    ),
                    "m/0'/1/2'/2": (
                        "xprvA2JDeKCSNNZky6uBCviVfJSKyQ1mDYahRjijr5idH2WwLsEd4Hsb2Tyh8RfQMuPh7f7RtyzTtdrbdqqsunu5Mm3wDvUAKRHSC34sJ7in334",
                        "xpub6FHa3pjLCk84BayeJxFW2SP4XRrFd1JYnxeLeU8EqN3vDfZmbqBqaGJAyiLjTAwm6ZLRQUMv1ZACTj37sR62cfN7fe5JnJ7dh8zL4fiyLHV",
                    ),
                    "m/0'/1/2'/2/1000000000": (
                        "xprvA41z7zogVVwxVSgdKUHDy1SKmdb533PjDz7J6N6mV6uS3ze1ai8FHa8kmHScGpWmj4WggLyQjgPie1rFSruoUihUZREPSL39UNdE3BBDu76",
                        "xpub6H1LXWLaKsWFhvm6RVpEL9P4KfRZSW7abD2ttkWP3SSQvnyA8FSVqNTEcYFgJS2UaFcxupHiYkro49S8yGasTvXEYBVPamhGW6cFJodrTHy",
                    ),
                },
            ),
            (
                "fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542",
                {
                    "m": (
                        "xprv9s21ZrQH143K31xYSDQpPDxsXRTUcvj2iNHm5NUtrGiGG5e2DtALGdso3pGz6ssrdK4PFmM8NSpSBHNqPqm55Qn3LqFtT2emdEXVYsCzC2U",
                        "xpub661MyMwAqRbcFW31YEwpkMuc5THy2PSt5bDMsktWQcFF8syAmRUapSCGu8ED9W6oDMSgv6Zz8idoc4a6mr8BDzTJY47LJhkJ8UB7WEGuduB",
                    ),
                    "m/0": (
                        "xprv9vHkqa6EV4sPZHYqZznhT2NPtPCjKuDKGY38FBWLvgaDx45zo9WQRUT3dKYnjwih2yJD9mkrocEZXo1ex8G81dwSM1fwqWpWkeS3v86pgKt",
                        "xpub69H7F5d8KSRgmmdJg2KhpAK8SR3DjMwAdkxj3ZuxV27CprR9LgpeyGmXUbC6wb7ERfvrnKZjXoUmmDznezpbZb7ap6r1D3tgFxHmwMkQTPH",
                    ),
                    "m/0/2147483647'": (
                        "xprv9wSp6B7kry3Vj9m1zSnLvN3xH8RdsPP1Mh7fAaR7aRLcQMKTR2vidYEeEg2mUCTAwCd6vnxVrcjfy2kRgVsFawNzmjuHc2YmYRmagcEPdU9",
                        "xpub6ASAVgeehLbnwdqV6UKMHVzgqAG8Gr6riv3Fxxpj8ksbH9ebxaEyBLZ85ySDhKiLDBrQSARLq1uNRts8RuJiHjaDMBU4Zn9h8LZNnBC5y4a",
                    ),
                    "m/0/2147483647'/1": (
                        "xprv9zFnWC6h2cLgpmSA46vutJzBcfJ8yaJGg8cX1e5StJh45BBciYTRXSd25UEPVuesF9yog62tGAQtHjXajPPdbRCHuWS6T8XA2ECKADdw4Ef",
                        "xpub6DF8uhdarytz3FWdA8TvFSvvAh8dP3283MY7p2V4SeE2wyWmG5mg5EwVvmdMVCQcoNJxGoWaU9DCWh89LojfZ537wTfunKau47EL2dhHKon",
                    ),
                    "m/0/2147483647'/1/2147483646'": (
                        "xprvA1RpRA33e1JQ7ifknakTFpgNXPmW2YvmhqLQYMmrj4xJXXWYpDPS3xz7iAxn8L39njGVyuoseXzU6rcxFLJ8HFsTjSyQbLYnMpCqE2VbFWc",
                        "xpub6ERApfZwUNrhLCkDtcHTcxd75RbzS1ed54G1LkBUHQVHQKqhMkhgbmJbZRkrgZw4koxb5JaHWkY4ALHY2grBGRjaDMzQLcgJvLJuZZvRcEL",
                    ),
                    "m/0/2147483647'/1/2147483646'/2": (
                        "xprvA2nrNbFZABcdryreWet9Ea4LvTJcGsqrMzxHx98MMrotbir7yrKCEXw7nadnHM8Dq38EGfSh6dqA9QWTyefMLEcBYJUuekgW4BYPJcr9E7j",
                        "xpub6FnCn6nSzZAw5Tw7cgR9bi15UV96gLZhjDstkXXxvCLsUXBGXPdSnLFbdpq8p9HmGsApME5hQTZ3emM2rnY5agb9rXpVGyy3bdW6EEgAtqt",
                    ),
                },
            ),
            (
                "4b381541583be4423346c643850da4b320e46a87ae3d2a4e6da11eba819cd4acba45d239319ac14f863b8d5ab5a0d0c64d2e8a1e7d1457df2e5a3c51c73235be",
                {
                    "m": (
                        "xprv9s21ZrQH143K25QhxbucbDDuQ4naNntJRi4KUfWT7xo4EKsHt2QJDu7KXp1A3u7Bi1j8ph3EGsZ9Xvz9dGuVrtHHs7pXeTzjuxBrCmmhgC6",
                        "xpub661MyMwAqRbcEZVB4dScxMAdx6d4nFc9nvyvH3v4gJL378CSRZiYmhRoP7mBy6gSPSCYk6SzXPTf3ND1cZAceL7SfJ1Z3GC8vBgp2epUt13",
                    ),
                    "m/0'": (
                        "xprv9uPDJpEQgRQfDcW7BkF7eTya6RPxXeJCqCJGHuCJ4GiRVLzkTXBAJMu2qaMWPrS7AANYqdq6vcBcBUdJCVVFceUvJFjaPdGZ2y9WACViL4L",
                        "xpub68NZiKmJWnxxS6aaHmn81bvJeTESw724CRDs6HbuccFQN9Ku14VQrADWgqbhhTHBaohPX4CjNLf9fq9MYo6oDaPPLPxSb7gwQN3ih19Zm4Y",
                    ),
                },
            ),
        ]

    def test_bip0032_vectors(self):
        curve = CurveEnum.SECP256K1
        for i, (seed, vector) in enumerate(self.vectors_from_bip0032()):
            with self.subTest(f"Case-{i}-{seed}"):
                node = bip32_class_on_curve(curve).from_master_seed(bytes.fromhex(seed))

                for j, (path, (xprv, xpub)) in enumerate(vector.items()):
                    with self.subTest(f"Case-{i}-{seed}-{j}-{path}"):
                        sub_node = node.derive_path(path)
                        self.assertEqual(sub_node.get_hd_wif(), xpub)
                        self.assertEqual(sub_node.get_hd_wif(as_private=True), xprv)

    def test_bip0032_vectors_xprv(self):
        curve = CurveEnum.SECP256K1
        for i, (_, vector) in enumerate(self.vectors_from_bip0032()):
            for path, (xprv, xpub) in vector.items():
                with self.subTest(f"Case-{i}-{path}-{xprv}"):
                    node = bip32_class_on_curve(curve).from_hd_wif(xprv)

                    self.assertEqual(node.get_hd_wif(), xpub)
                    self.assertEqual(node.get_hd_wif(as_private=True), xprv)

                    sub_vector = {"m" + p[len(path) :]: v for p, v in vector.items() if path in p}
                    for j, (sub_path, (sub_xprv, sub_xpub)) in enumerate(sub_vector.items()):
                        with self.subTest(f"Case-{i}-{path}-{xprv}-{j}-{sub_path}"):
                            sub_node = node.derive_path(sub_path)
                            self.assertEqual(sub_node.get_hd_wif(), sub_xpub)
                            self.assertEqual(sub_node.get_hd_wif(as_private=True), sub_xprv)

    def test_bip0032_vectors_xpub(self):
        curve = CurveEnum.SECP256K1
        for i, (_, vector) in enumerate(self.vectors_from_bip0032()):
            for path, (_, xpub) in vector.items():
                with self.subTest(f"Case-{i}-{path}-{xpub}"):
                    node = bip32_class_on_curve(curve).from_hd_wif(xpub)

                    self.assertEqual(node.get_hd_wif(), xpub)
                    with self.assertRaisesRegex(Exception, "Private key not found"):
                        node.get_hd_wif(as_private=True)

                    sub_vector = {"m" + p[len(path) :]: v for p, v in vector.items() if path in p}
                    for j, (sub_path, (_, sub_xpub)) in enumerate(sub_vector.items()):
                        with self.subTest(f"Case-{i}-{path}-{xpub}-{j}-{sub_path}"):
                            if "'" in sub_path:
                                with self.assertRaisesRegex(Exception, "is_hardened is only supported on private key"):
                                    node.derive_path(sub_path)

                                break

                            sub_node = node.derive_path(sub_path)
                            self.assertEqual(sub_node.get_hd_wif(), sub_xpub)
                            with self.assertRaisesRegex(Exception, "Private key not found"):
                                sub_node.get_hd_wif(as_private=True)
