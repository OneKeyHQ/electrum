#!/usr/bin/env python3
import asyncio

from electrum.blockchain import hash_raw_header
from electrum.network import Network, filter_protocol
from electrum.simple_config import SimpleConfig
from electrum.util import create_and_start_event_loop, log_exceptions

config = SimpleConfig()

loop, stopping_fut, loop_thread = create_and_start_event_loop()
network = Network(config)
network.start()


@log_exceptions
async def f():
    try:
        peers = await network.get_peers()
        peers = filter_protocol(peers)
        results = await network.send_multiple_requests(
            peers, "blockchain.headers.subscribe", []
        )
        for server, header in sorted(results.items(), key=lambda x: x[1].get("height")):
            height = header.get("height")
            blockhash = hash_raw_header(header.get("hex"))
            print(server, height, blockhash)
    finally:
        stopping_fut.set_result(1)


asyncio.run_coroutine_threadsafe(f(), loop)
