
import Foundation
import WebKit

extension WKWebView {
    public func sendError(_ error: String, to id: Int64) {
        let script = String(format: "window.ethereum.sendError(%ld, \"%@\")", id, error)
        print(script)
        evaluateJavaScript(script)
    }

    public func sendResult(_ result: String, to id: Int64) {
        let script = String(format: "window.ethereum.sendResponse(%ld, \"%@\")", id, result)
        print(script)
        evaluateJavaScript(script)
    }

    public func sendResults(_ results: [String], to id: Int64) {
        let array = results.map { String(format: "\"%@\"", $0) }
        let script = String(format: "window.ethereum.sendResponse(%ld, [%@])", id, array.joined(separator: ","))
        print(script)
        evaluateJavaScript(script)
    }
}
