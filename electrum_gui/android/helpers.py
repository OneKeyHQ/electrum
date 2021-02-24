import hashlib
import random
import string


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


def get_temp_file():
    return "".join(random.sample(string.ascii_letters + string.digits, 8)) + ".unique.file"


def get_unique_wallet_filename(wallet):
    return hashlib.sha256(wallet.get_addresses()[0].encode()).hexdigest()


def get_path_info(path, pos):
    return path.split("/")[pos].split("'")[0]


def get_derivation_path(wallet, address):
    deriv_suffix = wallet.get_address_index(address)
    derivation = wallet.keystore.get_derivation_prefix()
    address_path = "%s/%d/%d" % (derivation, *deriv_suffix)
    return address_path
