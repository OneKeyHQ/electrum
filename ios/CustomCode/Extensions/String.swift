//
//  String.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/16.
//  Copyright © 2021 Onekey. All rights reserved.
//

import Foundation

extension String {

    var coinImage: UIImage? {
        guard !self.isEmpty else { return nil }
        return UIImage(named: "token_" + self.lowercased())
    }

    func divEthereumUnit(type: EthereumUnit = .ether) -> NSDecimalNumber {
        guard !self.isEmpty else { return NSDecimalNumber(value: 0) }
        guard self != "0" else { return NSDecimalNumber(value: 0) }
        let value = NSDecimalNumber(string: self)
        let ratio = NSDecimalNumber(string: String(type.rawValue))
        let behavior = NSDecimalNumberHandler(
            roundingMode: .down,
            scale: 6,
            raiseOnExactness: false,
            raiseOnOverflow: false,
            raiseOnUnderflow: false,
            raiseOnDivideByZero: true
        )
        let result = value.dividing(by: ratio, withBehavior: behavior)
        return result
    }

   var localized: String {
       guard !isEmpty else { return "" }
       return (self as NSString).localized()
   }

   var has0xPrefix: Bool {
       return hasPrefix("0x")
   }

    var addHttps: String {
        if !self.lowercased().contains("https") {
            return "https:" + self
        }
        return self
    }

   var drop0x: String {
       if count > 2 && substring(with: 0..<2) == "0x" {
           return String(dropFirst(2))
       }
       return self
   }

   var add0x: String {
       if hasPrefix("0x") {
           return self
       } else {
           return "0x" + self
       }
   }

   var hextToDec: Int {
       return 0
   }

    var hexToDecimal: Int {
        guard !isEmpty else { return 0 }
        return Int(self.drop0x, radix: 16) ?? 0
    }

    var keccak256: String {
        return (self as NSString).keccak256()
    }

    var toInt: Int {
        return Int(self) ?? 0
    }

    var toURL: URL? {
        return URL.init(string: self)
    }


    var addressName: String {
        if count <= 4 {
            return self
        }
        return substring(from: count - 4).uppercased()
    }

}

extension String {
    func index(from: Int) -> Index {
        return index(startIndex, offsetBy: from)
    }

    func substring(from: Int) -> String {
        let fromIndex = index(from: from)
        return String(self[fromIndex...])
    }

    func substring(to: Int) -> String {
        let toIndex = index(from: to)
        return String(self[..<toIndex])
    }

    func substring(with r: Range<Int>) -> String {
        let startIndex = index(from: r.lowerBound)
        let endIndex = index(from: r.upperBound)
        return String(self[startIndex..<endIndex])
    }

    func nextLetterInAlphabet(for index: Int) -> String? {
        guard let uniCode = UnicodeScalar(self) else {
            return nil
        }
        switch uniCode {
        case "A"..<"Z":
            return String(UnicodeScalar(uniCode.value.advanced(by: index))!)
        default:
            return nil
        }
    }
}
