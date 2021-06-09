import base64
from collections import OrderedDict

from electrum_gui.common.provider.chains.algo.sdk import constants, encoding, error, transaction


class SuggestedParams:
    """
    Contains various fields common to all transaction types.

    Args:
        fee (int): transaction fee (per byte)
        first (int): first round for which the transaction is valid
        last (int): last round for which the transaction is valid
        gh (str): genesis hash
        gen (str, optional): genesis id
        flat_fee (bool, optional): whether the specified fee is a flat fee
        consensus_version (str, optional): the consensus protocol version as of 'first'
        min_fee (int, optional): the minimum transaction fee (flat)

    Attributes:
        fee (int)
        first (int)
        last (int)
        gen (str)
        gh (str)
        flat_fee (bool)
        consensus_version (str)
        min_fee (int)
    """

    def __init__(self, fee, first, last, gh, gen=None, flat_fee=False, consensus_version=None, min_fee=None):
        self.first = first
        self.last = last
        self.gh = gh
        self.gen = gen
        self.fee = fee
        self.flat_fee = flat_fee
        self.consensus_version = consensus_version
        self.min_fee = min_fee


class Transaction:
    """
    Superclass for various transaction types.
    """

    def __init__(self, sender, sp, note, lease, txn_type, rekey_to):
        self.sender = sender
        self.fee = sp.fee
        self.first_valid_round = sp.first
        self.last_valid_round = sp.last
        self.note = self.as_note(note)
        self.genesis_id = sp.gen
        self.genesis_hash = sp.gh
        self.group = None
        self.lease = self.as_lease(lease)
        self.type = txn_type
        self.rekey_to = rekey_to

    @staticmethod
    def as_hash(hash):
        """Confirm that a value is 32 bytes. If all zeros, or a falsy value, return None"""
        if not hash:
            return None
        assert isinstance(hash, (bytes, bytearray)), f"{hash} is not bytes"
        if len(hash) != constants.hash_len:
            raise error.WrongHashLengthError
        if not any(hash):
            return None
        return hash

    @staticmethod
    def as_note(note):
        if not note:
            return None
        if not isinstance(note, (bytes, bytearray, str)):
            raise error.WrongNoteType
        if isinstance(note, str):
            note = note.encode()
        if len(note) > constants.note_max_length:
            raise error.WrongNoteLength
        return note

    @classmethod
    def as_lease(cls, lease):
        try:
            return cls.as_hash(lease)
        except error.WrongHashLengthError:
            raise error.WrongLeaseLengthError

    def get_txid(self):
        """
        Get the transaction's ID.

        Returns:
            str: transaction ID
        """
        txn = encoding.msgpack_encode(self)
        to_sign = constants.txid_prefix + base64.b64decode(txn)
        txid = encoding.checksum(to_sign)
        txid = base64.b32encode(txid).decode()
        return encoding.undo_padding(txid)

    def dictify(self):
        d = dict()
        d["fee"] = self.fee
        if self.first_valid_round:
            d["fv"] = self.first_valid_round
        if self.genesis_id:
            d["gen"] = self.genesis_id
        d["gh"] = base64.b64decode(self.genesis_hash)
        if self.group:
            d["grp"] = self.group
        d["lv"] = self.last_valid_round
        if self.lease:
            d["lx"] = self.lease
        if self.note:
            d["note"] = self.note
        d["snd"] = encoding.decode_address(self.sender)
        d["type"] = self.type
        if self.rekey_to:
            d["rekey"] = encoding.decode_address(self.rekey_to)

        return d

    @staticmethod
    def undictify(d):
        sp = SuggestedParams(
            d["fee"],
            d["fv"] if "fv" in d else 0,
            d["lv"],
            base64.b64encode(d["gh"]).decode(),
            d["gen"] if "gen" in d else None,
            flat_fee=True,
        )
        args = {
            "sp": sp,
            "sender": encoding.encode_address(d["snd"]),
            "note": d["note"] if "note" in d else None,
            "lease": d["lx"] if "lx" in d else None,
            "rekey_to": encoding.encode_address(d["rekey"]) if "rekey" in d else None,
        }
        txn_type = d["type"]
        if not isinstance(d["type"], str):
            txn_type = txn_type.decode()
        if txn_type == constants.payment_txn:
            args.update(PaymentTxn.undictify(d))
            txn = PaymentTxn(**args)
        elif txn_type == constants.assetconfig_txn:
            args.update(AssetConfigTxn.undictify(d))
            txn = AssetConfigTxn(**args)
        elif txn_type == constants.assettransfer_txn:
            args.update(AssetTransferTxn.undictify(d))
            txn = AssetTransferTxn(**args)
        if "grp" in d:
            txn.group = d["grp"]
        return txn

    def __eq__(self, other):
        if not isinstance(other, (Transaction, transaction.Transaction)):
            return False
        return (
            self.sender == other.sender
            and self.fee == other.fee
            and self.first_valid_round == other.first_valid_round
            and self.last_valid_round == other.last_valid_round
            and self.genesis_hash == other.genesis_hash
            and self.genesis_id == other.genesis_id
            and self.note == other.note
            and self.group == other.group
            and self.lease == other.lease
            and self.type == other.type
            and self.rekey_to == other.rekey_to
        )

    @staticmethod
    def required(arg):
        if not arg:
            raise ValueError(f"{arg} supplied as a required argument")
        return arg

    @staticmethod
    def creatable_index(index, required=False):
        """Coerce an index for apps or assets to an integer.

        By using this in all constructors, we allow callers to use
        strings as indexes, check our convenience Txn types to ensure
        index is set, and ensure that 0 is always used internally for
        an unset id, not None, so __eq__ works properly.
        """
        i = int(index or 0)
        if i == 0 and required:
            raise IndexError("Required an index")
        if i < 0:
            raise IndexError(i)
        return i

    def __str__(self):
        return str(self.__dict__)


