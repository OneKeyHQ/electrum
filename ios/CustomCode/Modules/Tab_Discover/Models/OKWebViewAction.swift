//
//  OKWebViewAction.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

import Foundation

enum OKWebJSAction: String, Decodable, CaseIterable {
    case openDapp
    case unknow
}

struct OKWebJSModel {

    let method: String?
    let params: String?
    let id: String?
    
     init(paramters: [String : Any]?) {
        method = paramters?["method"] as? String
        params = paramters?["params"] as? String
        id = paramters?["id"] as? String
    }

    func jsAction() -> OKWebJSAction {
        guard let method = method else { return .unknow }
        return OKWebJSAction(rawValue: method) ?? .unknow
    }

    func jsParams() -> OKWebJSParams? {
        guard let params = params else { return nil }
        let decoder = JSONDecoder()
        return try? decoder.decode(OKWebJSParams.self, from: params.data(using: .utf8)!)
    }

}
