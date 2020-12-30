import sqlite3
import time

class TxDb:
    conn = None
    cursor = None
    path = None
    def __init__(self, path=""):
        TxDb.path = path

    @staticmethod
    def create_table(cursor):
        cursor.execute(
            "CREATE TABLE IF NOT EXISTS txinfo (tx_hash TEXT PRIMARY KEY, address TEXT, psbt_tx TEXT, raw_tx Text, time INTEGER, faile_info TEXT)")

    def create_save_time_table(self, cursor):
        cursor.execute(
            "CREATE TABLE IF NOT EXISTS txtimeinfo (tx_hash TEXT PRIMARY KEY, time INTEGER)")

    def create_save_fee_table(self, cursor):
        cursor.execute(
            "CREATE TABLE IF NOT EXISTS receviedtxfeeinfo (tx_hash TEXT PRIMARY KEY, fee TEXT)")

    def connect_db(fun):
        def wrapfun(*args, **kw_args):
            try:
                TxDb.conn = sqlite3.connect(TxDb.path)
                TxDb.cursor = TxDb.conn.cursor()
                return fun(*args, **kw_args)
            except Exception as e:
                raise e
            finally:
                if TxDb.cursor:
                    TxDb.cursor.close()
                if TxDb.conn:
                    TxDb.conn.close()
        return wrapfun

    @connect_db
    def get_tx_info(self, address):
        try:
            self.create_table(self.cursor)
            self.cursor.execute("SELECT * FROM txinfo WHERE address=? ORDER BY time", (address,))
            result = self.cursor.fetchall()
            tx_list = []
            for info in result:
                tx_list.append(info)
            return tx_list
        except Exception as e:
            raise e

    @connect_db
    def add_tx_info(self, address, psbt_tx, tx_hash, raw_tx="", failed_info=""):
        try:
            self.create_table(self.cursor)
            self.cursor.execute("INSERT OR IGNORE INTO txinfo VALUES(?, ?, ?, ?, ?, ?)",
                           (tx_hash, address, str(psbt_tx), str(raw_tx), time.time(), failed_info))
            self.conn.commit()
        except Exception as e:
            raise e

    @connect_db
    def get_tx_time_info(self, tx_hash):
        try:
            self.create_save_time_table(self.cursor)
            self.cursor.execute("SELECT * FROM txtimeinfo WHERE tx_hash=?", (tx_hash,))
            result = self.cursor.fetchall()
            tx_list = []
            for info in result:
                tx_list.append(info)
            return tx_list
        except Exception as e:
            raise e

    @connect_db
    def add_tx_time_info(self, tx_hash):
        try:
            self.create_save_time_table(self.cursor)
            self.cursor.execute("INSERT OR IGNORE INTO txtimeinfo VALUES(?, ?)",
                           (tx_hash, time.time()))
            self.conn.commit()
        except Exception as e:
            raise e
        
    ### API for recevied tx fee
    @connect_db
    def get_received_tx_fee_info(self, tx_hash):
        try:
            self.create_save_fee_table(self.cursor)
            self.cursor.execute("SELECT * FROM receviedtxfeeinfo WHERE tx_hash=?", (tx_hash,))
            result = self.cursor.fetchall()
            tx_list = []
            for info in result:
                tx_list.append(info)
            return tx_list
        except Exception as e:
            raise e

    @connect_db
    def add_received_tx_fee_info(self, tx_hash, fee):
        try:
            self.create_save_fee_table(self.cursor)
            self.cursor.execute("INSERT OR IGNORE INTO receviedtxfeeinfo VALUES(?, ?)",
                           (tx_hash, fee))
            self.conn.commit()
        except Exception as e:
            raise e
