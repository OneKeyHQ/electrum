from typing import Any, Dict, List, Optional, Tuple, Type, Union

import requests

from electrum_gui.common.basic import bip44
from electrum_gui.common.basic.functional.require import require
from electrum_gui.common.hardware import interfaces as hardware_interfaces
from electrum_gui.common.hardware import manager as hardware_manager
from electrum_gui.common.provider import data, exceptions, interfaces, loader
from electrum_gui.common.secret import interfaces as secret_interfaces


def get_best_block_number(chain_code: str) -> int:
    return loader.get_client_by_chain(chain_code).get_info().best_block_number


def get_address(chain_code: str, address: str) -> data.Address:
    return loader.get_client_by_chain(chain_code).get_address(address)


def batch_get_address(chain_code: str, addresses: List[str]) -> List[data.Address]:
    try:
        client = loader.get_client_by_chain(chain_code, instance_required=interfaces.BatchGetAddressMixin)
        return client.batch_get_address(addresses)
    except exceptions.NoAvailableClient:
        client = loader.get_client_by_chain(chain_code)
        return [client.get_address(i) for i in addresses]


def get_balance(chain_code: str, address: str, token_address: Optional[str] = None) -> int:
    # TODO: raise specific exceptions for callers to catch. This also applies
    # to the APIs in this module.
    return loader.get_client_by_chain(chain_code).get_balance(address, token_address=token_address)


def get_transaction_by_txid(chain_code: str, txid: str) -> data.Transaction:
    return loader.get_client_by_chain(chain_code).get_transaction_by_txid(txid)


def get_transaction_status(chain_code: str, txid: str) -> data.TransactionStatus:
    return loader.get_client_by_chain(chain_code).get_transaction_status(txid)


def search_txs_by_address(
    chain_code: str,
    address: str,
    paginate: Optional[data.TxPaginate] = None,
) -> List[data.Transaction]:
    try:
        return loader.get_client_by_chain(
            chain_code, instance_required=interfaces.SearchTransactionMixin
        ).search_txs_by_address(address, paginate=paginate)
    except exceptions.NoAvailableClient:
        return []


def search_txids_by_address(
    chain_code: str,
    address: str,
    paginate: Optional[data.TxPaginate] = None,
) -> List[str]:
    try:
        return loader.get_client_by_chain(
            chain_code, instance_required=interfaces.SearchTransactionMixin
        ).search_txids_by_address(address, paginate=paginate)
    except exceptions.NoAvailableClient:
        return []


def broadcast_transaction(chain_code: str, raw_tx: str) -> data.TxBroadcastReceipt:
    return loader.get_client_by_chain(chain_code).broadcast_transaction(raw_tx)


def get_prices_per_unit_of_fee(chain_code: str) -> data.PricesPerUnit:
    if chain_code == "eth":
        # Gasnow is now only for ETH, if this become common for different chains,
        # we can make it a provider extension then.
        try:
            resp = requests.get('https://www.gasnow.org/api/v3/gas/price?utm_source=onekey')
            gasnow_data = resp.json()["data"]
            return data.PricesPerUnit(
                normal=data.EstimatedTimeOnPrice(price=gasnow_data["standard"], time=180),
                others=[
                    data.EstimatedTimeOnPrice(price=gasnow_data["rapid"], time=15),
                    data.EstimatedTimeOnPrice(price=gasnow_data["fast"], time=60),
                    data.EstimatedTimeOnPrice(price=gasnow_data["slow"], time=600),
                ],
            )
        except Exception:
            # Avoid bandit try_except_pass
            return loader.get_client_by_chain(chain_code).get_prices_per_unit_of_fee()

    return loader.get_client_by_chain(chain_code).get_prices_per_unit_of_fee()


def verify_address(chain_code: str, address: str) -> data.AddressValidation:
    return loader.get_provider_by_chain(chain_code).verify_address(address)


def pubkey_to_address(chain_code: str, verifier: secret_interfaces.VerifierInterface, encoding: str = None) -> str:
    return loader.get_provider_by_chain(chain_code).pubkey_to_address(verifier, encoding=encoding)


def fill_unsigned_tx(chain_code: str, unsigned_tx: data.UnsignedTx) -> data.UnsignedTx:
    return loader.get_provider_by_chain(chain_code).fill_unsigned_tx(unsigned_tx)


