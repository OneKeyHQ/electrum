import logging

from electrum_gui.common.basic.ticker import signals, ticker

logger = logging.getLogger("app.ticker")

_ticker = None


def start_default_ticker(seconds: int):
    global _ticker
    if _ticker is not None:
        logger.warning("start ticker already")
        return

    _ticker = ticker.Ticker(seconds, signals.ticker_signal)
    _ticker.start()


def cancel_default_ticker():
    global _ticker
    _ticker.cancel()
