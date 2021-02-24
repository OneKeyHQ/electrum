from electrum import bitcoin, pywalib, util

# TODO: these functions are moved from no-self-use methods of AndroidCommands
# in console.py, consider remove them.


def eth_max_button_use_gas(gas_price, _coin=None):
    """
    The gas value when the max button is clicked, for eth only
    :param gas_price: gas price by coustomer
    :param coin: eth/bsc
    :return: gas info as string, unit is ether
    """
    return pywalib.PyWalib.get_max_use_gas(gas_price)


# #qr api
def get_raw_tx_from_qr_data(data):
    return util.bh2u(bitcoin.base_decode(data, None, base=43))


def get_qr_data_from_raw_tx(raw_tx):
    text = bitcoin.bfh(raw_tx)
    return bitcoin.base_encode(text, base=43)


def _on_callback(*args):
    util.print_stderr("[Callback] " + ", ".join(repr(x) for x in args))
