import threading
import weakref
from typing import Callable


def _make_id(receiver):
    if hasattr(receiver, "__self__") and hasattr(receiver, "__func__"):
        return (id(receiver.__self__), id(receiver.__func__))  # noqa
    else:
        return id(receiver)


class Signal(object):
    def __init__(self, name: str):
        self._receivers = []
        self._lock = threading.Lock()
        self._clean_flag = False
        self._name = name

    def __str__(self):
        return f"Signal<{self._name}>"

    def connect(self, receiver: Callable, use_weakref: bool = True):
        self.disconnect(receiver)

        receiver_id = _make_id(receiver)

        if use_weakref:
            ref_wrap = weakref.ref
            receiver_object = receiver

            if hasattr(receiver, "__self__") and hasattr(receiver, "__func__"):  # handle bound method
                ref_wrap = weakref.WeakMethod
                receiver_object = receiver.__self__

            receiver = ref_wrap(receiver)
            weakref.finalize(receiver_object, self._set_clean_flag)

        with self._lock:
            self._receivers.append((receiver_id, receiver))

    def disconnect(self, receiver: Callable):
        receiver_id = _make_id(receiver)

        with self._lock:
            for i in range(len(self._receivers)):
                cur_id, _ = self._receivers[i]
                if cur_id == receiver_id:
                    self._receivers.pop(i)

        self._clean_dead_receivers()

    def _set_clean_flag(self):
        self._clean_flag = True

    def _clean_dead_receivers(self):
        if not self._clean_flag:
            return

        with self._lock:
            self._receivers = [
                (receiver_id, receiver)
                for (receiver_id, receiver) in self._receivers
                if not isinstance(receiver, weakref.ReferenceType)
                or receiver() is not None  # is not a weak reference or still maintains a weak reference
            ]
            self._clean_flag = False

    def send(self, *args, **kwargs):
        self._clean_dead_receivers()

        for _, receiver in self._receivers:
            if isinstance(receiver, weakref.ReferenceType):
                receiver = receiver()

            receiver(*args, **kwargs)
