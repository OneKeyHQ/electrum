import logging
import time

from electrum_gui.common.provider import exceptions, registry
from electrum_gui.common.provider.interfaces import ProviderInterface

logger = logging.getLogger("app.provider")

_CACHE = {}


def get_provider_by_chain(chain_code: str, force_update: bool = False) -> ProviderInterface:
    provider, expired_at = _CACHE.get(chain_code) or (None, None)

    if not force_update and provider and expired_at and expired_at > time.time():
        return provider

    _CACHE.pop(chain_code, None)

    if chain_code not in registry.PROVIDERS:
        raise exceptions.ProviderNotFound(chain_code)

    for candidate in registry.PROVIDERS[chain_code]:
        try:
            if candidate.is_ready:
                _CACHE[chain_code] = (candidate, int(time.time() + 300))  # expired after 5 min
                return candidate
        except Exception as e:
            logger.debug(f"Error in check status of <{candidate}>. error: {e}", exc_info=True)

    raise exceptions.ProvidersAllDown(chain_code, registry.PROVIDERS[chain_code])
