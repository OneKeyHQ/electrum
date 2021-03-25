import random
import string

from electrum.keystore import bip44_derivation, bip44_eth_derivation


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
        default_path = bip44_derivation(0, bip43_purpose=purpose)
        return default_path[0 : default_path.rindex('/')]
    elif coin in ['eth', 'bsc', 'heco']:
        default_path = bip44_eth_derivation(0)
        return default_path[0 : default_path.rindex('/')]


def get_temp_file():
    return "".join(random.sample(string.ascii_letters + string.digits, 8)) + ".unique.file"


def get_path_info(path, pos):
    return path.split("/")[pos].split("'")[0]
