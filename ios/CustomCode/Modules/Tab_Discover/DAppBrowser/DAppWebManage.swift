//
//  DAppManage.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/17.
//  Copyright © 2021 Onekey. All rights reserved.
//

import Foundation

enum EthSignAndSendTxError: Error {
    case signTxError
    case sendTxError
    case missingParams
    case hardwareNotConnected
    case cancel
}

extension EthSignAndSendTxError {
    var errorMsg: String {
        switch self {
        case .sendTxError:
            return "Failed to send transaction".localized
        case .signTxError:
            return "Signature error".localized
        case .cancel:
            return "Cancel operation".localized
        case .missingParams:
            return "Missing transaction data".localized
        case .hardwareNotConnected:
            return "bluetooth.connection.fail".localized
        }
    }
}

enum DAppSignMessageError: Error {
    case missingParams
    case hardwareNotConnected
    case cancel
}

extension DAppSignMessageError {
    var errorMsg: String {
        switch self {
        case .cancel:
            return "Cancel operation".localized
        case .missingParams:
            return "Missing transaction data".localized
        case .hardwareNotConnected:
            return "bluetooth.connection.fail".localized
        }
    }
}

class DAppWebManage {

    static func fetchScriptConfig() -> WKUserScriptConfig? {
        guard let wallet = OKWalletManager.sharedInstance().currentWalletInfo else { return nil }
        let address = wallet.addr
//        return WKUserScriptConfig(
//            address: address,
////            chainId: 1,
////            rpcUrl: "https://mainnet.infura.io/v3/6e822818ec644335be6f0ed231f48310",
//            chainId: 3,
//            rpcUrl: "https://ropsten.infura.io/v3/f911e0056b6845e2b71419434c5f08a8",
//            privacyMode: false
//        )
        let result = OKPyCommandsManager.sharedInstance().callInterface(OKSwiftHelper.dapp_eth_rpc_info(), parameter: nil) as? [String : Any]
        guard let map = result,
              let rpcUrl = map["rpc"] as? String,
              let chainId = map["chain_id"] as? Int else {
            return nil
        }
        return WKUserScriptConfig(address: address, chainId: chainId, rpcUrl: rpcUrl, privacyMode: false)
    }

    static func ethSignAndSendTx(transaction: DAppTransaction,
                password: String,
                completion: @escaping (Result<String, EthSignAndSendTxError>) -> Void) {

        guard let wallet = OKWalletManager.sharedInstance().currentWalletInfo else {
            completion(.failure(.missingParams))
            return
        }

        let parameter = [
            "transaction" : transaction.toMap().jsonString() ?? "",
            "password" : password,
            "path" : wallet.walletType == .hardware ? OKSwiftHelper.bluetooth_ios() : ""
        ]

        OKSwiftHelper.pyCallMethod(OKSwiftHelper.dapp_eth_sign_tx(), parameter: parameter) { result1 in
            if let result1Map = result1 as? [String : Any],
               let raw = result1Map["raw"] as? String,
               !raw.isEmpty
            {
                OKSwiftHelper.pyCallMethod(OKSwiftHelper.dapp_eth_send_tx(), parameter:  ["tx_hex" : raw]) { result2 in
                    if result2 != nil {
                        completion(.success(raw))
                    } else {
                        completion(.failure(.sendTxError))
                    }
                }
            } else {
                completion(.failure(.signTxError))
            }
        }
    }
    
    static func signMessage(
        wallet: OKWalletInfoModel,
        data: Data,
        password: String,
        personal: Bool,
        completion: @escaping (String) -> Void
    ) {
        let newData = personal ? ethereumMessage(for: data).keccak256.data(using: .utf8)! : data
        let message = newData.string(encoding: .utf8)!
        let parameter = [
            "address" : wallet.addr,
            "message" : message,
            "password" : password,
            "path" : wallet.walletType == .hardware ? OKSwiftHelper.bluetooth_ios() : ""
        ]
        OKSwiftHelper.pyCallMethod(OKSwiftHelper.sign_message(), parameter: parameter) { (result) in
            let signed = (result as? NSMutableString ?? "") as String
            completion(signed)
        }
    }
    
    static func ethereumMessage(for data: Data) -> Data {
        let prefix = "\u{19}Ethereum Signed Message:\n\(data.count)".data(using: .utf8)!
        return prefix + data
    }
    
    static func supportDAppCoinTypes() -> [String] {
        return ["ETH", "HECO", "BSC"]
    }

    // MARK: - UI Action

