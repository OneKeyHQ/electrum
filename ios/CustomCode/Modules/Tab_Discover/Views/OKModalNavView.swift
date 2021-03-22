//
//  OKModalNavView.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/14.
//  Copyright © 2021 Onekey. All rights reserved.
//

import UIKit
import Reusable

class OKModalNavView: UIView {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var leftButton: UIButton!
    @IBOutlet weak var rightButton: UIButton!

    required init?(coder aDecoder: NSCoder) {
      super.init(coder: aDecoder)
        setUp()
    }
    
    @objc func setNavTitle(string: String) {
        titleLabel.text = string
    }

    @objc func setNavPop(onTap: @escaping () -> Void) {
        setNavLeftButton(imageName: "ok_large_back", onTap: onTap)
    }

    @objc func setNavDismiss(onTap: @escaping () -> Void) {
        setNavLeftButton(imageName: "popup_nav_close", onTap: onTap)
    }

    @objc func setNavLeftButton(imageName:String, onTap: @escaping () -> Void) {
        if let image = UIImage(named: imageName) {
            leftButton.setImageForAllStates(image)
        }
        leftButton.onTap {
            onTap()
        }
    }
    
    private func setUp() {
        loadNibContent()
        let inset = UIEdgeInsets(top: 30, left: 30, bottom: 30, right: 30)
        leftButton.expansionInsets = inset
        leftButton.expansionInsets = inset
    }
}

extension OKModalNavView: NibOwnerLoadable {}
