import logging
import threading
import time
import uuid
from contextlib import contextmanager
from typing import Callable

logger = logging.getLogger("app.timing")


@contextmanager
def timing_logger(tag: str, logger_func: Callable = logger.debug):
    identify = [tag, uuid.uuid4().hex[:8], threading.current_thread().name]
    prefix = f"TimingLogger<{', '.join(identify)}>"

    start_time = time.time()
    logger_func(f"{prefix}, start timing logger. now: {start_time}")
    try:
        yield
    finally:
        end_time = time.time()
        time_used = round(end_time - start_time, 4)
        logger_func(f"{prefix}, end timing logger. time_used: {time_used}s")
