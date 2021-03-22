//
//  WKWebViewObserver.swift
//  OneKey
//
//  Created by xuxiwen on 2021/3/17.
//  Copyright © 2021 Onekey. All rights reserved.
//

import UIKit
import WebKit

public enum WKWebViewKVOKeyPath: String, CaseIterable {
    case estimatedProgress = "estimatedProgress"
    case canGoBack = "canGoBack"
    case canGoForward = "canGoForward"
    case contentSize = "scrollView.contentSize"
    case loading = "loading"
    case title = "title"
    case URL = "URL"
        
    public var others: [WKWebViewKVOKeyPath] {
        return WKWebViewKVOKeyPath.allCases.filter { $0 != self }
    }
}

public class WKWebViewObserver: NSObject {
    weak var webView: WKWebView!
    
    public init(webView: WKWebView) {
        super.init()
        
        self.webView = webView
        
        WKWebViewKVOKeyPath.allCases.forEach { webView.addObserver(self, forKeyPath: $0.rawValue, options: .new, context: nil) }
    }
    
    deinit {
        WKWebViewKVOKeyPath.allCases.forEach { webView.removeObserver(self, forKeyPath: $0.rawValue) }
    }
    
    public typealias WKWebViewObserverOnChangeLoading = (WKWebViewObserver, Bool) -> Void
    public typealias WKWebViewObserverOnChangeEstimatedProgress = (WKWebViewObserver, Float) -> Void
    public typealias WKWebViewObserverOnChangeURL = (WKWebViewObserver, URL) -> Void
    public typealias WKWebViewObserverOnChangeTitle = (WKWebViewObserver, String) -> Void
    public typealias WKWebViewObserverOnChangeCanGoBack = (WKWebViewObserver, Bool) -> Void
    public typealias WKWebViewObserverOnChangeCanGoForward = (WKWebViewObserver, Bool) -> Void
    public typealias WKWebViewObserverOnChangeContentSize = (WKWebViewObserver, CGSize) -> Void
    
    private var loadingCallback: WKWebViewObserverOnChangeLoading?
    private var estimatedProgressCallback: WKWebViewObserverOnChangeEstimatedProgress?
    private var urlCallback: WKWebViewObserverOnChangeURL?
    private var titleCallback: WKWebViewObserverOnChangeTitle?
    private var canGoBackCallback: WKWebViewObserverOnChangeCanGoBack?
    private var canGoForwardCallback: WKWebViewObserverOnChangeCanGoForward?
    private var contentSizeCallback: WKWebViewObserverOnChangeContentSize?
    
    @discardableResult
    public func onChangeLoading(callback: @escaping WKWebViewObserverOnChangeLoading) -> Self {
        loadingCallback = callback
        return self
    }
    
    @discardableResult
    public func onChangeEstimatedProgress(callback: @escaping WKWebViewObserverOnChangeEstimatedProgress) -> Self {
        estimatedProgressCallback = callback
        return self
    }
    
    @discardableResult
    public func onChangeURL(callback: @escaping WKWebViewObserverOnChangeURL) -> Self {
        urlCallback = callback
        return self
    }
    
    @discardableResult
    public func onChangeTitle(callback: @escaping WKWebViewObserverOnChangeTitle) -> Self {
        titleCallback = callback
        return self
    }
    
    @discardableResult
    public func onChangeCanGoBack(callback: @escaping WKWebViewObserverOnChangeCanGoBack) -> Self {
        canGoBackCallback = callback
        return self
    }
    
    @discardableResult
    public func onChangeCanGoForward(callback: @escaping WKWebViewObserverOnChangeCanGoForward) -> Self {
        canGoForwardCallback = callback
        return self
    }
    
    @discardableResult
    public func onChangeContentSize(callback: @escaping WKWebViewObserverOnChangeContentSize) -> Self {
        contentSizeCallback = callback
        return self
    }
    
    override public func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        guard let kp = keyPath, let path = WKWebViewKVOKeyPath(rawValue: kp),
            let webView = object as? WKWebView, self.webView == webView else {
                super.observeValue(forKeyPath: keyPath, of: object, change: change, context: context)
                return
        }
        
        switch path {
        case .loading:
            guard let loading = change?[.newKey] as? Bool else { return }
            loadingCallback?(self, loading)
            
        case .estimatedProgress:
            guard let p = change?[.newKey] as? Double else { return }
            estimatedProgressCallback?(self, Float(p))
            
        case .URL: urlCallback?(self, webView.url!)
        case .title: titleCallback?(self, webView.title!)
            
        case .canGoBack:
            guard let can = change?[.newKey] as? Bool else { return }
            canGoBackCallback?(self, can)
            
        case .canGoForward:
            guard let can = change?[.newKey] as? Bool else { return }
            canGoForwardCallback?(self, can)
            
        case .contentSize: contentSizeCallback?(self, webView.scrollView.contentSize)
        }
    }
}
