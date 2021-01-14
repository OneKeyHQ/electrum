import functools
import sqlite3
import time


def connect_db(f):
    @functools.wraps(f)
    def wrapper(cls, *args, is_add=False, **kwargs):
        try:
            cls.conn = sqlite3.connect(cls.path)
            cls.cursor = cls.conn.cursor()
            result = f(cls, *args, **kwargs)
        except Exception as e:
            if is_add:
                cls.conn.rollback()
            raise e
        else:
            if is_add:
                cls.conn.commit()
            return result
        finally:
            if cls.cursor:
                cls.cursor.close()
            if cls.conn:
                cls.conn.close()

    return wrapper


class TxDb:
    conn = None
    cursor = None
    path = None

    def __init__(self, path=""):
        TxDb.path = path

    @classmethod
    def create_table(cls):
        cls.cursor.execute(
            "CREATE TABLE IF NOT EXISTS txinfo (tx_hash TEXT PRIMARY KEY, address TEXT, psbt_tx TEXT, raw_tx Text, time INTEGER, faile_info TEXT)")

    @classmethod
    def create_save_time_table(cls):
        cls.cursor.execute(
            "CREATE TABLE IF NOT EXISTS txtimeinfo (tx_hash TEXT PRIMARY KEY, time INTEGER)")

    @classmethod
    def create_save_fee_table(cls):
        cls.cursor.execute(
            "CREATE TABLE IF NOT EXISTS receviedtxfeeinfo (tx_hash TEXT PRIMARY KEY, fee TEXT)")

    @classmethod
    @connect_db
    def get_tx_info(cls, address):
        cls.create_table()
        cls.cursor.execute("SELECT * FROM txinfo WHERE address=? ORDER BY time", (address,))
        result = cls.cursor.fetchall()
        tx_list = []
        for info in result:
            tx_list.append(info)
        return tx_list

    @classmethod
    @connect_db
    def add_tx_info(cls, address, psbt_tx, tx_hash, raw_tx="", failed_info=""):
        cls.create_table()
        cls.cursor.execute("INSERT OR IGNORE INTO txinfo VALUES(?, ?, ?, ?, ?, ?)",
                           (tx_hash, address, str(psbt_tx), str(raw_tx), time.time(), failed_info))

    @classmethod
    @connect_db
    def get_tx_time_info(cls, tx_hash):
        cls.create_save_time_table()
        cls.cursor.execute("SELECT * FROM txtimeinfo WHERE tx_hash=?", (tx_hash,))
        result = cls.cursor.fetchall()
        tx_list = []
        for info in result:
            tx_list.append(info)
        return tx_list

    @classmethod
    @connect_db
    def add_tx_time_info(cls, tx_hash):
        cls.create_save_time_table()
        cls.cursor.execute("INSERT OR IGNORE INTO txtimeinfo VALUES(?, ?)",
                           (tx_hash, time.time()))

    ### API for recevied tx fee
    @classmethod
    @connect_db
    def get_received_tx_fee_info(cls, tx_hash):
        cls.create_save_fee_table()
        cls.cursor.execute("SELECT * FROM receviedtxfeeinfo WHERE tx_hash=?", (tx_hash,))
        result = cls.cursor.fetchall()
        tx_list = []
        for info in result:
            tx_list.append(info)
        return tx_list

    @classmethod
    @connect_db
    def add_received_tx_fee_info(cls, tx_hash, fee):
        cls.create_save_fee_table()
        cls.cursor.execute("INSERT OR IGNORE INTO receviedtxfeeinfo VALUES(?, ?)",
                           (tx_hash, fee))
