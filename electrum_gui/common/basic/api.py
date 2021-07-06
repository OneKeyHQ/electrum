import enum
import functools
import json

from electrum_gui.common.basic import exceptions
from electrum_gui.common.basic.functional import json_encoders


@enum.unique
class Version(enum.IntEnum):
    V1 = 1
    V2 = 2


@enum.unique
class ResultStatus(enum.IntEnum):
    SUCCESS = 0
    FAILED = 1


def api_entry(force_version: int = None):
    def middle(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            def _filter_params(err=None):
                error_msg = {"fun_name": func.__name__}
                error_msg.update({k: v for k, v in kwargs.items() if k not in ["seed", "password", "mnemonic"]})
                if err is not None:
                    error_msg.update({"err_msg_detail": "%s:%s" % (err.__class__.__name__, err.args)})
                return error_msg

            api_version = kwargs.pop("api_version", Version.V1)
            api_version = force_version or api_version

            if api_version == Version.V1:
                return func(*args, **kwargs)
            elif api_version == Version.V2:
                try:
                    result = func(*args, **kwargs)
                    out = {"status": ResultStatus.SUCCESS, "info": result}
                except exceptions.OneKeyException as e:
                    out = {"status": ResultStatus.FAILED, "err_msg_key": e.key, "other_info": e.other_info}
                except Exception as e:
                    out = {
                        "status": ResultStatus.FAILED,
                        "err_msg_key": exceptions.OneKeyException.key,
                        "low_level_error": _filter_params(e),
                    }
                out.update({"api_version": api_version})
                return json.dumps(out, cls=json_encoders.DecimalEncoder)
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
