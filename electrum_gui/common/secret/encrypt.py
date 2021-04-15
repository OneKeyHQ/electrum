import electrum.crypto


def encrypt_data(password: str, data: str) -> str:
    return electrum.crypto.pw_encode(data, password, version=1)


def decrypt_data(password: str, data: str) -> str:
    return electrum.crypto.pw_decode(data, password, version=1)
