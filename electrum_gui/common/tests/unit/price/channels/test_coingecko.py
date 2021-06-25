from unittest import TestCase
from unittest.mock import Mock, call, patch

from electrum_gui.common.price import data
from electrum_gui.common.price.channels.coingecko import Coingecko


class TestCoingecko(TestCase):
    def setUp(self) -> None:
        self.cgk = Coingecko()
        self.cgk.restful = Mock()

    @patch("electrum_gui.common.conf.chains.get_coingecko_ids")
    def test_pricing_btc(self, fake_get_cgkids):
        fake_get_cgkids.return_value = []
        self.cgk.restful.get.return_value = {
            "rates": {
                "usd": {
                    "type": "fiat",
                    "value": 120000,
                },
                "cny": {
                    "type": "fiat",
                    "value": 780000,
                },
                "btc": {
                    "type": "currency",
                    "value": 1,
                },
            }
        }

        self.assertEqual(
            [data.YieldedPrice("btc", 120000, "usd"), data.YieldedPrice("btc", 780000, "cny")],
            list(self.cgk.pricing([Mock(code="btc", chain_code="btc", token_address=None)])),
        )
        self.cgk.restful.get.assert_called_once_with("/api/v3/exchange_rates")

    @patch("electrum_gui.common.conf.chains.get_coin_codes_by_coingecko_id")
    @patch("electrum_gui.common.conf.chains.get_coingecko_ids")
    def test_pricing_pre_config_cgk_id_coins(self, fake_get_cgkids, fake_get_coin_codes):
        mappings = {
            "ethereum": ["eth"],
            "binancecoin": ["bsc"],
        }
        fake_get_cgkids.return_value = list(mappings.keys())
        fake_get_coin_codes.side_effect = lambda cgkid: mappings.get(cgkid)
        self.cgk.restful.get.side_effect = [
            {},
            [
                {
                    "id": "ethereum",
                    "current_price": 0.125,
                },
                {
                    "id": "binancecoin",
                    "current_price": 0.001,
                },
            ],
        ]

        self.assertEqual(
            [
                data.YieldedPrice("eth", 0.125, "btc"),
                data.YieldedPrice("bsc", 0.001, "btc"),
            ],
            list(
                self.cgk.pricing(
                    [
                        Mock(code="eth", chain_code="eth", token_address=None),
                        Mock(code="bsc", chain_code="bsc", token_address=None),
                        Mock(code="okt", chain_code="okt", token_address=None),
                    ]
                )
            ),
        )
        self.assertEqual(2, self.cgk.restful.get.call_count)
        self.cgk.restful.get.assert_has_calls(
            [
                call("/api/v3/exchange_rates"),
                call(
                    "/api/v3/coins/markets",
                    params={
                        "ids": "ethereum,binancecoin",
                        "vs_currency": "btc",
                    },
                ),
            ]
        )

    @patch("electrum_gui.common.conf.chains.get_coingecko_ids")
    def test_pricing_erc20(self, fake_get_cgkids):
        fake_get_cgkids.return_value = []
        self.cgk.restful.get.side_effect = [{}, {"0x11": {"btc": 0.000125}, "0xab": {"btc": 0.00011}}]

        self.assertEqual(
            [
                data.YieldedPrice("eth_cc", 0.000125, "btc"),
                data.YieldedPrice("eth_ab", 0.00011, "btc"),
            ],
            list(
                self.cgk.pricing(
                    [
                        Mock(code="eth_cc", chain_code="eth", token_address="0x11"),
                        Mock(code="eth_ab", chain_code="eth", token_address="0xAb"),
                    ]
                )
            ),
        )
        self.assertEqual(2, self.cgk.restful.get.call_count)
        self.cgk.restful.get.assert_has_calls(
            [
                call("/api/v3/exchange_rates"),
                call(
                    "/api/v3/simple/token_price/ethereum",
                    params={
                        "contract_addresses": "0x11,0xab",
                        "vs_currencies": "btc",
                    },
                ),
            ]
        )
