import enum
import functools
import json

from electrum_gui.common.basic import exceptions
from electrum_gui.common.basic.functional import json_encoders


@enum.unique
class Version(enum.IntEnum):
    V1 = 1  # Legacy
    V2 = 2  # Unify return values and exceptions
    V3 = 3  # Single kwarg params as a json str


SUPPORTED_VERSIONS = list(Version)


@enum.unique
class ResultStatus(enum.IntEnum):
    SUCCESS = 0
    FAILED = 1
    APP_USED = 2


def api_entry(force_version: int = None):
    def middle(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            pre_run_failure_msg = None
            api_version = kwargs.pop("api_version", Version.V1)
            api_version = force_version or api_version

            if api_version == Version.V1:  # Legacy
                return func(*args, **kwargs)

            # Version.V2 onward

            if api_version not in SUPPORTED_VERSIONS:
                pre_run_failure_msg = "Unsupported API version."
            elif api_version >= Version.V3:  # Load (and check) params
                params = kwargs.pop("params", None)
                # TODO:
                #  - check len(args) <= 1, only for the main resource id
                #  - check kwargs is now empty, all the parameters should be put in params
                if params is not None:
                    try:
                        params = json.loads(params)
                        if not isinstance(params, dict):
                            raise ValueError
                        # TODO: add param validation
                    except ValueError:
                        pre_run_failure_msg = "Failed to load params."
                    else:
                        kwargs["params"] = params
                else:
                    kwargs["params"] = {}

            ret = {
                "status": ResultStatus.FAILED,
                "api_version": api_version,
            }
            if pre_run_failure_msg is None:
                try:
                    result = func(*args, **kwargs)
                except exceptions.OneKeyException as e:
                    ret["err_msg_key"] = e.key
                except Exception as e:
                    ret["err_msg_key"] = exceptions.OneKeyException.key
                    ret["low_level_error"] = f"{e.__class__.__name__}: {str(e)}"
                else:
                    ret["info"] = result
                    ret["status"] = ResultStatus.SUCCESS
            else:
                ret["err_msg_key"] = exceptions.OneKeyException.key
                ret["low_level_error"] = pre_run_failure_msg

            return json.dumps(ret, cls=json_encoders.DecimalEncoder)

        return wrapper

    return middle
