class OneKeyException(Exception):
    key = "msg__unknown_error"


class UnavailablePrivateKey(OneKeyException):
    key = "msg__incorrect_private_key"


class InvalidKeystoreFormat(OneKeyException):
    key = "msg__incorrect_keystore_format"


class InvalidMnemonicFormat(OneKeyException):
    key = "msg__incorrect_recovery_phrase_format"


class UnavailableBtcAddr(OneKeyException):
    key = "msg__incorrect_bitcoin_address"


class InvalidPassword(OneKeyException):
    key = "msg__incorrect_password"


class UnavailablePublicKey(OneKeyException):
    key = "msg__incorrect_public_key"


class UnavailableEthAddr(OneKeyException):
    key = "msg__incorrect_eth_address"


class IncorrectAddress(OneKeyException):
    key = "msg__incorrect_address"


class InactiveAddress(OneKeyException):
    key = "msg__the_address_has_not_been_activated_please_enter_receipt_identifier"


class UnsupportedCurrencyCoin(OneKeyException):
    key = "msg__unsupported_currency_coin"


class NotEnoughFunds(OneKeyException):
    key = "msg__not_enough_fund"


class InvalidBip39Seed(OneKeyException):
    key = "msg__invalid_bip39_seed"


class UserCancel(OneKeyException):
    key = "msg__user_cancel"


class DerivedWalletLimit(OneKeyException):
    key = "msg__derived_wallet_limit"


class NotChosenWallet(OneKeyException):
    key = "msg__you_have_not_chosen_a_wallet_yet"


class DustTransaction(OneKeyException):
    key = "msg__dust_transaction"


#####################################
#      hardware exceptions          #
#####################################
class HardwareInvalidPIN(OneKeyException):
    key = "msg__incorrect_pin_please_try_again"


class HardwareUpdateFailed(OneKeyException):
    key = "msg__update_failed_please_try_again"
