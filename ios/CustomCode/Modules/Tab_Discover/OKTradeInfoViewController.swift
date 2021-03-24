//
//  OKTradeInfoViewController.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/14.
//  Copyright © 2021 Onekey. All rights reserved.
//

import UIKit
import Reusable
import PanModal

class OKTradeInfoViewController: PanModalViewController {

    @IBOutlet weak var navView: OKModalNavView!
    @IBOutlet weak var tokenImageView: UIImageView!
    @IBOutlet weak var tokenCountLabel: UILabel!
    @IBOutlet weak var operateTitleLabel: UILabel!
    @IBOutlet weak var operateContentLabel: UILabel!
    @IBOutlet weak var payAccountTitleLabel: UILabel!
    @IBOutlet weak var payAccountContentLabel: UILabel!
    @IBOutlet weak var receiveAccountTitleLabel: UILabel!
    @IBOutlet weak var receiveAccountContentLabel: UILabel!
    @IBOutlet weak var payGasTitleLabel: UILabel!
    @IBOutlet weak var payGasContentLabel: UILabel!
    @IBOutlet weak var nextStepButton: UIButton!
    @IBOutlet weak var gasActivity: UIActivityIndicatorView!

    private var transaction: DAppTransaction?
    private var feeInfoModel: OKDefaultFeeInfoModel?
    private var selectedFee: OKSendFeeModel?
    private var selectGasType: OKTradeFeeSelect = .medium

    private var feeViewController: OKTradeFeeViewController?

    var cancelAction: (()->Void)?
    var finishWithPassword: ((String, OKSendFeeModel?)->Void)?

    @objc class func instance(transaction: DAppTransaction) -> OKTradeInfoViewController {
        let page = OKTradeInfoViewController.instantiate()
        page.transaction = transaction;
        return page
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: false)
        view.layoutIfNeeded()
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setUpUI()
        if let transaction = transaction {
            configWithDAppTransaction(transaction: transaction)
        }
    }

    private func setUpUI() {
        navView.setNavTitle(string: "Transaction details".localized)
        navView.setNavDismiss { [weak self] in
            self?.dismiss(animated: true, completion: nil)
            self?.cancelAction?()
        }
        operateTitleLabel.text = "Operation name".localized
        payAccountTitleLabel.text = "Payment account".localized
        receiveAccountTitleLabel.text = "Receiving address".localized
        payGasTitleLabel.text = "Miners fee".localized
        nextStepButton.setTitleForAllStates("The next step".localized)
        view.roundCorners([.topLeft, .topRight], radius: 20)
        payGasContentLabel.text = ""
    }

    private func configWithDAppTransaction(transaction: DAppTransaction) {
        let tokenName = transaction.coinType.uppercased()
        operateContentLabel.text = tokenName.uppercased() + "transfer".localized
        payAccountContentLabel.text = transaction.from
        receiveAccountContentLabel.text = transaction.to
        let amount = String(transaction.value).divEthereumUnit().stringValue
        tokenCountLabel.text = amount + " " + tokenName
        tokenImageView.image = transaction.coinType.coinImage
        if let gas = transaction.gas {
            let value = gas.gweiToEther
            updateGasValue(value: value)
        }
    }

     func updateDefaultFeeInfo(model: OKDefaultFeeInfoModel) {
        self.feeInfoModel = model
        updateGasValue(value: model.normal.fee)
        self.selectedFee = model.normal
        feeViewController?.updateDefaultFeeInfo(model: model)
    }

    private func updateGasValue(value: String) {
        gasActivity.stopAnimating()
        payGasContentLabel.text = value + " "
            + (transaction?.coinType.uppercased() ?? "")
    }

    @IBAction func changeGasNumAction(_ sender: Any) {
        if feeInfoModel == nil {
            return
        }
        let page = OKTradeFeeViewController.instantiate()
        page.model = feeInfoModel
        page.selectGasType = selectGasType
        page.feeModel = selectedFee
        feeViewController = page
        page.callBackFee = { [weak self] (type, value) in
            guard let self = self else { return }
            self.selectGasType = type
            if let fee = value {
                self.selectedFee = value
                self.updateGasValue(value: fee.fee)
            }
        }
        navigationController?.pushViewController(page, animated: true)
    }

    @IBAction func nextStepAction(_ sender: Any) {
        guard let wallet = OKWalletManager.sharedInstance().currentWalletInfo else { return }
        if wallet.walletType == .hardware {
            self.finishWithPassword?("", nil)
        } else {
            OKHelperUtils.presentPasswordPage(self) { [weak self] pwd in
                guard let `self` = self else { return }
                self.finishWithPassword?(pwd, self.selectedFee)
            }
        }
    }

    // MARK: - Pan Modal Presentable

    override var shortFormHeight: PanModalHeight {
        .contentHeight(500)
    }

    override var longFormHeight: PanModalHeight {
        .maxHeight
    }

    override func shouldRespond(to panModalGestureRecognizer: UIPanGestureRecognizer) -> Bool {
        true
    }

}

extension OKTradeInfoViewController: StoryboardSceneBased {
    static let sceneStoryboard = UIStoryboard(name: "Tab_Discover", bundle: nil)
    static var sceneIdentifier = "OKTradeInfoViewController"
}
