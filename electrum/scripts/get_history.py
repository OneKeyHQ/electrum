#!/usr/bin/env python3

import asyncio
import sys

from electrum import bitcoin
from electrum.network import Network
from electrum.simple_config import SimpleConfig
from electrum.util import (
    create_and_start_event_loop,
    json_encode,
    log_exceptions,
    print_msg,
)

try:
    addr = sys.argv[1]
except Exception:
    print("usage: get_history <bitcoin_address>")
    sys.exit(1)

config = SimpleConfig()

loop, stopping_fut, loop_thread = create_and_start_event_loop()
network = Network(config)
network.start()


@log_exceptions
async def f():
    try:
        sh = bitcoin.address_to_scripthash(addr)
        hist = await network.get_history_for_scripthash(sh)
        print_msg(json_encode(hist))
    finally:
        stopping_fut.set_result(1)


asyncio.run_coroutine_threadsafe(f(), loop)
