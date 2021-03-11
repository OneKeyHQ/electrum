import peewee

from electrum_gui.common.basic.orm.models import AutoDateTimeField, BaseModel


def update(db: peewee.Database, migrator, migrate):
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

    db.create_tables((CoinModel,))
