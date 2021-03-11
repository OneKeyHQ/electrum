from dataclasses import fields

import peewee

from electrum_gui.common.basic.orm.models import AutoDateTimeField, BaseModel
from electrum_gui.common.coin.data import CoinInfo


class CoinModel(BaseModel):
    id = peewee.IntegerField(primary_key=True)
    code = peewee.CharField(unique=True)
    chain_code = peewee.CharField()
    name = peewee.CharField()
    symbol = peewee.CharField()
    decimals = peewee.IntegerField()
    icon = peewee.TextField(null=True)
    token_address = peewee.TextField(null=True)
    created_time = AutoDateTimeField()

    def to_dataclass(self) -> CoinInfo:
        field_names = {i.name for i in fields(CoinInfo)}
        data = {k: v for k, v in self.__data__.items() if k in field_names}
        return CoinInfo.from_dict(data)

    def __str__(self):
        return (
            f"id: {self.id}, chain_code: {self.chain_code}, "
            f"code: {self.code}, symbol: {self.symbol}, "
            f"decimals: {self.decimals}, token_address: {self.token_address}"
        )
