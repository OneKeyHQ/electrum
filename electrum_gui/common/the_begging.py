def initialize():
    import logging.config

    from electrum_gui.common.conf import settings

    # prepare logging
    logging.config.dictConfig(settings.LOGGING)

    # database migrating
    from electrum_gui.common.basic.orm.database import db
    from electrum_gui.common.basic.orm.migrate import manager

    manager.migrating(db)

    # from electrum_gui.common.basic import ticker

    # connect ticker signal here before start
    # example ticker.signals.ticker_signal.connect(my_ticker_callback)
    # start ticker
    # ticker.start_default_ticker(seconds=60)  # every 1 min
