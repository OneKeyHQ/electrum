import sqlite3
import time

class TxDb:
    def __init__(self, path=""):
        self.path = path

    ## API for failed tx
    @staticmethod
    def create_table(cursor):
        cursor.execute(
            "CREATE TABLE IF NOT EXISTS txinfo (tx_hash TEXT PRIMARY KEY, address TEXT, psbt_tx TEXT, raw_tx Text, time INTEGER, faile_info TEXT)")

    def get_tx_info(self, address):
        conn = None
        cursor = None
        try:
            conn = sqlite3.connect(self.path)
            cursor = conn.cursor()
            self.create_table(cursor)
            cursor.execute("SELECT * FROM txinfo WHERE address=? ORDER BY time", (address,))
            result = cursor.fetchall()
            tx_list = []
            for info in result:
                tx_list.append(info)
            return tx_list
        except Exception as e:
            raise e
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()

    def add_tx_info(self, address, psbt_tx, tx_hash, raw_tx="", failed_info=""):
        conn = None
        cursor = None
        try:
            conn = sqlite3.connect(self.path)
            cursor = conn.cursor()
            self.create_table(cursor)
            cursor.execute("INSERT OR IGNORE INTO txinfo VALUES(?, ?, ?, ?, ?, ?)",
                           (tx_hash, address, str(psbt_tx), str(raw_tx), time.time(), failed_info))
            conn.commit()
        except Exception as e:
            e.__repr__()
            raise e
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()

    ### API for tx time
    def create_save_time_table(self, cursor):
        cursor.execute(
            "CREATE TABLE IF NOT EXISTS txtimeinfo (tx_hash TEXT PRIMARY KEY, time INTEGER)")

    def get_tx_time_info(self, tx_hash):
        conn = None
        cursor = None
        try:
            conn = sqlite3.connect(self.path)
            cursor = conn.cursor()
            self.create_save_time_table(cursor)
            cursor.execute("SELECT * FROM txtimeinfo WHERE tx_hash=?", (tx_hash,))
            result = cursor.fetchall()
            tx_list = []
            for info in result:
                tx_list.append(info)
            return tx_list
        except Exception as e:
            raise e
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()

    def add_tx_time_info(self, tx_hash):
        conn = None
        cursor = None
        try:
            conn = sqlite3.connect(self.path)
            cursor = conn.cursor()
            self.create_save_time_table(cursor)
            cursor.execute("INSERT OR IGNORE INTO txtimeinfo VALUES(?, ?)",
                           (tx_hash, time.time()))
            conn.commit()
        except Exception as e:
            e.__repr__()
            raise e
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()
