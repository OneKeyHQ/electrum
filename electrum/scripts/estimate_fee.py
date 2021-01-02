#!/usr/bin/env python3
import asyncio
import json
from numbers import Number
from statistics import median

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
            peers, "blockchain.estimatefee", [2]
        )
        print(json.dumps(results, indent=4))
        feerate_estimates = filter(lambda x: isinstance(x, Number), results.values())
        print(f"median feerate: {median(feerate_estimates)}")
    finally:
        stopping_fut.set_result(1)


asyncio.run_coroutine_threadsafe(f(), loop)
