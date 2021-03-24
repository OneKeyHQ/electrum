
import UIKit
import WebKit
import Reusable
import PanModal

final class OKDAppWebViewController: UIViewController {

    @IBOutlet weak var tokenImage: UIImageView!
    @IBOutlet weak var navTitle: UILabel!
    @IBOutlet weak var leftButton: UIButton!
    @IBOutlet weak var leftButtonWidth: NSLayoutConstraint!
    @IBOutlet weak var leftSplitLine: UIView!
    @IBOutlet weak var leftSpliteLineWidth: NSLayoutConstraint!
    @IBOutlet weak var accountName: UILabel!

    private  var networkErrorView: NetworkErrorView?

    private  var requestAccountsId: Int64 = 0
    var webViewObserver: WKWebViewObserver!

    @objc class func instance(homepage: String) -> OKDAppWebViewController {
        let page = OKDAppWebViewController.instantiate()
        page.homepage = homepage
        return page
    }

    private var homepage = ""

    private lazy var address: String = {
        let wallet = OKWalletManager.sharedInstance().currentWalletInfo;
        return wallet?.addr ?? ""
    }()

    private lazy var progressView: UIProgressView = {
        let progressView = UIProgressView(progressViewStyle: .default)
        progressView.tintColor = .tintBrand()
        progressView.trackTintColor = UIColor.tintBrand().withAlphaComponent(0.1)
        return progressView
    }()

    private var webView: WKWebView!

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: animated)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setUpWebView()
        setupSubviews()
        configurationContent()
//        homepage = "https://app.uniswap.org/#/swap"
//        homepage = "https://zksync.io/"
        navigate(to: homepage)
    }

    private func setUpWebView() {
        let config = WKWebViewConfiguration()
        let controller = WKUserContentController()
        if let scriptConfig = DAppWebManage.fetchScriptConfig() {
            controller.addUserScript(scriptConfig.providerScript)
            controller.addUserScript(scriptConfig.injectedScript)
        }
        for name in DAppMethod.allCases {
            controller.add(self, name: name.rawValue)
        }
        config.userContentController = controller
        webView = WKWebView(frame: .zero, configuration: config)
        webView.uiDelegate = self
        webView.navigationDelegate = self
    }

    private func setupSubviews() {
        view.addSubview(webView)
        view.addSubview(progressView)
        webView.anchor(
            top: view.safeAreaLayoutGuide.topAnchor,
            left: view.leftAnchor,
            bottom: view.bottomAnchor,
            right: view.rightAnchor,
            topConstant: 44
        )
        progressView.anchor(
            top: view.safeAreaLayoutGuide.topAnchor,
            left: webView.leftAnchor,
            right: webView.rightAnchor,
            topConstant: 44,
            heightConstant: 2
        )

        networkErrorView = NetworkErrorView.addParentView(
            parentView: webView,
            handler: { [weak self] in
                guard let self = self else { return }
                self.webView.reload()
        })

    }

    private func configurationContent() {
        let wallet = OKWalletManager.sharedInstance().currentWalletInfo
        accountName.text = wallet?.label ?? ""
        tokenImage.image = (wallet?.coinType ?? "").coinImage

        updateWebViewCanGoBack(flag: false)

        webViewObserver = WKWebViewObserver(webView: webView)
            .onChangeEstimatedProgress { [weak self] _, progress in
                guard let self = self else { return }
                self.progressView.progress = progress
                self.progressView.isHidden = progress == 1
            }.onChangeCanGoBack { [weak self]  _, can in
                guard let self = self else { return }
                self.updateWebViewCanGoBack(flag: can)
            }.onChangeTitle { [weak self] _, title in
                guard let self = self else { return }
                self.navTitle.text = title
            }

        leftButton.onTap { [weak self] in
            guard let self = self else { return }
            if (self.webView.canGoBack) {
                self.webView.goBack()
            }
        }

    }

    private func navigate(to url: String) {
        navTitle.text = url
        guard let url = URL(string: url) else { return }
        let request = URLRequest(
            url: url,
            cachePolicy: .reloadRevalidatingCacheData,
            timeoutInterval: 30
        )
        webView.load(request)
    }

    @IBAction func selectAccount(_ sender: Any) {
        let page = OKChangeWalletController.withStoryboard()
        page.chianType = [.ETH]
        page.walletChangedCallback = { [weak self] value in
            if value.addr != self?.address {
                self?.updateAccount()
                self?.webView.reload()
            }
        }
        page.modalPresentationStyle = .overCurrentContext
        self.present(page, animated: false, completion: nil)
    }

    @IBAction func tapMenuButton(_ sender: Any) {

    }

    @IBAction func tapCloseButton(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }

    private func updateWebViewCanGoBack(flag: Bool) {
        leftButton.isEnabled = flag
        leftButton.isHidden = !flag
        leftButtonWidth.constant = flag ? 35.5 : 0
        leftSpliteLineWidth.constant = flag ? 0.5 : 0
    }

    private func updateAccount() {
        guard let wallet = OKWalletManager.sharedInstance().currentWalletInfo else { return }
        accountName.text = wallet.label
        address = wallet.addr
        updateRPCInfo()
        handleRequestAccounts()
    }

    private func updateRPCInfo() {
        guard let scriptConfig = DAppWebManage.fetchScriptConfig() else { return }
        webView.configuration.userContentController.addUserScript(scriptConfig.injectedScript)
    }

}

extension OKDAppWebViewController: WKScriptMessageHandler {
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        let json = message.json
        print(json)
        guard let name = json["name"] as? String,
            let method = DAppMethod(rawValue: name),
            let id = json["id"] as? Int64 else {
            return
        }

