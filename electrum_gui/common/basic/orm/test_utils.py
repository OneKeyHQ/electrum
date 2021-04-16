from contextlib import contextmanager
from typing import Type

import peewee


@contextmanager
def test_database(*models: Type[peewee.Model]):
    test_db = peewee.SqliteDatabase(":memory:")

    with test_db.bind_ctx(models):
        test_db.create_tables(models)
        try:
            yield
        finally:
            test_db.drop_tables(models)
            test_db.close()


def cls_test_database(*models: Type[peewee.Model]):
    def decorator(the_class):
        @test_database(*models)
        def run(inner_self, *args, **kwargs):
            the_class.run(inner_self, *args, **kwargs)

        return type(the_class.__name__, (the_class,), {"run": run})

    return decorator
