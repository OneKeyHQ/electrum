//
//  OKDiscoverViewController.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/9.
//  Copyright © 2021 Onekey. All rights reserved.
//

import UIKit
import Reusable
import PanModal
import WKWebViewJavascriptBridge

final class OKDiscoverViewController: UIViewController {

    @IBOutlet weak var webView: WKWebView!
    @IBOutlet weak var activity: UIActivityIndicatorView!

   private  var networkErrorView: NetworkErrorView?

    var bridge: WKWebViewJavascriptBridge!

    @objc class func instance() -> OKDiscoverViewController {
        return OKDiscoverViewController.instantiate()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: animated)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        setBackgroundColor(color: .white)

        networkErrorView = NetworkErrorView.addParentView(
            parentView: view,
            handler: { [weak self] in
                guard let self = self else { return }
                self.webView.reload()
        })

        // setup bridge
        bridge = WKWebViewJavascriptBridge(webView: webView)
        bridge.isLogEnable = isDebug
        bridge.register(handlerName: "callNativeMethod") { (paramters, callback) in
            print("testiOSCallback called: \(String(describing: paramters))")
            let model = OKWebJSModel(paramters: paramters)
            switch model.jsAction() {
            case .openDapp:
                callback?(["id":  model.id, "result" : "success"])
                DAppWebManage.handleOpenDApp(model: model)
                break
            case .unknow:
                break
            }
        }

        webView.scrollView.keyboardDismissMode = .onDrag
        webView.scrollView.showsVerticalScrollIndicator = false
        webView.scrollView.showsHorizontalScrollIndicator = false
        webView.navigationDelegate = self
        loadUrl(url: "https://dapp.onekey.so/")
    }

    private func loadUrl(url: String) {
        guard let url = URL(string: url) else { return }
        let request = URLRequest(
            url: url,
            cachePolicy: .reloadRevalidatingCacheData,
            timeoutInterval: 30
        )
        webView.load(request)
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

    private func setBackgroundColor(color: UIColor) {
        view.backgroundColor = color
        webView.backgroundColor = color
        webView.scrollView.backgroundColor = color
    }

}

extension OKDiscoverViewController: WKNavigationDelegate {

    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        decisionHandler(WKNavigationActionPolicy.allow)
    }

    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        print("webViewDidStartLoad")
    }

    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        print("webViewDidFinishLoad")
        if activity.isAnimating {
            setBackgroundColor(color: .bg_W02())
            activity.stopAnimating()
        }
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

}

extension OKDiscoverViewController: StoryboardSceneBased {
    static let sceneStoryboard = UIStoryboard(name: "Tab_Discover", bundle: nil)
    static var sceneIdentifier = "OKDiscoverViewController"
}
