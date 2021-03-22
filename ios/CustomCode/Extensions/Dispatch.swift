//
//  Dispatch.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/21.
//  Copyright © 2021 Onekey. All rights reserved.
//

import Foundation

struct Dispatch {

    public static func globalAsync(closure: @escaping () -> Void) {
        DispatchQueue.global().async(execute: closure)
    }

    public static func mainAsync(closure: @escaping () -> Void) {
        if Thread.current.isMainThread {
            closure()
        } else {
            DispatchQueue.main.async(execute: closure)
        }
    }

    public static func delay(_ delay: Double, closure: @escaping () -> Void) {
        let when = DispatchTime.now() + delay
        DispatchQueue.main.asyncAfter(deadline: when, execute: closure)
    }

}
