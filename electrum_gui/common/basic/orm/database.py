from peewee import SqliteDatabase

from electrum_gui.common.conf import settings

db = SqliteDatabase(settings.DATABASE["default"]["name"])
