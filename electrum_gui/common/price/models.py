import peewee

from electrum_gui.common.basic.orm.models import AutoDateTimeField, BaseModel
from electrum_gui.common.price.data import Channel


class Price(BaseModel):
    id = peewee.AutoField(primary_key=True)
    coin_code = peewee.CharField()
    price = peewee.DecimalField()
    unit = peewee.CharField()
    channel = peewee.IntegerField(choices=Channel.to_choices())
    created_time = AutoDateTimeField()
    modified_time = AutoDateTimeField()

    class Meta:
        indexes = ((("coin_code", "unit", "channel"), True),)

    def __str__(self):
        return (
            f"id: {self.id}, coin_code: {self.coin_code}, "
            f"price: {self.price}, unit: {self.unit}, channel: {self.channel}"
        )
