import peewee

from electrum_gui.common.basic.orm.models import AutoDateTimeField, BaseModel
from electrum_gui.common.transaction.data import TxActionStatus


class TxAction(BaseModel):
    id = peewee.IntegerField(primary_key=True)
    txid = peewee.CharField()
    status = peewee.IntegerField(choices=TxActionStatus.to_choices())
    chain_code = peewee.CharField()
    coin_code = peewee.CharField()
    value = peewee.DecimalField(max_digits=32, decimal_places=0)
    decimals = peewee.IntegerField()
    symbol = peewee.CharField()
    from_address = peewee.CharField(index=True)
    to_address = peewee.CharField(index=True)
    fee_limit = peewee.DecimalField(max_digits=32, decimal_places=0)
    fee_used = peewee.DecimalField(max_digits=32, decimal_places=0, default=0)
    fee_price_per_unit = peewee.DecimalField(max_digits=32, decimal_places=0, default=1)
    raw_tx = peewee.TextField()
    block_number = peewee.IntegerField(null=True)
    block_hash = peewee.CharField(null=True)
    block_time = peewee.IntegerField(null=True)
    index = peewee.IntegerField(default=0, help_text="action index of the transaction")
    nonce = peewee.IntegerField(default=-1, help_text="a special field of the nonce model, likes eth")
    created_time = AutoDateTimeField()
    modified_time = AutoDateTimeField()

    def __str__(self):
        value_in_decimals = self.value / pow(10, self.decimals)
        return (
            f"id: {self.id}, coin_code: {self.coin_code}, "
            f"action: <from {self.from_address} to {self.to_address} for {value_in_decimals} {self.symbol}>, "
            f"status: {self.status}, txid: {self.txid}"
        )

    class Meta:
        indexes = ((("txid", "coin_code", "index"), True),)
