//
//  OKDAppMenu.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/23.
//  Copyright © 2021 Onekey. All rights reserved.
//

import Foundation

enum OKDAppMenuType {
    case switchAccount
    case collect
    case collected
    case onekeyKeys
    case floatingWindow
    case refresh
    case share
    case copyURL
    case openInSafari
}

extension OKDAppMenuType {
    var model:OKDAppMenuItem {
        switch self {
        case .switchAccount:
            return OKDAppMenuItem(icon: "token_eth", title: "Switch account".localized)
        case .collect:
            return OKDAppMenuItem(icon: "ok_menu_collect", title: "Favorites" .localized)
        case .collected:
            return OKDAppMenuItem(icon: "ok_menu_collected", title: "Favorites" .localized)
        case .onekeyKeys:
            return OKDAppMenuItem(icon: "ok_menu_key", title: "Onekey key".localized)
        case .floatingWindow:
            return OKDAppMenuItem(icon: "ok_menu_float", title: "Floating window".localized)
        case .refresh:
            return OKDAppMenuItem(icon: "ok_menu_refresh", title: "Refresh".localized)
        case .share:
            return OKDAppMenuItem(icon: "ok_menu_share", title: "Share".localized)
        case .copyURL:
            return OKDAppMenuItem(icon: "ok_menu_copy", title: "Copy URL".localized)
        case .openInSafari:
            return OKDAppMenuItem(icon: "ok_menu_browser", title: "Browser opens".localized)
        }
    }
}

struct OKDAppMenuItem {
    let icon: String
    let title: String
}
