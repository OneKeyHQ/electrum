import peewee

from electrum_gui.common.basic.orm.models import AutoDateTimeField, BaseModel
from electrum_gui.common.secret.data import CurveEnum, PubKeyType, SecretKeyType


class PubKeyModel(BaseModel):
    id = peewee.AutoField(primary_key=True)
    curve = peewee.IntegerField(choices=CurveEnum.to_choices())
    pubkey_type = peewee.IntegerField(choices=PubKeyType.to_choices())
    pubkey = peewee.CharField()
    path = peewee.CharField(null=True)
    secret_key_id = peewee.IntegerField(null=True)
    parent_pubkey_id = peewee.IntegerField(null=True)
    created_time = AutoDateTimeField()

    def __str__(self):
        return (
            f"id: {self.id}, curve: {self.curve}, pubkey_type: {self.pubkey_type}, "
            f"pubkey: {self.pubkey}, path: {self.path}"
        )


class SecretKeyModel(BaseModel):
    id = peewee.AutoField(primary_key=True)
    secret_key_type = peewee.IntegerField(choices=SecretKeyType.to_choices())
    encrypted_secret_key = peewee.CharField(help_text="Encrypted secret key")
    created_time = AutoDateTimeField()

    def __str__(self):
        return (
            f"id: {self.id}, secret_key_type: {self.secret_key_type}, encrypted_secret_key: {self.encrypted_secret_key}"
        )
