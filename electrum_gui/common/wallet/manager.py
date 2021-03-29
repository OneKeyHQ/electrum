from typing import Type

from electrum_gui.common.coin import manager as coin_manager
from electrum_gui.common.wallet import adapters
from electrum_gui.common.wallet.exceptions import AdapterClassNotFound
from electrum_gui.common.wallet.interfaces import WalletAdapterInterface

_ADAPTERS = {}


def _load_adapter_class(chain_code: str) -> Type[WalletAdapterInterface]:
    chain_info = coin_manager.get_chain_info(chain_code)
    prefix = chain_info.chain_affinity or chain_code
    class_name = f"{prefix.upper()}WalletAdapter"
    clazz = getattr(adapters, class_name, None)
    if clazz is None:
        raise AdapterClassNotFound(chain_code, class_name)
    return clazz


def get_adapter_by_chain(chain_code: str) -> WalletAdapterInterface:
    adapter = _ADAPTERS.get(chain_code)

    if adapter is None:
        adapter_class = _load_adapter_class(chain_code)
        adapter = adapter_class()
        _ADAPTERS[chain_code] = adapter

    return adapter
