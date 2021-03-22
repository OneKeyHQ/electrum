//
//  Int.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/19.
//  Copyright © 2021 Onekey. All rights reserved.
//

import Foundation

public enum EthereumUnit: Int64 {
    case wei = 1
    case kwei = 1_000
    case gwei = 1_000_000_000
    case ether = 1_000_000_000_000_000_000
}

extension EthereumUnit {
    var name: String {
        switch self {
        case .wei: return "Wei"
        case .kwei: return "Kwei"
        case .gwei: return "Gwei"
        case .ether: return "Ether"
        }
    }
}

extension Int {

    var gweiToEther: String {
        let scale = NSDecimalNumber(value: EthereumUnit.gwei.rawValue)
            .dividing(by: NSDecimalNumber(value: EthereumUnit.ether.rawValue))
        let gwei = NSDecimalNumber(value: self)
        let behavior = NSDecimalNumberHandler(
            roundingMode: .down,
            scale: 6,
            raiseOnExactness: false,
            raiseOnOverflow: false,
            raiseOnUnderflow: false,
            raiseOnDivideByZero: true
        )
        let value = gwei.multiplying(by: scale, withBehavior: behavior).stringValue
        return value
    }
    
}
