"""
Contains useful constants.
"""

# transaction types
payment_txn = "pay"
"""str: indicates a payment transaction"""
assetconfig_txn = "acfg"
"""str: indicates an asset configuration transaction"""
assettransfer_txn = "axfer"
"""str: indicates an asset transfer transaction"""


# prefixes
txid_prefix = b"TX"
"""bytes: transaction prefix when signing"""


hash_len = 32
"""int: how long various hash-like fields should be"""
check_sum_len_bytes = 4
"""int: how long checksums should be"""
key_len_bytes = 32
"""int: how long addresses are in bytes"""
address_len = 58
"""int: how long addresses are in base32, including the checksum"""
min_txn_fee = 1000
"""int: minimum transaction fee"""
metadata_length = 32
"""int: length of asset metadata"""
note_max_length = 1024
"""int: maximum length of note field"""
lease_length = 32
"""int: byte length of leases"""
max_asset_decimals = 19
"""int: maximum value for decimals in assets"""
