from os import path

from electrum_gui.common.conf.utils import get_data_dir

try:
    from electrum import constants

    IS_DEV = constants.net.TESTNET
except ImportError:
    IS_DEV = False

PROJECT_DIR = path.dirname(path.dirname(path.dirname(path.dirname(path.abspath(__file__)))))
DATA_DIR = get_data_dir()

LOGGING = {
    "version": 1,
    "disable_existing_loggers": False,
    "handlers": {
        'console': {
            'class': 'logging.StreamHandler',
            'level': 'DEBUG',
        },
    },
    "loggers": {
        "app": {
            'handlers': ['console'],
            'level': 'INFO',
            "propagate": False,
        }
    },
}

DATABASE = {
    "default": {
        "name": f"{DATA_DIR}/database.sqlite",
    },
}

DB_MODULES = [
    "electrum_gui.common.coin",
    "electrum_gui.common.price",
    "electrum_gui.common.transaction",
    "electrum_gui.common.secret",
    "electrum_gui.common.wallet",
]

# loading local_settings.py on project root
try:
    from local_settings import *  # noqa
except ImportError:
    pass