class PaymentTxn(Transaction):
    """
    Represents a payment transaction.

    Args:
        sender (str): address of the sender
        sp (SuggestedParams): suggested params from algod
        receiver (str): address of the receiver
        amt (int): amount in microAlgos to be sent
        close_remainder_to (str, optional): if nonempty, account will be closed
            and remaining algos will be sent to this address
        note (bytes, optional): arbitrary optional bytes
        lease (byte[32], optional): specifies a lease, and no other transaction
            with the same sender and lease can be confirmed in this
            transaction's valid rounds
        rekey_to (str, optional): additionally rekey the sender to this address

    Attributes:
        sender (str)
        fee (int)
        first_valid_round (int)
        last_valid_round (int)
        note (bytes)
        genesis_id (str)
        genesis_hash (str)
        group (bytes)
        receiver (str)
        amt (int)
        close_remainder_to (str)
        type (str)
        lease (byte[32])
        rekey_to (str)
    """

    def __init__(self, sender, sp, receiver, amt, close_remainder_to=None, note=None, lease=None, rekey_to=None):
        Transaction.__init__(self, sender, sp, note, lease, constants.payment_txn, rekey_to)
        if receiver:
            self.receiver = receiver
        else:
            raise error.ZeroAddressError

        self.amt = amt
        if (not isinstance(self.amt, int)) or self.amt < 0:
            raise error.WrongAmountType
        self.close_remainder_to = close_remainder_to
        if sp.flat_fee:
            self.fee = max(constants.min_txn_fee, self.fee)
        else:
            raise error.EstimateSizeError

    def dictify(self):
        d = dict()
        if self.amt:
            d["amt"] = self.amt
        if self.close_remainder_to:
            d["close"] = encoding.decode_address(self.close_remainder_to)

        decoded_receiver = encoding.decode_address(self.receiver)
        if any(decoded_receiver):
            d["rcv"] = encoding.decode_address(self.receiver)

        d.update(super(PaymentTxn, self).dictify())
        od = OrderedDict(sorted(d.items()))

        return od

    @staticmethod
    def undictify(d):
        args = {
            "close_remainder_to": encoding.encode_address(d["close"]) if "close" in d else None,
            "amt": d["amt"] if "amt" in d else 0,
            "receiver": encoding.encode_address(d["rcv"]) if "rcv" in d else None,
        }
        return args

    def __eq__(self, other):
        if not isinstance(other, (PaymentTxn, transaction.PaymentTxn)):
            return False
        return (
            super(PaymentTxn, self).__eq__(other)
            and self.receiver == other.receiver
            and self.amt == other.amt
            and self.close_remainder_to == other.close_remainder_to
        )


