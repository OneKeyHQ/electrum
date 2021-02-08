import importlib
import logging
import pkgutil
from collections import defaultdict, namedtuple
from types import ModuleType
from typing import Iterable, List

from peewee import Database
from playhouse.migrate import SqliteMigrator, migrate

from electrum_gui.common.basic.orm.exceptions import BadMigrationException
from electrum_gui.common.basic.orm.migrate.models import MigrationRecord
from electrum_gui.common.conf import settings

logger = logging.getLogger("app.migrating")


MIGRATION_MODULE_NAME = "migrations"
MIGRATION_FUNC_NAME = "update"

_Migration = namedtuple("Migration", ["module", "name", "update_func"])


def _load_migration_modules() -> Iterable[ModuleType]:
    for module_path in settings.DB_MODULES:
        module = importlib.import_module(f"{module_path}.{MIGRATION_MODULE_NAME}")
        if getattr(
            module, "__file__", None  # Empty directories are namespaces, like (dir without __init__.py)
        ) and getattr(
            module, "__path__", None  # Module is a package
        ):
            yield module


def _load_migrations_by_module(module: ModuleType) -> Iterable[_Migration]:
    assert module.__name__.endswith(f".{MIGRATION_MODULE_NAME}")
    module_name = module.__name__.split(".")[-2]

    migration_names = {
        name for _, name, is_pkg in pkgutil.iter_modules(module.__path__) if not is_pkg and not name.startswith("_")
    }
    migration_names = sorted(migration_names)

    for migration_name in migration_names:
        migration_path = f"{module.__name__}.{migration_name}"
        migration_module = importlib.import_module(migration_path)
        migration_func = getattr(migration_module, MIGRATION_FUNC_NAME, None)

        if not migration_func:
            raise BadMigrationException(
                f"No {repr(MIGRATION_FUNC_NAME)} function found from {migration_module.__file__}"
            )

        yield _Migration(module=module_name, name=migration_name, update_func=migration_func)


def _load_needed_migrations() -> List[_Migration]:
    migrated_records = defaultdict(set)

    for i in MigrationRecord.select():
        migrated_records[i.module].add(i.name)

    migrations = []
    for module in _load_migration_modules():
        for migration in _load_migrations_by_module(module):
            if migration.name not in migrated_records[migration.module]:
                migrations.append(migration)

    return migrations


def migrating(database: Database):
    logger.info("Start migrating...")

    migrator = SqliteMigrator(database)
    database.create_tables((MigrationRecord,))

    all_module_names = (i.split(".")[-1] for i in settings.DB_MODULES)
    logger.info(f"Apply all migrations: {', '.join(all_module_names)}")

    migrations = _load_needed_migrations()

    if not migrations:
        logger.info("No migrations to apply.")
        return

    for migration in migrations:
        migration_path = f"{migration.module}.{MIGRATION_MODULE_NAME}.{migration.name}"

        try:
            with database.atomic():
                migration.update_func(database, migrator, migrate)
                MigrationRecord.create(module=migration.module, name=migration.name)

            logger.info(f"Apply {migration_path} success.")
        except Exception as e:
            logger.exception(f"Apply {migration_path} failed. exception: {e}")
            raise

    logger.info("All migrations applied.")
