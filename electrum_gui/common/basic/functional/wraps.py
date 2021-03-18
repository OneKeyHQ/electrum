import functools
import time


def cache_it(timeout: int = 60):  # in seconds
    def wrapper(fn):
        _cache = {}

        @functools.wraps(fn)
        def inner(*args, **kwargs):
            nonlocal _cache
            force_update = kwargs.pop("__force_update_cache_it__", False)
            key_ = str((args, tuple(sorted(kwargs.items()))))
            val, expired_at = _cache.get(key_) or (None, None)

            if force_update or expired_at is None or expired_at < time.time():
                _cache.pop(key_, None)
                val = fn(*args, **kwargs)
                expired_at = time.time() + timeout
                _cache[key_] = (val, expired_at)

            return val

        return inner

    return wrapper
