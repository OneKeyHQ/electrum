from electrum import bitcoin, util

# TODO: these functions are moved from no-self-use methods of AndroidCommands
# in console.py, consider remove them.


# #qr api
def get_raw_tx_from_qr_data(data):
    return util.bh2u(bitcoin.base_decode(data, None, base=43))


def get_qr_data_from_raw_tx(raw_tx):
    text = bitcoin.bfh(raw_tx)
    return bitcoin.base_encode(text, base=43)


def _on_callback(*args):
    util.print_stderr("[Callback] " + ", ".join(repr(x) for x in args))
