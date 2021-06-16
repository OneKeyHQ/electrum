import functools
import json
from enum import IntEnum, unique

from electrum.util import DecimalEncoder


@unique
class ResultStatus(IntEnum):
    SUCCESS = 0
    FAILED = 1
    APP_USED = 2


@unique
class ApiVersion(IntEnum):
    V1 = 1
    V2 = 2


class OneKeyException(Exception):
    key = "msg__unknown_error"
    status = ResultStatus.FAILED


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


class UnavailableAddress(OneKeyException):
    key = "msg__incorrect_address"


def catch_exception(force_api_version: int = None):
    def middle(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            def _filter_params(err=None):
                error_msg = {"fun_name": func.__name__}
                error_msg.update({k: v for k, v in kwargs.items() if k not in ["seed", "password", "mnemonic"]})
                if err is not None:
                    error_msg.update({"err_msg_detail": "%s:%s" % (err.__class__.__name__, err.args)})
                return error_msg

            api_version = kwargs.pop("api_version", ApiVersion.V1)
            api_version = force_api_version or api_version

            if api_version == ApiVersion.V1:
                return func(*args, **kwargs)
            elif api_version == ApiVersion.V2:
                try:
                    result = func(*args, **kwargs)
                    out = {"status": ResultStatus.SUCCESS, "info": result}
                except OneKeyException as e:
                    out = {"status": e.status, "err_msg_key": e.key}
                except Exception as e:
                    out = {
                        "status": OneKeyException.status,
                        "err_msg_key": OneKeyException.key,
                        "low_level_error": _filter_params(e),
                    }
                out.update({"api_version": api_version})
                return json.dumps(out, cls=DecimalEncoder)
            else:
                return json.dumps(
                    {
                        "status": ResultStatus.FAILED,
                        "err_msg_key": "msg_unknown_error",
                        "low_level_error": "unsupported api version",
                        "api_version": api_version,
                    }
                )

        return wrapper

    return middle