class AssetConfigTxn(Transaction):
    """
    Represents a transaction for asset creation, reconfiguration, or
    destruction.

    To create an asset, include the following:
        total, default_frozen, unit_name, asset_name,
        manager, reserve, freeze, clawback, url, metadata,
        decimals

    To destroy an asset, include the following:
        index, strict_empty_address_check (set to False)

    To update asset configuration, include the following:
        index, manager, reserve, freeze, clawback,
        strict_empty_address_check (optional)

    Args:
        sender (str): address of the sender
        sp (SuggestedParams): suggested params from algod
        index (int, optional): index of the asset
        total (int, optional): total number of base units of this asset created
        default_frozen (bool, optional): whether slots for this asset in user
            accounts are frozen by default
        unit_name (str, optional): hint for the name of a unit of this asset
        asset_name (str, optional): hint for the name of the asset
        manager (str, optional): address allowed to change nonzero addresses
            for this asset
        reserve (str, optional): account whose holdings of this asset should
            be reported as "not minted"
        freeze (str, optional): account allowed to change frozen state of
            holdings of this asset
        clawback (str, optional): account allowed take units of this asset
            from any account
        url (str, optional): a URL where more information about the asset
            can be retrieved
        metadata_hash (byte[32], optional): a commitment to some unspecified
            asset metadata (32 byte hash)
        note (bytes, optional): arbitrary optional bytes
        lease (byte[32], optional): specifies a lease, and no other transaction
            with the same sender and lease can be confirmed in this
            transaction's valid rounds
        strict_empty_address_check (bool, optional): set this to False if you
            want to specify empty addresses. Otherwise, if this is left as
            True (the default), having empty addresses will raise an error,
            which will prevent accidentally removing admin access to assets or
            deleting the asset.
        decimals (int, optional): number of digits to use for display after
            decimal. If set to 0, the asset is not divisible. If set to 1, the
            base unit of the asset is in tenths. Must be between 0 and 19,
            inclusive. Defaults to 0.
        rekey_to (str, optional): additionally rekey the sender to this address

    Attributes:
        sender (str)
        fee (int)
        first_valid_round (int)
        last_valid_round (int)
        genesis_hash (str)
        index (int)
        total (int)
        default_frozen (bool)
        unit_name (str)
        asset_name (str)
        manager (str)
        reserve (str)
        freeze (str)
        clawback (str)
        url (str)
        metadata_hash (byte[32])
        note (bytes)
        genesis_id (str)
        type (str)
        lease (byte[32])
        decimals (int)
        rekey (str)
    """

    def __init__(
        self,
        sender,
        sp,
        index=None,
        total=None,
        default_frozen=None,
        unit_name=None,
        asset_name=None,
        manager=None,
        reserve=None,
        freeze=None,
        clawback=None,
        url=None,
        metadata_hash=None,
        note=None,
        lease=None,
        strict_empty_address_check=True,
        decimals=0,
        rekey_to=None,
    ):
        Transaction.__init__(self, sender, sp, note, lease, constants.assetconfig_txn, rekey_to)
        if strict_empty_address_check:
            if not (manager and reserve and freeze and clawback):
                raise error.EmptyAddressError
        self.index = self.creatable_index(index)
        self.total = int(total) if total else None
        self.default_frozen = bool(default_frozen)
        self.unit_name = unit_name
        self.asset_name = asset_name
        self.manager = manager
        self.reserve = reserve
        self.freeze = freeze
        self.clawback = clawback
        self.url = url
        self.metadata_hash = self.as_metadata(metadata_hash)
        self.decimals = int(decimals)
        if self.decimals < 0 or self.decimals > constants.max_asset_decimals:
            raise error.OutOfRangeDecimalsError
        if sp.flat_fee:
            self.fee = max(constants.min_txn_fee, self.fee)
        else:
            raise error.EstimateSizeError

    def dictify(self):
        d = dict()

        if (
            self.total
            or self.default_frozen
            or self.unit_name
            or self.asset_name
            or self.manager
            or self.reserve
            or self.freeze
            or self.clawback
            or self.decimals
        ):
            apar = OrderedDict()
            if self.metadata_hash:
                apar["am"] = self.metadata_hash
            if self.asset_name:
                apar["an"] = self.asset_name
            if self.url:
                apar["au"] = self.url
            if self.clawback:
                apar["c"] = encoding.decode_address(self.clawback)
            if self.decimals:
                apar["dc"] = self.decimals
            if self.default_frozen:
                apar["df"] = self.default_frozen
            if self.freeze:
                apar["f"] = encoding.decode_address(self.freeze)
            if self.manager:
                apar["m"] = encoding.decode_address(self.manager)
            if self.reserve:
                apar["r"] = encoding.decode_address(self.reserve)
            if self.total:
                apar["t"] = self.total
            if self.unit_name:
                apar["un"] = self.unit_name
            d["apar"] = apar

        if self.index:
            d["caid"] = self.index

        d.update(super(AssetConfigTxn, self).dictify())
        od = OrderedDict(sorted(d.items()))

        return od

    @staticmethod
    def undictify(d):
        index = None
        total = None
        default_frozen = None
        unit_name = None
        asset_name = None
        manager = None
        reserve = None
        freeze = None
        clawback = None
        url = None
        metadata_hash = None
        decimals = 0

        if "caid" in d:
            index = d["caid"]
        if "apar" in d:
            if "t" in d["apar"]:
                total = d["apar"]["t"]
            if "df" in d["apar"]:
                default_frozen = d["apar"]["df"]
            if "un" in d["apar"]:
                unit_name = d["apar"]["un"]
            if "an" in d["apar"]:
                asset_name = d["apar"]["an"]
            if "m" in d["apar"]:
                manager = encoding.encode_address(d["apar"]["m"])
            if "r" in d["apar"]:
                reserve = encoding.encode_address(d["apar"]["r"])
            if "f" in d["apar"]:
                freeze = encoding.encode_address(d["apar"]["f"])
            if "c" in d["apar"]:
                clawback = encoding.encode_address(d["apar"]["c"])
            if "au" in d["apar"]:
                url = d["apar"]["au"]
            if "am" in d["apar"]:
                metadata_hash = d["apar"]["am"]
            if "dc" in d["apar"]:
                decimals = d["apar"]["dc"]

        args = {
            "index": index,
            "total": total,
            "default_frozen": default_frozen,
            "unit_name": unit_name,
            "asset_name": asset_name,
            "manager": manager,
            "reserve": reserve,
            "freeze": freeze,
            "clawback": clawback,
            "url": url,
            "metadata_hash": metadata_hash,
            "strict_empty_address_check": False,
            "decimals": decimals,
        }

        return args

    def __eq__(self, other):
        if not isinstance(other, (AssetConfigTxn, transaction.AssetConfigTxn)):
            return False
        return (
            super(AssetConfigTxn, self).__eq__(other)
            and self.index == other.index
            and self.total == other.total
            and self.default_frozen == other.default_frozen
            and self.unit_name == other.unit_name
            and self.asset_name == other.asset_name
            and self.manager == other.manager
            and self.reserve == other.reserve
            and self.freeze == other.freeze
            and self.clawback == other.clawback
            and self.url == other.url
            and self.metadata_hash == other.metadata_hash
            and self.decimals == other.decimals
        )

    @classmethod
    def as_metadata(cls, md):
        try:
            return cls.as_hash(md)
        except error.WrongHashLengthError:
            raise error.WrongMetadataLengthError


