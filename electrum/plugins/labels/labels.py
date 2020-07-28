import asyncio
import hashlib
import json
import sys
import traceback
from typing import Union, TYPE_CHECKING

import base64

from electrum.plugin import BasePlugin, hook
from electrum.crypto import aes_encrypt_with_iv, aes_decrypt_with_iv, EncodeAES_bytes, DecodeAES_bytes
from electrum.i18n import _
from electrum.util import log_exceptions, ignore_exceptions, make_aiohttp_session
from electrum.network import Network

if TYPE_CHECKING:
    from electrum.wallet import Abstract_Wallet


class ErrorConnectingServer(Exception):
    def __init__(self, reason: Union[str, Exception] = None):
        self.reason = reason

    def __str__(self):
        header = _("Error connecting to {} server").format('Labels')
        reason = self.reason
        if isinstance(reason, BaseException):
            reason = repr(reason)
        return f"{header}: {reason}" if reason else header


class LabelsPlugin(BasePlugin):

    def __init__(self, parent, config, name):
        BasePlugin.__init__(self, parent, config, name)
        #self.target_host = 'labels.electrum.org'
        self.target_host = '39.105.86.163:8080'
        #self.target_host = '10.10.0.37:8080'
        self.wallets = {}
        self.create_wallets = {}
        self.get_wallet_loop = asyncio.get_event_loop()

    def set_host(self, ip, port):
        self.target_host = '%s:%s' %(ip, port)

    def encode(self, wallet, msg):
        password, iv, wallet_id = self.wallets[wallet]
        encrypted = aes_encrypt_with_iv(password, iv, msg.encode('utf8'))
        return base64.b64encode(encrypted).decode()

    def decode(self, wallet, message):
        password, iv, wallet_id = self.wallets[wallet]
        decoded = base64.b64decode(message)
        decrypted = aes_decrypt_with_iv(password, iv, decoded)
        return decrypted.decode('utf8')

    def encode_xpub(self, xpub, msg):
        mpk = xpub.encode('ascii')
        password = hashlib.sha1(mpk).hexdigest()[:32].encode('ascii')
        iv = hashlib.sha256(password).digest()[:16]
        encrypted = aes_encrypt_with_iv(password, iv, msg.encode('utf8'))
        return base64.b64encode(encrypted).decode()
        

    def decode_xpub(self, xpub, message):
        decoded = base64.b64decode(message)
        mpk = xpub.encode('ascii')
        password = hashlib.sha1(mpk).hexdigest()[:32].encode('ascii')
        iv = hashlib.sha256(password).digest()[:16]
        decrypted = aes_decrypt_with_iv(password, iv, decoded)
        return decrypted.decode('utf8')

    def get_nonce(self, wallet):
        # nonce is the nonce to be used with the next change
        nonce = wallet.db.get('wallet_nonce')
        if nonce is None:
            nonce = 1
            self.set_nonce(wallet, nonce)
        return nonce

    def set_nonce(self, wallet, nonce):
        self.logger.info(f"set {wallet.basename()} nonce to {nonce}")
        wallet.db.put("wallet_nonce", nonce)

    @hook
    def set_label(self, wallet, item, label):
        if wallet not in self.wallets:
            return
        if not item:
            return
        nonce = self.get_nonce(wallet)
        wallet_id = self.wallets[wallet][2]
        bundle = {"walletId": wallet_id,
                  "walletNonce": nonce,
                  "externalId": self.encode(wallet, item),
                  "encryptedLabel": self.encode(wallet, label)}
        asyncio.run_coroutine_threadsafe(self.do_post_safe("/label", bundle), wallet.network.asyncio_loop)
        # Caller will write the wallet
        self.set_nonce(wallet, nonce + 1)

    @ignore_exceptions
    @log_exceptions
    async def do_post_safe(self, *args):
        await self.do_post(*args)

    async def do_get(self, url="/labels"):
        url = 'http://' + self.target_host + url
        network = Network.get_instance()
        proxy = network.proxy if network else None
        async with make_aiohttp_session(proxy) as session:
            async with session.get(url) as result:
                return await result.json()

    async def do_post(self, url="/labels", data=None):
        url = 'http://' + self.target_host + url
        network = Network.get_instance()
        proxy = network.proxy if network else None
        async with make_aiohttp_session(proxy) as session:
            async with session.post(url, json=data) as result:
                try:
                    return await result.json()
                except Exception as e:
                    raise Exception('Could not decode: ' + await result.text()) from e

    async def push_thread(self, wallet):
        wallet_data = self.wallets.get(wallet, None)
        if not wallet_data:
            raise Exception('Wallet {} not loaded'.format(wallet))
        wallet_id = wallet_data[2]
        bundle = {"labels": [],
                  "walletId": wallet_id,
                  "walletNonce": self.get_nonce(wallet)}
        for key, value in wallet.labels.items():
            try:
                encoded_key = self.encode(wallet, key)
                encoded_value = self.encode(wallet, value)
            except:
                self.logger.info(f'cannot encode {repr(key)} {repr(value)}')
                continue
            bundle["labels"].append({'encryptedLabel': encoded_value,
                                     'externalId': encoded_key})
        await self.do_post("/labels", bundle)

    async def pull_thread(self, wallet, force):
        wallet_data = self.wallets.get(wallet, None)
        if not wallet_data:
            raise Exception('Wallet {} not loaded'.format(wallet))
        wallet_id = wallet_data[2]
        nonce = 1 if force else self.get_nonce(wallet) - 1
        self.logger.info(f"asking for labels since nonce {nonce}")
        try:
            response = await self.do_get("/labels/since/%d/for/%s" % (nonce, wallet_id))
            print("respose ---11111---= %s" %response)
        except Exception as e:
            raise ErrorConnectingServer(e) from e
        if response["labels"] is None:
            self.logger.info('no new labels')
            return
        result = {}
        for label in response["labels"]:
            try:
                key = self.decode(wallet, label["externalId"])
                value = self.decode(wallet, label["encryptedLabel"])
            except:
                continue
            try:
                json.dumps(key)
                json.dumps(value)
            except:
                self.logger.info(f'error: no json {key}')
                continue
            result[key] = value

        for key, value in result.items():
            if force or not wallet.labels.get(key):
                wallet.labels[key] = value

        self.logger.info(f"received {len(response)} labels")
        self.set_nonce(wallet, response["nonce"] + 1)
        self.on_pulled(wallet)

    def on_pulled(self, wallet: 'Abstract_Wallet') -> None:
        raise NotImplementedError()

    @ignore_exceptions
    @log_exceptions
    async def pull_safe_thread(self, wallet, force):
        try:
            await self.pull_thread(wallet, force)
        except ErrorConnectingServer as e:
            self.logger.info(repr(e))

    def pull(self, wallet, force):
        if not wallet.network: raise Exception(_('You are offline.'))
        return asyncio.run_coroutine_threadsafe(self.pull_thread(wallet, force), wallet.network.asyncio_loop).result()

    def push(self, wallet):
        if not wallet.network: raise Exception(_('You are offline.'))
        return asyncio.run_coroutine_threadsafe(self.push_thread(wallet), wallet.network.asyncio_loop).result()


    async def push_xpub_thread(self, wallet, wallet_type, wallet_name):
        wallet_data = self.create_wallets.get(wallet, None)
        if not wallet_data:
            raise Exception('Wallet {} not loaded'.format(wallet))
        wallet_id = wallet_data[2]
        
        for xpub in wallet_data[3]:
           # xpubId = self.encode(wallet, xpub)
            bundle = {"xpubs": "",
                      "xpubId": self.encode_xpub(xpub, xpub),
                      "walletId": wallet_id,
                      "walletType": self.encode_xpub(xpub, wallet_type),
                      "walletName": self.encode_xpub(xpub, wallet_name)}
            bundle_list = []
            for value in wallet_data[3]:
                bundle_list.append(value)
            bundle["xpubs"] = self.encode_xpub(xpub, json.dumps(bundle_list))
            await self.do_post("/wallet", bundle)

    async def pull_xpub_thread(self, xpub):
        try:
            search_xpub = self.encode_xpub(xpub, xpub)
            bundle = {}
            bundle['xpubId'] = search_xpub
            response = await self.do_post("/wallets", bundle)
            print("--111112222 response=%s" %response)
        except Exception as e:
            raise ErrorConnectingServer(e) from e
        out = []
        if response.__contains__('Error'):
            return json.dumps(out)
            #raise BaseException(response)

        if response["Walltes"] is None:
            self.logger.info('no wallets info')
            return
        for wallet in response["Walltes"]:
            result = {}
            try:
                result['xpubId'] = self.decode_xpub(xpub, wallet['xpubId'])
                result['walletId'] = wallet['WalletId']
                result['xpubs'] = self.decode_xpub(xpub, wallet['Xpubs'])
                result['walletType'] = self.decode_xpub(xpub, wallet['WalletType'])
                result['walletName'] = self.decode_xpub(xpub, wallet['WalletName'])
            except:
                continue
            out.append(result)
        self.logger.info(f"received {len(response)} wallets")
        print("wallet info is %s---" %json.dumps(out))
        return json.dumps(out)

    @ignore_exceptions
    @log_exceptions
    async def pull_xpub_safe_thread(self, wallet, force):
        try:
            await self.pull_thread(wallet, force)
        except ErrorConnectingServer as e:
            self.logger.info(repr(e))

    @ignore_exceptions
    @log_exceptions
    async def push_xpub_safe_thread(self, wallet, wallet_type, wallet_name):
        try:
            await self.push_xpub_thread(wallet, wallet_type, wallet_name)
        except ErrorConnectingServer as e:
            self.logger.info(repr(e))

    def pull_xpub(self, xpub):
        return asyncio.run_coroutine_threadsafe(self.pull_xpub_thread(xpub), self.get_wallet_loop).result()

    def push_xpub(self, wallet):
        if not wallet.network: raise Exception(_('You are offline.'))
        return asyncio.run_coroutine_threadsafe(self.push_xpub_thread(wallet), wallet.network.asyncio_loop).result()
   
    def create_new_wallet(self, wallet, wallet_type, wallet_name):
        if not wallet.network: return  # 'offline' mode
        mpk = wallet.get_fingerprint()
        if not mpk:
            return
        mpk = mpk.encode('ascii')
        password = hashlib.sha1(mpk).hexdigest()[:32].encode('ascii')
        iv = hashlib.sha256(password).digest()[:16]
        wallet_id = hashlib.sha256(mpk).hexdigest()
        xpubkeys = wallet.get_master_public_keys()
        self.create_wallets[wallet] = (password, iv, wallet_id, xpubkeys)
       # self.push(wallet)
        # If there is an auth token we can try to actually start syncing
        asyncio.run_coroutine_threadsafe(self.push_xpub_safe_thread(wallet, wallet_type, wallet_name), wallet.network.asyncio_loop)

    ###################### sync tx info######################
    def stop_wallet(self, wallet):
        self.wallets.pop(wallet, None)
        self.get_wallet_loop.close()

    def start_wallet(self, wallet):
        if not wallet.network: return  # 'offline' mode
        nonce = self.get_nonce(wallet)
        self.logger.info(f"wallet {wallet.basename()} nonce is {nonce}")
        mpk = wallet.get_fingerprint()
        if not mpk:
            return
        mpk = mpk.encode('ascii')
        password = hashlib.sha1(mpk).hexdigest()[:32].encode('ascii')
        iv = hashlib.sha256(password).digest()[:16]
        wallet_id = hashlib.sha256(mpk).hexdigest()
        self.wallets[wallet] = (password, iv, wallet_id)
        # If there is an auth token we can try to actually start syncing

        # asyncio.run_coroutine_threadsafe(self.push_thread(wallet), wallet.network.asyncio_loop)
        asyncio.run_coroutine_threadsafe(self.pull_safe_thread(wallet, False), wallet.network.asyncio_loop)

    def pull_tx(self, wallet):
        if not wallet.network: raise Exception(_('You are offline.'))
        return asyncio.run_coroutine_threadsafe(self.pull_tx_thread(wallet), wallet.network.asyncio_loop).result()

    def push_tx(self, wallet, action, tx_hash, tx=None, tx_hash_old=None):
        if not wallet.network: raise Exception(_('You are offline.'))
        return asyncio.run_coroutine_threadsafe(self.push_tx_thread(wallet, action, tx_hash, tx, tx_hash_old), wallet.network.asyncio_loop).result()

    async def push_tx_thread(self, wallet, action, tx_hash, tx=None, tx_hash_old=None):
        wallet_data = self.wallets.get(wallet, None)
        if not wallet_data:
            raise Exception('Wallet {} not loaded'.format(wallet))
        wallet_id = wallet_data[2]
        bundle = {"walletId": wallet_id,
                  "txHash": self.encode(wallet, tx_hash)}
        if action != "deltx":
            bundle['tx'] = self.encode(wallet, tx)
        if action == "rbftx":
            bundle['txHashOld'] = self.encode(wallet, tx_hash_old)

        cmd = '/%s'%action
        await self.do_post(url=cmd, data=bundle)

    async def pull_tx_thread(self, wallet):
        wallet_data = self.wallets.get(wallet, None)
        if not wallet_data:
            raise Exception('Wallet {} not loaded'.format(wallet))
        wallet_id = wallet_data[2]
        try:
            response = await self.do_get("/transactions/%s"%(wallet_id))
            # print("respose ---11111---= %s" %response)
        except Exception as e:
            raise ErrorConnectingServer(e) from e
        if not response.__contains__("Transactions"):
            raise BaseException('no transactions')
        # if response.__contains__('Error'):
        #     raise BaseException(response)

        out = []
        for tx in response["Transactions"]:
            result = {}
            try:
                result['walletId'] = tx['WalletId']
                result['tx_hash'] = self.decode(wallet, tx['TxHash'])#self.decode_xpub(xpub, wallet['xpubId'])
                result['tx'] = self.decode(wallet, tx['Tx'])
            except:
                continue
            out.append(result)
        self.logger.info(f"received {len(response)} transactions")
        # print("tx info is %s---" %json.dumps(out))
        return json.dumps(out)
        

    
   
    