        switch method {
        case .requestAccounts:
            requestAccountsId = id
            handleRequestAccounts()
        case .signMessage:
            guard let data = extractMessage(json: json) else {
                print("data is missing")
                return
            }
            handleSignMessage(id: id, data: data, personal: false)
            break
        case .signPersonalMessage:
            guard let data = extractMessage(json: json) else {
                print("data is missing")
                return
            }
            handleSignMessage(id: id, data: data, personal: true)
            break
        case .ecRecover:
            guard let tuple = extractSignature(json: json) else {
                print("signature or message is missing")
                return
            }
            let recovered = ecRecover(signature: tuple.signature, message: tuple.message) ?? ""
            print(recovered)
            DispatchQueue.main.async {
                self.webView.sendResult(recovered, to: id)
            }
            break
        case .signTransaction:
            DAppWebManage.dealTransaction(json: json) { [weak self] result in
                guard let self = self else { return }
                switch result {
                case .success(let raw):
                    self.webView.sendResult(raw, to: id)
                    break
                case .failure(let error):
                    OKTools.sharedInstance().tipMessage(error.errorMsg)
                    self.webView.sendError(error.errorMsg, to: id)
                    break
                }
            }
            break
        default:
            break
        }
    }

    private func handleRequestAccounts() {
        webView.sendResults([address], to: requestAccountsId)
    }

    private func handleSignMessage(id: Int64, data: Data, personal: Bool) {
        DAppWebManage.dealSignMessage(data: data, personal: personal) { [weak self] result in
            guard let `self` = self else { return }
            switch result {
            case .success(let signed):
                self.webView.sendResult(signed, to: id)
                break
            case .failure(let error):
            self.webView.sendError(error.errorMsg, to: id)
            }
        }
    }

    private func extractMessage(json: [String: Any]) -> Data? {
        guard let params = json["object"] as? [String: Any],
            let string = params["data"] as? String,
            let data = Data(hexString: string) else {
            return nil
        }
        return data
    }

    private func extractSignature(json: [String: Any]) -> (signature: Data, message: Data)? {
        guard let params = json["object"] as? [String: Any],
            let signature = params["signature"] as? String,
            let message = params["message"] as? String else {
            return nil
        }
        return (Data(hexString: signature)!, Data(hexString: message)!)
    }

    // TODO ecRecover
    private func ecRecover(signature: Data, message: Data) -> String? {
//        let data = ethereumMessage(for: message)
//        let hash = Hash.keccak256(data: data)
//        guard let publicKey = PublicKey.recover(signature: signature, message: hash),
//            PublicKey.isValid(data: publicKey.data, type: publicKey.keyType) else {
//            return nil
//        }
//        return CoinType.ethereum.deriveAddressFromPublicKey(publicKey: publicKey).lowercased()
        return nil
    }

    private func ethereumMessage(for data: Data) -> Data {
        let prefix = "\u{19}Ethereum Signed Message:\n\(data.count)".data(using: .utf8)!
        return prefix + data
    }

    private func hideErrorView() {
        networkErrorView?.updateVisible(visible: false)
    }

    private func handleError(error: Error) {
        if error.code == NSURLErrorCancelled {
            return
        } else {
            networkErrorView?.updateVisible(visible: true)
        }
    }

}

extension OKDAppWebViewController: WKNavigationDelegate {

    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        print("webViewDidStartLoad")
    }

    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        print("webViewDidFinishLoad")
    }

    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        hideErrorView()
    }

    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        handleError(error: error)
    }

    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        handleError(error: error)
    }

    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        guard let url = navigationAction.request.url, let scheme = url.scheme else {
            return decisionHandler(.allow)
        }
        let app = UIApplication.shared
        if ["tel", "mailto"].contains(scheme), app.canOpenURL(url) {
            app.open(url)
            return decisionHandler(.cancel)
        }
        decisionHandler(.allow)
    }
}

extension OKDAppWebViewController: WKUIDelegate {
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        guard navigationAction.request.url != nil else {
           return nil
        }
        _ = webView.load(navigationAction.request)
        return nil
    }

    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
//        let alert = UIAlertController(title: "", message: message, preferredStyle: .alert)
//        alert.addAction(.init(title: "OK", style: .default, handler: { _ in
//            completionHandler()
//        }))
//        present(alert, animated: true, completion: nil)
    }

    func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping (Bool) -> Void) {
//        let alert = UIAlertController(title: "", message: message, preferredStyle: .alert)
//        alert.addAction(.init(title: "OK", style: .default, handler: { _ in
//            completionHandler(true)
//        }))
//        alert.addAction(.init(title: "Cancel", style: .cancel, handler: { _ in
//            completionHandler(false)
//        }))
//        present(alert, animated: true, completion: nil)
    }
}

extension OKDAppWebViewController: StoryboardSceneBased {
    static let sceneStoryboard = UIStoryboard(name: "Tab_Discover", bundle: nil)
    static var sceneIdentifier = "DAppWebViewController"
}

extension OKDAppWebViewController: PanModalPresentable {

    var panScrollable: UIScrollView? {
        return nil
    }

    var topOffset: CGFloat {
        return 0.0
    }

    var springDamping: CGFloat {
        return 1.0
    }

    var transitionDuration: Double {
        return 0.4
    }

    var transitionAnimationOptions: UIView.AnimationOptions {
        return [.allowUserInteraction, .beginFromCurrentState]
    }

    var shouldRoundTopCorners: Bool {
        return false
    }

    var showDragIndicator: Bool {
        return false
    }
}