class AssetTransferTxn(Transaction):
    """
    Represents a transaction for asset transfer.

    To begin accepting an asset, supply the same address as both sender and
    receiver, and set amount to 0 (or use AssetOptInTxn)

    To revoke an asset, set revocation_target, and issue the transaction from
    the asset's revocation manager account.

    Args:
        sender (str): address of the sender
        sp (SuggestedParams): suggested params from algod
        receiver (str): address of the receiver
        amt (int): amount of asset base units to send
        index (int): index of the asset
        close_assets_to (string, optional): send all of sender's remaining
            assets, after paying `amt` to receiver, to this address
        revocation_target (string, optional): send assets from this address,
            rather than the sender's address (can only be used by an asset's
            revocation manager, also known as clawback)
        note (bytes, optional): arbitrary optional bytes
        lease (byte[32], optional): specifies a lease, and no other transaction
            with the same sender and lease can be confirmed in this
            transaction's valid rounds
        rekey_to (str, optional): additionally rekey the sender to this address

    Attributes:
        sender (str)
        fee (int)
        first_valid_round (int)
        last_valid_round (int)
        genesis_hash (str)
        index (int)
        amount (int)
        receiver (string)
        close_assets_to (string)
        revocation_target (string)
        note (bytes)
        genesis_id (str)
        type (str)
        lease (byte[32])
        rekey_to (str)
    """

    def __init__(
        self,
        sender,
        sp,
        receiver,
        amt,
        index,
        close_assets_to=None,
        revocation_target=None,
        note=None,
        lease=None,
        rekey_to=None,
    ):
        Transaction.__init__(self, sender, sp, note, lease, constants.assettransfer_txn, rekey_to)
        if receiver:
            self.receiver = receiver
        else:
            raise error.ZeroAddressError
        self.amount = amt
        if (not isinstance(self.amount, int)) or self.amount < 0:
            raise error.WrongAmountType
        self.index = self.creatable_index(index, required=True)
        self.close_assets_to = close_assets_to
        self.revocation_target = revocation_target
        if sp.flat_fee:
            self.fee = max(constants.min_txn_fee, self.fee)
        else:
            raise error.EstimateSizeError

    def dictify(self):
        d = dict()

        if self.amount:
            d["aamt"] = self.amount
        if self.close_assets_to:
            d["aclose"] = encoding.decode_address(self.close_assets_to)

        decoded_receiver = encoding.decode_address(self.receiver)
        if any(decoded_receiver):
            d["arcv"] = encoding.decode_address(self.receiver)
        if self.revocation_target:
            d["asnd"] = encoding.decode_address(self.revocation_target)

        if self.index:
            d["xaid"] = self.index

        d.update(super(AssetTransferTxn, self).dictify())
        od = OrderedDict(sorted(d.items()))

        return od

    @staticmethod
    def undictify(d):
        args = {
            "receiver": encoding.encode_address(d["arcv"]) if "arcv" in d else None,
            "amt": d["aamt"] if "aamt" in d else 0,
            "index": d["xaid"] if "xaid" in d else None,
            "close_assets_to": encoding.encode_address(d["aclose"]) if "aclose" in d else None,
            "revocation_target": encoding.encode_address(d["asnd"]) if "asnd" in d else None,
        }

        return args

    def __eq__(self, other):
        if not isinstance(other, (AssetTransferTxn, transaction.AssetTransferTxn)):
            return False
        return (
            super(AssetTransferTxn, self).__eq__(other)
            and self.index == other.index
            and self.amount == other.amount
            and self.receiver == other.receiver
            and self.close_assets_to == other.close_assets_to
            and self.revocation_target == other.revocation_target
        )


