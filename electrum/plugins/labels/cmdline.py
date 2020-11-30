from .labels import LabelsPlugin
from electrum.plugin import hook

class Plugin(LabelsPlugin):

    @hook
    def load_wallet(self, wallet):
        self.start_wallet(wallet)

    def create_wallet(self, wallet, wallet_type, wallet_name):
        self.create_new_wallet(wallet, wallet_type, wallet_name)

    def on_pulled(self, wallet):
        self.logger.info('labels pulled from server')

    def ping_host(self):
        self.ping_server()