//
//  OKSignMessageViewController.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/17.
//  Copyright © 2021 Onekey. All rights reserved.
//

import UIKit
import Reusable
import PanModal

class OKSignMessageViewController: PanModalViewController {

    @IBOutlet weak var navView: OKModalNavView!
    @IBOutlet weak var senderTitleLabel: UILabel!
    @IBOutlet weak var sendContentLabel: UILabel!
    @IBOutlet weak var messageTitleLabel: UILabel!
    @IBOutlet weak var messageContentLabel: UILabel!
    @IBOutlet weak var messageContentTextView: UITextView!
    @IBOutlet weak var nextButton: UIButton!
    
    private var address = ""
    private var message = ""

    var cancelAction: (()->Void)?
    var finishWithPassword: ((String)->Void)?
    
    @objc class func instance(address: String, message: String) -> OKSignMessageViewController {
        let page = OKSignMessageViewController.instantiate()
        page.address = address
        page.message = message
        return page
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: false)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        navView.setNavTitle(string: "Signed message".localized)

        navView.setNavDismiss { [weak self] in
            self?.dismiss(animated: true, completion: nil)
            self?.cancelAction?()
        }

        senderTitleLabel.text = "The sender".localized
        sendContentLabel.text = address
        messageTitleLabel.text = "The message".localized
        nextButton.setTitleForAllStates("The next step".localized)
        messageContentLabel.text = message
        messageContentTextView.text = message

    }

    @IBAction func nextAction(_ sender: Any) {
        guard let wallet = OKWalletManager.sharedInstance().currentWalletInfo else { return }
        if wallet.walletType == .hardware {
            self.finishWithPassword?("")
        } else {
            OKHelperUtils.presentPasswordPage(self) { [weak self] pwd in
                guard let `self` = self else { return }
                self.finishWithPassword?(pwd)
            }
        }
    }

}

extension OKSignMessageViewController: StoryboardSceneBased {
    static let sceneStoryboard = UIStoryboard(name: "Tab_Discover", bundle: nil)
    static var sceneIdentifier = "OKSignMessageViewController"
}