class SignedTransaction:
    """
    Represents a signed transaction.

    Args:
        transaction (Transaction): transaction that was signed
        signature (str): signature of a single address
        authorizing_address (str, optional): the address authorizing the signed transaction, if different from sender

    Attributes:
        transaction (Transaction)
        signature (str)
        authorizing_address (str)
    """

    def __init__(self, transaction, signature, authorizing_address=None):
        self.signature = signature
        self.transaction = transaction
        self.authorizing_address = authorizing_address

    def get_txid(self):
        """
        Get the transaction's ID.

        Returns:
            str: transaction ID
        """
        return self.transaction.get_txid()

    def dictify(self):
        od = OrderedDict()
        if self.signature:
            od["sig"] = base64.b64decode(self.signature)
        od["txn"] = self.transaction.dictify()
        if self.authorizing_address:
            od["sgnr"] = encoding.decode_address(self.authorizing_address)
        return od

    @staticmethod
    def undictify(d):
        sig = None
        if "sig" in d:
            sig = base64.b64encode(d["sig"]).decode()
        auth = None
        if "sgnr" in d:
            auth = encoding.encode_address(d["sgnr"])
        txn = Transaction.undictify(d["txn"])
        stx = SignedTransaction(txn, sig, auth)
        return stx

    def __eq__(self, other):
        if not isinstance(other, (SignedTransaction, transaction.SignedTransaction)):
            return False
        return (
            self.transaction == other.transaction
            and self.signature == other.signature
            and self.authorizing_address == other.authorizing_address
        )
