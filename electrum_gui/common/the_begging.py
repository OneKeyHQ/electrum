def initialize():
    import logging.config

    from electrum_gui.common.conf import settings

    # prepare logging
    logging.config.dictConfig(settings.LOGGING)

    # database migrating
    from electrum_gui.common.basic.orm.database import db
    from electrum_gui.common.basic.orm.migrate import manager

    manager.migrating(db)
