from __future__ import absolute_import

import sys
import warnings

import pkg_resources
from eth_keyfile.keyfile import create_keyfile_json  # noqa: F401
from eth_keyfile.keyfile import (
    decode_keyfile_json,
    extract_key_from_keyfile,
    load_keyfile,
)

if sys.version_info.major < 3:
    warnings.simplefilter("always", DeprecationWarning)
    warnings.warn(
        DeprecationWarning(
            "The `eth-keyfile` library is dropping support for Python 2.  Upgrade to Python 3."
        )
    )
    warnings.resetwarnings()


__version__ = pkg_resources.get_distribution("eth-keyfile").version