def sign_transaction(
    chain_code: str, unsigned_tx: data.UnsignedTx, signers: Dict[str, secret_interfaces.SignerInterface]
) -> data.SignedTx:
    return loader.get_provider_by_chain(chain_code).sign_transaction(unsigned_tx, signers)


def utxo_can_spend(chain_code: str, utxo: data.UTXO) -> bool:
    return loader.get_client_by_chain(chain_code).utxo_can_spend(utxo)


def search_utxos_by_address(chain_code: str, address: str) -> List[data.UTXO]:
    return loader.get_client_by_chain(chain_code, instance_required=interfaces.SearchUTXOMixin).search_utxos_by_address(
        address
    )


def get_token_info_by_address(chain_code: str, token_address: str) -> Tuple[str, str, int]:
    return loader.get_provider_by_chain(chain_code).get_token_info_by_address(token_address)


def get_client_by_chain(chain_code: str, instance_required: Any = None) -> interfaces.ClientInterface:
    return loader.get_client_by_chain(chain_code, instance_required=instance_required)


def get_provider_by_chain(chain_code: str) -> interfaces.ProviderInterface:
    return loader.get_provider_by_chain(chain_code)


def _require_special_provider(chain_code: str, require_type: Type) -> Any:
    provider = loader.get_provider_by_chain(chain_code)
    require(
        isinstance(provider, require_type),
        NotImplementedError(f"{provider} not yet implemented {require_type}"),
    )
    return provider


def _ensure_provider_and_hardware_client(
    chain_code: str, hardware_device_path: str
) -> Tuple[interfaces.HardwareSupportingMixin, hardware_interfaces.HardwareClientInterface]:
    provider = _require_special_provider(chain_code, interfaces.HardwareSupportingMixin)
    hardware_client = hardware_manager.get_client(hardware_device_path)
    return provider, hardware_client


def _ensure_bip44_path(bip44_path: Union[str, bip44.BIP44Path]) -> bip44.BIP44Path:
    return bip44.BIP44Path.from_bip44_path(bip44_path) if isinstance(bip44_path, str) else bip44_path


def hardware_get_xpub(
    chain_code: str,
    hardware_device_path: str,
    bip44_path: Union[str, bip44.BIP44Path],
    confirm_on_device: bool = False,
) -> str:
    provider, hardware_client = _ensure_provider_and_hardware_client(chain_code, hardware_device_path)
    bip44_path = _ensure_bip44_path(bip44_path)
    return provider.hardware_get_xpub(hardware_client, bip44_path, confirm_on_device=confirm_on_device)


def hardware_get_address(
    chain_code: str,
    hardware_device_path: str,
    bip44_path: Union[str, bip44.BIP44Path],
    confirm_on_device: bool = False,
) -> str:
    provider, hardware_client = _ensure_provider_and_hardware_client(chain_code, hardware_device_path)
    bip44_path = _ensure_bip44_path(bip44_path)
    return provider.hardware_get_address(hardware_client, bip44_path, confirm_on_device=confirm_on_device)


def hardware_sign_transaction(
    chain_code: str,
    hardware_device_path: str,
    bip44_path_of_signers: Dict[str, Union[str, bip44.BIP44Path]],
    unsigned_tx: data.UnsignedTx,
) -> data.SignedTx:
    provider, hardware_client = _ensure_provider_and_hardware_client(chain_code, hardware_device_path)
    bip44_path_of_signers = {address: _ensure_bip44_path(path) for address, path in bip44_path_of_signers.items()}
    return provider.hardware_sign_transaction(hardware_client, bip44_path_of_signers, unsigned_tx)


def hardware_sign_message(
    chain_code: str,
    hardware_device_path: str,
    signer_bip44_path: Union[str, bip44.BIP44Path],
    message: str,
) -> str:
    provider, hardware_client = _ensure_provider_and_hardware_client(chain_code, hardware_device_path)
    signer_bip44_path = _ensure_bip44_path(signer_bip44_path)
    return provider.hardware_sign_message(hardware_client, signer_bip44_path, message)


def hardware_verify_message(
    chain_code: str,
    hardware_device_path: str,
    address: str,
    message: str,
    signature: str,
) -> bool:
    provider, hardware_client = _ensure_provider_and_hardware_client(chain_code, hardware_device_path)
    return provider.hardware_verify_message(hardware_client, address, message, signature)
