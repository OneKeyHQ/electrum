package org.haobtc.onekey.onekeys.dappbrowser;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import com.orhanobut.logger.Logger;
import java.math.BigInteger;
import java.net.URI;
import org.haobtc.onekey.onekeys.dappbrowser.bean.Address;
import org.haobtc.onekey.onekeys.dappbrowser.bean.CryptoFunctions;
import org.haobtc.onekey.onekeys.dappbrowser.bean.EthereumMessage;
import org.haobtc.onekey.onekeys.dappbrowser.bean.EthereumTypedMessage;
import org.haobtc.onekey.onekeys.dappbrowser.bean.SignMessageType;
import org.haobtc.onekey.onekeys.dappbrowser.bean.Web3Transaction;
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignMessageListener;
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignPersonalMessageListener;
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignTransactionListener;
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignTypedMessageListener;
import org.haobtc.onekey.utils.HexUtils;
import org.json.JSONObject;

public class SignCallbackJSInterface {

    private final WebView webView;
    @NonNull private final OnSignTransactionListener onSignTransactionListener;
    @NonNull private final OnSignMessageListener onSignMessageListener;
    @NonNull private final OnSignPersonalMessageListener onSignPersonalMessageListener;
    @NonNull private final OnSignTypedMessageListener onSignTypedMessageListener;

    public SignCallbackJSInterface(
            WebView webView,
            @NonNull OnSignTransactionListener onSignTransactionListener,
            @NonNull OnSignMessageListener onSignMessageListener,
            @NonNull OnSignPersonalMessageListener onSignPersonalMessageListener,
            @NonNull OnSignTypedMessageListener onSignTypedMessageListener) {
        this.webView = webView;
        this.onSignTransactionListener = onSignTransactionListener;
        this.onSignMessageListener = onSignMessageListener;
        this.onSignPersonalMessageListener = onSignPersonalMessageListener;
        this.onSignTypedMessageListener = onSignTypedMessageListener;
    }

    @JavascriptInterface
    public void signTransaction(
            int callbackId,
            String recipient,
            String value,
            String nonce,
            String gasLimit,
            String gasPrice,
            String payload) {
        if (value.equals("undefined") || value == null) {
            value = "0";
        }
        if (gasPrice == null) {
            gasPrice = "0";
        }
        Web3Transaction transaction =
                new Web3Transaction(
                        TextUtils.isEmpty(recipient) ? Address.EMPTY : new Address(recipient),
                        null,
                        HexUtils.hexToBigInteger(value),
                        HexUtils.hexToBigInteger(gasPrice, BigInteger.ZERO),
                        HexUtils.hexToBigInteger(gasLimit, BigInteger.ZERO),
                        HexUtils.hexToLong(nonce, -1),
                        payload,
                        callbackId);
        Logger.e(transaction.toString());
        webView.post(() -> onSignTransactionListener.onSignTransaction(transaction, getUrl()));
    }

    @JavascriptInterface
    public void signMessage(int callbackId, String data) {
        webView.post(
                () ->
                        onSignMessageListener.onSignMessage(
                                new EthereumMessage(
                                        data, getUrl(), callbackId, SignMessageType.SIGN_MESSAGE)));
    }

    @JavascriptInterface
    public void signPersonalMessage(int callbackId, String data) {
        webView.post(
                () ->
                        onSignPersonalMessageListener.onSignPersonalMessage(
                                new EthereumMessage(
                                        data,
                                        getUrl(),
                                        callbackId,
                                        SignMessageType.SIGN_PERSONAL_MESSAGE)));
    }

    @JavascriptInterface
    public void signTypedMessage(int callbackId, String data) {
        webView.post(
                () -> {
                    try {
                        JSONObject obj = new JSONObject(data);
                        String address = obj.getString("from");
                        String messageData = obj.getString("data");
                        CryptoFunctions cryptoFunctions = new CryptoFunctions();

                        EthereumTypedMessage message =
                                new EthereumTypedMessage(
                                        messageData, getDomainName(), callbackId, cryptoFunctions);
                        onSignTypedMessageListener.onSignTypedMessage(message);
                    } catch (Exception e) {
                        EthereumTypedMessage message =
                                new EthereumTypedMessage(null, "", getDomainName(), callbackId);
                        onSignTypedMessageListener.onSignTypedMessage(message);
                        e.printStackTrace();
                    }
                });
    }

    private String getUrl() {
        return webView == null ? "" : webView.getUrl();
    }

    private String getDomainName() {
        return webView == null ? "" : getDomainName(webView.getUrl());
    }

    private static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (Exception e) {
            return url != null ? url : "";
        }
    }
}
