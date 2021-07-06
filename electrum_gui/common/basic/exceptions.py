class OneKeyException(Exception):
    key = "msg__unknown_error"
    other_info = ""

    def __init__(self, other_info=None):
        if other_info is not None:
            self.other_info = other_info


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


class AddressNotInCurrentWallet(OneKeyException):
    key = "msg__the_address_is_not_in_the_current_wallet"


class ThisIsWatchOnlyWallet(OneKeyException):
    key = "msg__this_is_a_watching_only_wallet"


class CurWalletNotSuppSigMesg(OneKeyException):
    key = "msg__current_wallet_does_not_support_signature_message"


class ReplaceWatchOnlyWallet(OneKeyException):
    key = "msg__replace_watch_only_wallet"


class NotSupportExportSeed(OneKeyException):
    key = "msg__current_wallet_does_not_support_exporting_mnemonic"


class FileAlreadyExist(OneKeyException):
    key = "msg__file_already_exists"


class FailedGetTx(OneKeyException):
    key = "msg__failed_to_get_transaction"


class BroadcastFailedDueToNetExcept(OneKeyException):
    key = "msg__cannot_broadcast_transaction_due_to_network_connected_exceptions"


class TxFormatError(OneKeyException):
    key = "msg__transaction_formatter_error"


class TxBroadcastError(OneKeyException):
    key = "msg__transaction_broadcast_error"


class PythonLibNotStart(OneKeyException):
    key = "msg__python_lib_not_start_please_restart_app"
