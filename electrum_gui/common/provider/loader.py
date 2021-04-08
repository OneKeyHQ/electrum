import logging
import time
from functools import partial
from typing import Any, Iterable

from electrum_gui.common.coin import manager as coin_manager
from electrum_gui.common.provider import chains, exceptions
from electrum_gui.common.provider.interfaces import ClientInterface, ProviderInterface

logger = logging.getLogger("app.chain")


_CLIENTS = {}
_CANDIDATE_CLIENTS_CACHE = {}
_PROVIDERS = {}


def _load_clients_by_chain(chain_code: str) -> Iterable[ClientInterface]:
    clients = _CLIENTS.get(chain_code)

    if clients is not None:
        return clients

    chain_info = coin_manager.get_chain_info(chain_code)
    chain_affinity = chain_info.chain_affinity
    chain = getattr(chains, chain_affinity, object())
    client_classes = {
        i["class"]: getattr(chain, i["class"])
        for i in chain_info.clients
        if i.get("class") and hasattr(chain, i["class"]) and issubclass(getattr(chain, i["class"]), ClientInterface)
    }

    clients = []
    for config in chain_info.clients:
        class_name = config.get("class")
        if class_name not in client_classes:
            continue

        instantiate_params = dict(config)
        instantiate_params.pop("class")

        try:
            instance = client_classes[class_name](**instantiate_params)
            clients.append(instance)
        except Exception as e:
            logger.exception(
                f"Something wrong in creating the {chain_code} instance of <{class_name}> "
                f"with kwargs: ({instantiate_params}).",
                e,
            )

    _CLIENTS[chain_code] = clients
    return clients


def get_client_by_chain(
    chain_code: str,
    force_update: bool = False,
    instance_required: Any = None,
) -> Any:
    candidates = _CANDIDATE_CLIENTS_CACHE.get(chain_code)

    if not candidates:
        candidates = [
            {"client": client, "is_ready": False, "expired_at": None} for client in _load_clients_by_chain(chain_code)
        ]
        _CANDIDATE_CLIENTS_CACHE[chain_code] = candidates

    if instance_required is not None:
        candidates = [i for i in candidates if isinstance(i.get("client"), instance_required)]

    for candidate in candidates:
        client, is_ready, expired_at = (
            candidate["client"],
            candidate["is_ready"],
            candidate["expired_at"],
        )

        if not force_update and expired_at and expired_at > time.time():
            if not is_ready:
                continue
            else:
                return client

        try:
            is_ready, skip_seconds = client.is_ready, 300
        except Exception as e:
            is_ready, skip_seconds = False, 30
            logger.info(f"Error in check status of <{candidate}>. error: {e}", exc_info=True)

        candidate.update({"expired_at": int(time.time() + skip_seconds), "is_ready": is_ready})

        if is_ready:
            return client

    raise exceptions.NoAvailableClient(chain_code, candidates, instance_required or "Any")


def _load_provider(chain_code: str) -> ProviderInterface:
    chain_info = coin_manager.get_chain_info(chain_code)
    chain_affinity = chain_info.chain_affinity
    chain = getattr(chains, chain_affinity, object())
    provider_class_name = f"{chain_affinity.upper()}Provider"
    provider_class = getattr(chain, provider_class_name, None)
    if provider_class is None:
        raise exceptions.ProviderClassNotFound(chain_code, f"{chains.__name__}.{chain_affinity}.{provider_class_name}")

    provider = provider_class(
        chain_info,
        partial(coin_manager.get_coins_by_chain, chain_code),
        partial(get_client_by_chain, chain_code),
    )
    return provider


def get_provider_by_chain(chain_code: str) -> ProviderInterface:
    provider = _PROVIDERS.get(chain_code)

    if provider is None:
        provider = _load_provider(chain_code)
        _PROVIDERS[chain_code] = provider

    return provider
