import random
import string
from typing import Tuple

import eth_utils

from electrum.keystore import bip44_derivation, bip44_eth_derivation
from electrum_gui.common.secret import interfaces as secret_interfaces


def get_best_block_by_feerate(feerate, fee_info_list):
    if feerate < fee_info_list[20]:
        return 25
    elif feerate < fee_info_list[10]:
        return 20
    elif feerate < fee_info_list[5]:
        return 10
    elif feerate < fee_info_list[4]:
        return 5
    elif feerate < fee_info_list[3]:
        return 4
    elif feerate < fee_info_list[2]:
        return 3
    elif feerate < fee_info_list[1]:
        return 2
    else:
        return 1


def get_show_addr(addr):
    return f"{addr[0:6]}...{addr[-6:]}"


def get_default_path(coin, purpose):
    if coin == 'btc':
        return bip44_derivation(0, bip43_purpose=purpose)
    elif coin in ['eth', 'bsc', 'heco', 'okt']:
        return bip44_eth_derivation(0)


def get_temp_file():
    return "".join(random.sample(string.ascii_letters + string.digits, 8)) + ".unique.file"


def get_path_info(path, pos):
    return path.split("/")[pos].split("'")[0]


class EthSoftwareSigner(secret_interfaces.SignerInterface):
    def __init__(self, wallet, password):
        self.prvkey = wallet.get_account(wallet.get_addresses()[0], password)._key_obj

    def get_pubkey(self, compressed=True) -> bytes:
        raise NotImplementedError()  # Shouldn't reach

    def verify(self, digest: bytes, signature: bytes) -> bool:
        raise NotImplementedError()  # Shouldn't reach

    def sign(self, digest: bytes) -> Tuple[bytes, int]:
        signature = self.prvkey.sign_msg_hash(digest)
        sig = eth_utils.int_to_big_endian(signature.r) + eth_utils.int_to_big_endian(signature.s)
        rec_id = signature.v
        return sig, rec_id
