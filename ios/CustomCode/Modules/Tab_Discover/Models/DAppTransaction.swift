//
//  DAppTransaction.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/17.
//  Copyright © 2021 Onekey. All rights reserved.
//

import Foundation

class DAppTransaction: NSObject {

    var from: String = ""
    var to: String = ""
    var value: Int = 0
    var gas: Int?
    var gasPrice: Int?
    var data: String?
    var coin: String = ""
    var coinType: String = ""
    var nonce: Int?

    init(from: String,
         to: String,
         value: Int,
         gas: Int?,
         gasPrice: Int?,
         data: String?,
         coinType: String,
         nonce: Int?
    ) {
        super.init()
        self.from = from
        self.to = to
        self.value = value
        self.gas = gas
        self.gasPrice = gasPrice
        self.data = data
        self.coinType = coinType
        self.nonce = nonce
    }

    func toMap() -> [String : Any] {
        var map: [String : Any]  = [
            "from" : from,
            "to" : to,
            "value" : value,
        ]
        if let gas = gas {
            map["gas"] = gas
        }
        if let gasPrice = gasPrice {
            map["gasPrice"] = gasPrice
        }
        if let nonce = nonce {
            map["nonce"] = nonce
        }
        if let data = data {
            map["data"] = data
        }
        return map
    }

}
