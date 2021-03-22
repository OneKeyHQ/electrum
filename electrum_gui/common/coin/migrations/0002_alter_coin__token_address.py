import peewee


def update(db, migrator, migrate):
    migrate(
        migrator.alter_column_type("coinmodel", "token_address", peewee.TextField(null=True, collation="NOCASE")),
    )
