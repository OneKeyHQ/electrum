import sqlite3
import time

class TxDb:
    def __init__(self, path=""):
        self.conn = sqlite3.connect(path)
        self.cursor = self.conn.cursor()
        self.create_db()

    def create_db(self):
        self.cursor.execute("CREATE TABLE IF NOT EXISTS txinfo (tx_hash TEXT PRIMARY KEY, address TEXT, psbt_tx TEXT, raw_tx Text, time INTEGER, faile_info TEXT)")

    def get_tx_info(self, address):
        self.cursor.execute("SELECT * FROM txinfo WHERE address=? ORDER BY time", (address,))
        result = self.cursor.fetchall()
        tx_list = []
        for info in result:
            tx_list.append(info)
        return tx_list

    def add_tx_info(self, address, psbt_tx, tx_hash, raw_tx="", failed_info=""):
        self.cursor.execute("INSERT OR IGNORE INTO txinfo VALUES(?, ?, ?, ?, ?, ?)",
                               (tx_hash, address, str(psbt_tx), str(raw_tx), time.time(), failed_info))
        self.conn.commit()

    # def update_tx_info(self, address, tx_hash="", psbt_tx="", raw_tx="", failed_info=""):
    #     self.cursor.execute("UPDATA txinfo SET failed_info=(?) WHERE tx_hash=?", (failed_info, tx_hash,))
    #     self.conn.commit()