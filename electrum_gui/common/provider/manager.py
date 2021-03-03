import logging
import time
from typing import Any

from electrum_gui.common.provider import exceptions, registry
from electrum_gui.common.provider.interfaces import ProviderInterface

logger = logging.getLogger("app.provider")


_CACHE = dict()


def get_provider_by_chain(
    chain_code: str,
    force_update: bool = False,
    instance_required: Any = None,
) -> ProviderInterface:
    candidates = _CACHE.get(chain_code)

    if not candidates:
        candidates = [
            {"provider": provider, "is_ready": False, "expired_at": None}
            for provider in (registry.PROVIDERS.get(chain_code) or ())
        ]
        _CACHE[chain_code] = candidates

    if instance_required is not None:
        candidates = (i for i in candidates if isinstance(i.get("provider"), instance_required))

    for candidate in candidates:
        provider, is_ready, expired_at = (
            candidate["provider"],
            candidate["is_ready"],
            candidate["expired_at"],
        )

        if not force_update and expired_at and expired_at > time.time():
            if not is_ready:
                continue
            else:
                return provider

        try:
            is_ready, skip_seconds = provider.is_ready, 300
        except Exception as e:
            is_ready, skip_seconds = False, 180
            logger.info(f"Error in check status of <{candidate}>. error: {e}", exc_info=True)

        candidate.update({"expired_at": int(time.time() + skip_seconds), "is_ready": is_ready})

        if is_ready:
            return provider

    raise exceptions.NoAvailableProvider(chain_code, registry.PROVIDERS[chain_code], instance_required or "Any")