    static func handleOpenDApp(model: OKWebJSModel) {

        guard let data = model.jsParams() else { return }
        guard let chain = data.chain else { return }
        guard let url = data.url, !url.isEmpty else { return }
        
        let dappCoinType = chain.uppercased()
        
        if !DAppWebManage.supportDAppCoinTypes().contains(dappCoinType) {
            PanBottomAlertViewController.show(
                icon: nil,
                title: "Temporary does not support".localized + " " + chain.uppercased(),
                content: "Stay tuned".localized,
                leftAction: .init(normalTitle: "cancel".localized, onTap: nil),
                rightAction: .init(highlightTitle: "determine".localized, onTap: {

                })
            )
            return
        }
        
        guard let wallet = OKWalletManager.sharedInstance().currentWalletInfo else { return }

        if wallet.coinType.uppercased() != dappCoinType || wallet.walletType == .observe {
            PanBottomAlertViewController.show(
                icon: nil,
                title: String(format: "Switch to %@ account?".localized, dappCoinType),
                content: String(format: "Current DApp only supports %@ account".localized, dappCoinType),
                leftAction: .init(normalTitle: "cancel".localized, onTap: nil),
                rightAction: .init(highlightTitle: "determine".localized, onTap: {
                    let page = OKChangeWalletController.withStoryboard()
                    page.chianType = .ethLike
                    page.walletChangedCallback = { _ in }
                    page.modalPresentationStyle = .overCurrentContext
                    OKTools.ok_TopViewController().present(page, animated: false, completion: nil)
                })
            )
            return
        }

        func goDAppBroswer() {
            let page = OKDAppWebViewController.instance(homepage: url)
            UIApplication.shared.keyWindow?.rootViewController?.presentPanModal(page)
        }

        let key = "OneKey-DApp:" + url

        if let value = UserDefaults.standard[key] as? String, !value.isEmpty {
            goDAppBroswer()
        } else {
            let content = String(format: "Your use of third-party DApps will be applicable to the third-party DApp’s \"Privacy Policy\" and \"User Agreement\", and %@ will be directly and solely liable to you".localized, data.name ?? "aapp")
            PanBottomAlertViewController.show(
                icon: data.img == nil ? nil : .init(remoteImageURLString: data.img!),
                title: data.name ?? "DApp",
                content: content,
                leftAction: .init(normalTitle: "cancel".localized, onTap: nil),
                rightAction: .init(highlightTitle: "Continue to visit".localized, onTap: {
                    UserDefaults.standard[key] = url
                    goDAppBroswer()
                })
            )
        }

    }
    
    static func dealSignMessage(
        data: Data,
        personal: Bool,
        completion: @escaping (Result<String, EthSignAndSendTxError>) -> Void) {

        guard let wallet = OKWalletManager.sharedInstance().currentWalletInfo else {
            completion(.failure(.missingParams))
            return
        }
        guard !wallet.addr.isEmpty else {
            completion(.failure(.missingParams))
            return
        }
        let message = personal ? String(data: data, encoding: .utf8) ?? "" : data.hexString
        let page = OKSignMessageViewController.instance(address: wallet.addr, message: message)

        page.cancelAction = { [weak page] in
            page?.dismiss(animated: true, completion: nil)
            completion(.failure(.cancel))
        }
        
        page.finishWithPassword = { [weak page] password in

            let isHardwareWallet = wallet.walletType == .hardware
            var hardwareAlert: PanBottomAlertViewController?

            func signMessage() {
                OKTools.sharedInstance().showIndicatorView()
                DAppWebManage.signMessage(
                    wallet: wallet,
                    data: data,
                    password: password,
                    personal: personal
                ) { [weak hardwareAlert] signed in
                    OKTools.sharedInstance().hideIndicatorView()
                    if (isHardwareWallet) {
                        hardwareAlert?.dismiss(animated: true, completion: nil)
                    } else {
                        page?.dismiss(animated: true, completion: nil)
                    }
                    completion(.success(signed))
                }
            }

            if (isHardwareWallet) {
                page?.dismiss(animated: true) {
                    let connect = OKWallertTranferConnect()
                    hardwareAlert = hardwareConnectAlert {
                        connect.stop()
                        completion(.failure(.cancel))
                    }
                    connect.start()
                    connect.connectSuccessCallback = {
                        signMessage()
                    }
                    connect.connectFailedCallback = { error in
                        hardwareAlert?.dismiss(animated: true, completion: {
                            OKTools.sharedInstance().tipMessage(error)
                            completion(.failure(.hardwareNotConnected))
                        })
                    }
                }
            } else {
                signMessage()
            }
        }
        OKTools.ok_TopViewController().presentPanModal(page)
    }
    
