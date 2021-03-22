//
//  NetworkErrorView.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/21.
//  Copyright © 2021 Onekey. All rights reserved.
//

import UIKit
import Reusable

class NetworkErrorView: UIView, NibLoadable {

    @IBOutlet weak var netTipIcon: UIImageView!
    @IBOutlet weak var netTipLabel: UILabel!

    private weak var gesture: UITapGestureRecognizer?
    
    @discardableResult
    static func addParentView(
        parentView: UIView,
        tipTitle: String = "",
        handler: @escaping () -> Void) -> NetworkErrorView {
        let networkView = NetworkErrorView.loadFromNib()
        parentView.addSubview(networkView)
        networkView.fillToSuperview()
        networkView.updateTipTitle(title: tipTitle)
        networkView.gesture = networkView.addTapGestureRecognizer(action: { [weak networkView] in
            networkView?.updateVisible(visible: false)
        })
        return networkView
    }

    func updateTipTitle(title: String) {
        if !title.isEmpty {
            netTipLabel.text = title
        }
    }

    func updateVisible(visible: Bool) {
        isHidden = !visible
        subviews.forEach { $0.isHidden = !visible  }
        gesture?.isEnabled = visible
    }

    override func awakeFromNib() {
        super.awakeFromNib()
        updateVisible(visible: false)
        netTipLabel.text = "Sorry, access failed, please try again or check the network".localized
    }

}
