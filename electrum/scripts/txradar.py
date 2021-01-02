#!/usr/bin/env python3
import asyncio
import sys

from electrum.network import Network, filter_protocol
from electrum.simple_config import SimpleConfig
from electrum.util import create_and_start_event_loop, log_exceptions

try:
    txid = sys.argv[1]
except:
    print("usage: txradar txid")
    sys.exit(1)

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
            peers, "blockchain.transaction.get", [txid]
        )
        r1, r2 = [], []
        for k, v in results.items():
            (r1 if not isinstance(v, Exception) else r2).append(k)
        print(f"Received {len(results)} answers")
        try:
            propagation = len(r1) * 100.0 / (len(r1) + len(r2))
        except ZeroDivisionError:
            propagation = 0
        print(f"Propagation rate: {propagation:.1f} percent")
    finally:
        stopping_fut.set_result(1)


asyncio.run_coroutine_threadsafe(f(), loop)
