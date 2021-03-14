import functools
import time
from typing import Optional


def on_interval(seconds: int):  # in seconds
    def wrapper(fn):
        _last_called_time: Optional[float] = None

        @functools.wraps(fn)
        def inner(*args, **kwargs):
            nonlocal _last_called_time
            now = time.time()
            if not _last_called_time or _last_called_time + seconds < now:
                _last_called_time = now
                return fn(*args, **kwargs)

        return inner

    return wrapper
