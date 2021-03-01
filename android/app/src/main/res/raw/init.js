const addressHex = "%1$s";
const rpcURL = "%2$s";
const chainID = "%3$s";
const debug = true;

function executeCallback (id, error, value) {
  OnekeyWallet.executeCallback(id, error, value)
}

function debugPrint(...args){
  if(debug){
    console.log(args)
  }
}

window.OnekeyWallet.init(rpcURL, {
  getAccounts: function (cb) { cb(null, [addressHex]) },
  signTransaction: function (tx, cb){
    debugPrint('signing a transaction', JSON.stringify(tx))
    const { id = 8888 } = tx
    OnekeyWallet.addCallback(id, cb)

    var gasLimit = tx.gasLimit || tx.gas || null;
    var gasPrice = tx.gasPrice || null;
    var data = tx.data || null;
    var nonce = tx.nonce || -1;
    onekeyJsCall.signTransaction(id, tx.to || null, tx.value, nonce, gasLimit, gasPrice, data);
  },
//  processTransaction: function (tx, cb){
//      debugPrint('signing a transaction', JSON.stringify(tx))
//      const { id = 8888 } = tx
//      OnekeyWallet.addCallback(id, cb)
//
//      var gasLimit = tx.gasLimit || tx.gas || null;
//      var gasPrice = tx.gasPrice || null;
//      var data = tx.data || null;
//      var nonce = tx.nonce || -1;
//      onekeyJsCall.signTransaction(id, tx.to || null, tx.value, nonce, gasLimit, gasPrice, data);
//    },
  signMessage: function (msgParams, cb) {
      debugPrint('signMessage', msgParams)
      const { data, chainType } = msgParams
      const { id = 8888 } = msgParams
    OnekeyWallet.addCallback(id, cb)
    onekeyJsCall.signMessage(id, data);
  },
  signPersonalMessage: function (msgParams, cb) {
      debugPrint('signPersonalMessage', msgParams)
      const { data, chainType } = msgParams
      const { id = 8888 } = msgParams
    OnekeyWallet.addCallback(id, cb)
    onekeyJsCall.signPersonalMessage(id, data);
  },
  signTypedMessage: function (msgParams, cb) {
    debugPrint('signTypedMessage ', msgParams)
    const { data } = msgParams
    const { id = 8888 } = msgParams
    OnekeyWallet.addCallback(id, cb)
    onekeyJsCall.signTypedMessage(id, JSON.stringify(msgParams))
  },
  enable: function() {
      return new Promise(function(resolve, reject) {
          //send back the coinbase account as an array of one
          resolve([addressHex])
      })
  }
}, {
    address: addressHex,
    networkVersion: chainID
})

window.web3.setProvider = function () {
  console.log('Onekey Wallet - overrode web3.setProvider')
}

window.web3.version.getNetwork = function(cb) {
    debugPrint('Onekey Provider - getNetwork '+ chainID)
    cb(null, chainID)
}

window.web3.eth.getCoinbase = function(cb) {
    debugPrint('Onekey Provider - getCoinbase '+ addressHex)
    return cb(null, addressHex)
}

window.web3.eth.defaultAccount = addressHex

window.ethereum = web3.currentProvider
