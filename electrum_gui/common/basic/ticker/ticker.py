import logging
from threading import Timer

from electrum_gui.common.basic.functional.signal import Signal
from electrum_gui.common.basic.functional.timing import timing_logger

logger = logging.getLogger("app.ticker")


class Ticker(Timer):
    def __init__(self, interval: int, signal: Signal):
        self._signal = signal
        super(Ticker, self).__init__(interval, self._send_signal)

    def _send_signal(self):
        if self._signal is None:
            return

        try:
            with timing_logger(f"ticker.send_signal by {self._signal}"):
                self._signal.send()
        except Exception as e:
            logger.exception(f"Error in sending signal. signal: {self._signal}", e)

    def run(self):
        while not self.finished.is_set():
            self.function(*self.args, **self.kwargs)
            self.finished.wait(self.interval)
