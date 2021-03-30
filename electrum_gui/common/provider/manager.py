import logging
import time
from typing import Any, Iterable

from electrum_gui.common.coin import manager as coin_manager
from electrum_gui.common.provider import clients, exceptions
from electrum_gui.common.provider.interfaces import ProviderInterface

logger = logging.getLogger("app.provider")


_PROVIDERS = {}
_CACHE = {}


def _load_providers_by_chain(chain_code: str) -> Iterable[ProviderInterface]:
    providers = _PROVIDERS.get(chain_code)

    if providers is not None:
        return providers

    chain_info = coin_manager.get_chain_info(chain_code)
    holder_name = chain_info.chain_affinity or chain_code
    holder = getattr(clients, holder_name, object())
    provider_classes = {
        i["class"]: getattr(holder, i["class"])
        for i in chain_info.providers
        if i.get("class") and hasattr(holder, i["class"]) and issubclass(getattr(holder, i["class"]), ProviderInterface)
    }

    providers = []
    for config in chain_info.providers:
        class_name = config.get("class")
        if class_name not in provider_classes:
            continue

        instantiate_params = dict(config)
        instantiate_params.pop("class")

        try:
            instance = provider_classes[class_name](**instantiate_params)
            providers.append(instance)
        except Exception as e:
            logger.exception(
                f"Something wrong in creating the {chain_code} instance of <{class_name}> "
                f"with kwargs: ({instantiate_params}).",
                e,
            )

    _PROVIDERS[chain_code] = providers
    return providers


def get_provider_by_chain(
    chain_code: str,
    force_update: bool = False,
    instance_required: Any = None,
) -> ProviderInterface:
    candidates = _CACHE.get(chain_code)

    if not candidates:
        candidates = [
            {"provider": provider, "is_ready": False, "expired_at": None}
            for provider in _load_providers_by_chain(chain_code)
        ]
        _CACHE[chain_code] = candidates

    if instance_required is not None:
        candidates = [i for i in candidates if isinstance(i.get("provider"), instance_required)]

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

    raise exceptions.NoAvailableProvider(chain_code, candidates, instance_required or "Any")
