//
//  Error.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/19.
//  Copyright © 2021 Onekey. All rights reserved.
//

import Foundation

extension Error {
    var code: Int { return (self as NSError).code }
    var domain: String { return (self as NSError).domain }
}
