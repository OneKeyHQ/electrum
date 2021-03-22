
import Foundation
import WebKit

struct WKUserScriptConfig {

    let address: String
    let chainId: Int
    let rpcUrl: String
    let privacyMode: Bool

    var providerJsBundleUrl: URL {
        let bundlePath = Bundle.main.bundlePath
        let bundle = Bundle(path: bundlePath)!
        return bundle.url(forResource: "onekey-min", withExtension: "js")!
    }

    var providerScript: WKUserScript {
        let source = try! String(contentsOf: providerJsBundleUrl)
        let script = WKUserScript(source: source, injectionTime: .atDocumentStart, forMainFrameOnly: false)
        return script
    }

    var injectedScript: WKUserScript {
        let source: String
        if privacyMode {
            source = """
            (function() {
                var config = {
                    chainId: \(chainId),
                    rpcUrl: "\(rpcUrl)"
                };
                const provider = new window.Trust(config);
                window.ethereum = provider;

                window.chrome = {webstore: {}};
            })();
            """
        } else {
            source = """
            (function() {
                var config = {
                    address: "\(address)".toLowerCase(),
                    chainId: \(chainId),
                    rpcUrl: "\(rpcUrl)"
                };
                const provider = new window.Trust(config);
                provider.isDebug = true;
                window.ethereum = provider;
                window.web3 = new window.Web3(provider);
                window.web3.eth.defaultAccount = config.address;

                window.chrome = {webstore: {}};
            })();
            """
        }
        let script = WKUserScript(source: source, injectionTime: .atDocumentStart, forMainFrameOnly: false)
        return script
    }
}
