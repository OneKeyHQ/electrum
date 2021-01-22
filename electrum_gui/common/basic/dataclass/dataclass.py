import json
from dataclasses import replace, asdict, fields
from typing import Set
from decimal import Decimal

from electrum.util import DecimalEncoder


class DataClassMixin(object):
    def clone(self, **kwargs):
        return replace(self, **kwargs)

    @classmethod
    def from_dict(cls, data: dict):
        if not data:
            data = dict()

        return cls(**data)

    def to_dict(self) -> dict:
        return asdict(self)

    @classmethod
    def from_json(cls, json_str: str):
        data = json.loads(json_str) if json_str else None
        data = cls._solve_decimal_fields(data)
        return cls.from_dict(data)

    @classmethod
    def _load_decimal_fields(cls) -> Set[str]:
        cache_name = "__decimal_fields"
        decimal_fields = getattr(cls, cache_name, None)

        if decimal_fields is None:
            decimal_fields = {i.name for i in fields(cls) if i.type is Decimal}
            setattr(cls, cache_name, decimal_fields)

        return decimal_fields

    @classmethod
    def _solve_decimal_fields(cls, data: dict) -> dict:
        decimal_fields = cls._load_decimal_fields()

        if decimal_fields:
            data = {
                k: Decimal(v) if k in decimal_fields else v for k, v in data.items()
            }

        return data

    def to_json(self):
        return json.dumps(self.to_dict(), cls=DecimalEncoder)
