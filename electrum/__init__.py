import os
import sys

# these are ~duplicated from run_electrum:
is_bundle = getattr(sys, "frozen", False)
is_local = not is_bundle and os.path.exists(
    os.path.join(os.path.dirname(os.path.dirname(__file__)), "electrum.desktop")
)

# when running from source, on Windows, also search for DLLs in inner 'electrum' folder
if is_local and os.name == "nt":
    if hasattr(os, "add_dll_directory"):  # requires python 3.8+
        os.add_dll_directory(os.path.dirname(__file__))


from . import bitcoin, daemon, transaction
from .coinchooser import COIN_CHOOSERS
from .commands import Commands, known_commands
from .interface import Interface
from .network import Network, pick_random_server
from .plugin import BasePlugin
from .simple_config import SimpleConfig
from .storage import WalletStorage
from .transaction import Transaction
from .util import format_satoshis
from .version import ELECTRUM_VERSION
from .wallet import Wallet

__version__ = ELECTRUM_VERSION
