//
//  UIImageView.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/24.
//  Copyright © 2021 Onekey. All rights reserved.
//

import Foundation

extension UIImageView {

    func setNetImage(url: String, placeholder: String = "") {
        if let url = URL.init(string: url) {
//            sd_setImage(
//                with: url,
//                placeholderImage: UIImage(named: placeholder),
//                options: [],
//                context: nil
//            )
        } else {
            image = UIImage(named: placeholder)
        }

    }
}
