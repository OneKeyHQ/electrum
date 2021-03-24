//
//  OKDAppMenuSheetCollectionCell.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/23.
//  Copyright © 2021 Onekey. All rights reserved.
//

import UIKit
import Reusable

extension OKDAppMenuSheetCollectionCell: NibReusable {}

final class OKDAppMenuSheetCollectionCell: UICollectionViewCell {

    @IBOutlet weak var itemIcon: UIImageView!
    @IBOutlet weak var itemTitle: UILabel!
    @IBOutlet weak var iconTop: NSLayoutConstraint!
    @IBOutlet weak var iconWidth: NSLayoutConstraint!
    @IBOutlet weak var itemSubTitle: UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    func setModel(type: OKDAppMenuType, coinType: String?) {
        let model = type.model
        itemIcon.image = UIImage(named: model.icon)
        itemTitle.text = model.title
        itemSubTitle.isHidden = true
        iconWidth.constant = 32
        iconTop.constant = 16
        if type == .switchAccount {
            if let wallet = OKWalletManager.sharedInstance().currentWalletInfo {
                itemIcon.image = wallet.coinType.coinImage
                itemSubTitle.isHidden = false
                itemSubTitle.text = wallet.addr.addressName
                iconWidth.constant = 24
                iconTop.constant = 5
            } else {
                iconTop.constant = 11
            }
        }
    }
}
