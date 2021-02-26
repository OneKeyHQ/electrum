import json
import logging
import os
from typing import Dict, List

from electrum_gui.common.coin import codes
from electrum_gui.common.conf import settings
from electrum_gui.common.provider.clients import eth
from electrum_gui.common.provider.interfaces import ProviderInterface

logger = logging.getLogger("app.provider")

_mapping = {
    codes.ETH: eth,
    codes.TETH: eth,
    codes.BSC: eth,
    codes.TBSC: eth,
    codes.HECO: eth,
    codes.THECO: eth,
}


def _load_providers(providers_config_name: str) -> Dict[str, List[ProviderInterface]]:
    providers_config = json.loads(open(providers_config_name).read())
    providers = {}

    for chain_code, configs in providers_config.items():
        if chain_code not in _mapping:
            logger.warning(f"Please config chain_code<{chain_code}> to the mapping")
            continue

        module = _mapping[chain_code]
        providers[chain_code] = []
        configs = (i for i in configs if i.get("class"))

        for config in configs:
            clazz = config.pop("class")
            try:
                providers[chain_code].append(getattr(module, clazz)(**config))
            except Exception as e:
                logger.exception(
                    f"Something wrong in creating the {chain_code} instance of <{clazz}> with kwargs: ({config})."
                    f" error: {e}"
                )

    return providers


PROVIDERS = _load_providers(os.path.join(settings.PROJECT_DIR, "electrum_gui/common/provider/configs/providers.json"))
