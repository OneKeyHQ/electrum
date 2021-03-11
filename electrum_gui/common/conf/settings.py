from os import path

from electrum_gui.common.coin import codes
from electrum_gui.common.conf.utils import get_data_dir

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
]

ENABLED_CHAIN_COINS = [
    codes.ETH,
    codes.BSC,
    codes.HECO,
]  # TODO enable specific test coin on dev env?

# loading local_settings.py on project root
try:
    from local_settings import *  # noqa                                                                                                                               |~
except ImportError:
    pass