    static func dealTransaction(
        json: [String : Any],
        completion: @escaping (Result<String, EthSignAndSendTxError>) -> Void
    ) {
        guard let wallet = OKWalletManager.sharedInstance().currentWalletInfo else {
            completion(.failure(.missingParams))
            return
        }
        guard let object = json["object"] as? [String : Any] else {
            completion(.failure(.missingParams))
            return
        }
        guard let from = object["from"] as? String, !from.isEmpty else {
            completion(.failure(.missingParams))
            return
        }
        guard let to = object["to"]  as? String, !to.isEmpty else {
            completion(.failure(.missingParams))
            return
        }
        guard let value = object["value"]  as? String, !value.isEmpty else {
            completion(.failure(.missingParams))
            return
        }

//        var gas: String? = object["gas"] as? String
//        let gasPrice: String? = object["gasPrice"] as? String
        let data: String = object["data"]  as? String ?? ""

        let coinType = wallet.coinType
        let transaction = DAppTransaction(
            from: from,
            to: to,
            value: value.hexToDecimal,
            gas: nil,
            gasPrice: nil,
//            gas: gas?.hexToDecimal,
//            gasPrice: gasPrice?.hexToDecimal,
            data: data,
            coinType: coinType,
            nonce: nil
        )

        let page = OKTradeInfoViewController.instance(transaction: transaction)
        let nav = PanModalNavViewController(viewControllers: [page])
        OKTools.ok_TopViewController().presentPanModal(nav)

        page.cancelAction = { [weak page] in
            page?.dismiss(animated: true)
            completion(.failure(.cancel))
        }
        
        var password = ""
        
        let group = DispatchGroup()

        // gas 为空接口获取
//        if gas == nil {
            group.enter()
            let amount = String(transaction.value).divEthereumUnit().stringValue
            OKHelperUtils.getDefaultFeeInfoCoinType(
                wallet.coinType,
                toAddress: to,
                amount: amount,
                data: data,
                contractAddress: nil
            ) { model in
                guard let model = model else { return }
                group.leave()
                page.updateDefaultFeeInfo(model: model)
            }
//        }

        group.enter()
        page.finishWithPassword = { (pwd, fee) in
            password = pwd
            if let fee = fee {
                transaction.gasPrice = fee.gas_price.toInt
                transaction.gas = fee.gas_limit.toInt
            }
            group.leave()
        }

        group.notify(queue: .main) {
            
            let isHardwareWallet = wallet.walletType == .hardware
            var hardwareAlert: PanBottomAlertViewController?

            func ethSignAndSendTx() {
                OKTools.sharedInstance().showIndicatorView()
                DAppWebManage.ethSignAndSendTx(transaction: transaction,
                                               password: password) { [weak page, hardwareAlert](result) in
                    OKTools.sharedInstance().hideIndicatorView()
                    if (isHardwareWallet) {
                        hardwareAlert?.dismiss(animated: true, completion: nil)
                    } else {
                        page?.dismiss(animated: true, completion: nil)
                    }
                    switch result {
                    case .success(let raw):
                        completion(.success(raw))
                        break
                    case .failure(let error):
                        completion(.failure(error))
                        break
                    }
                }
            }

            if (isHardwareWallet) {
                page.dismiss(animated: true) {
                    let connect = OKWallertTranferConnect()
                    hardwareAlert = hardwareConnectAlert {
                        connect.stop()
                        completion(.failure(.cancel))
                    }
                    connect.start()
                    connect.connectSuccessCallback = {
                        ethSignAndSendTx()
                    }
                    connect.connectFailedCallback = { [weak hardwareAlert] error in
                        hardwareAlert?.dismiss(animated: true, completion: {
                            OKTools.sharedInstance().tipMessage(error)
                            completion(.failure(.hardwareNotConnected))
                        })
                    }
                }
            } else {
                ethSignAndSendTx()
            }
        }
    }

    static func hardwareConnectAlert(onTap: (()->Void)?) -> PanBottomAlertViewController {
        return PanBottomAlertViewController.show(
            icon: .init(lodingImage: nil),
            title: "Connecting".localized,
            content: "Please unlock the hardware wallet and keep it within the effective range of the mobile phone’s Bluetooth".localized,
            leftAction: .init(
                normalTitle: "cancel".localized,
                onTap: {
                    onTap?()
        }),
            rightAction: nil
        )
    }
}
