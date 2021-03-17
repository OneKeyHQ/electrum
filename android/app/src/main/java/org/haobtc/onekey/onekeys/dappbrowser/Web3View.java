package org.haobtc.onekey.onekeys.dappbrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.onekeys.dappbrowser.bean.EthereumMessage;
import org.haobtc.onekey.onekeys.dappbrowser.bean.EthereumTypedMessage;
import org.haobtc.onekey.onekeys.dappbrowser.bean.Signable;
import org.haobtc.onekey.onekeys.dappbrowser.bean.Web3Transaction;
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignMessageListener;
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignPersonalMessageListener;
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignTransactionListener;
import org.haobtc.onekey.onekeys.dappbrowser.listener.OnSignTypedMessageListener;
import org.json.JSONObject;

public class Web3View extends WebView {

    private static final String JS_PROTOCOL_CANCELLED = "cancelled";
    private static final String JS_PROTOCOL_ON_SUCCESSFUL = "executeCallback(%1$s, null, \"%2$s\")";
    private static final String JS_PROTOCOL_ON_FAILURE = "executeCallback(%1$s, \"%2$s\", null)";
    public static final long JS_SING_ID = 8888;

    @Nullable private OnSignTransactionListener onSignTransactionListener;
    @Nullable private OnSignMessageListener onSignMessageListener;
    @Nullable private OnSignPersonalMessageListener onSignPersonalMessageListener;
    @Nullable private OnSignTypedMessageListener onSignTypedMessageListener;
    private JsInjectorClient jsInjectorClient;
    private Web3ViewClient webViewClient;
    private URLLoadInterface loadInterface;

    public Web3View(@NonNull Context context) {
        super(context);
    }

    public Web3View(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Web3View(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(new WrapWebViewClient(webViewClient, client, jsInjectorClient));
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void init() {
        jsInjectorClient = new JsInjectorClient(getContext());
        webViewClient = new Web3ViewClient(jsInjectorClient, new UrlHandlerManager());
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (BuildConfig.DEBUG) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUserAgentString(
                webSettings.getUserAgentString()
                        + "OnekeyWallet(Platform=Android&AppVersion="
                        + BuildConfig.VERSION_NAME
                        + ")");
        WebView.setWebContentsDebuggingEnabled(true); // so devs can debug their scripts/pages
        addJavascriptInterface(
                new SignCallbackJSInterface(
                        this,
                        innerOnSignTransactionListener,
                        innerOnSignMessageListener,
                        innerOnSignPersonalMessageListener,
                        innerOnSignTypedMessageListener),
                "onekeyJsCall");
    }

    public void setWalletAddress(@NonNull String address) {
        jsInjectorClient.setWalletAddress(address);
    }

    @Nullable
    public String getWalletAddress() {
        return jsInjectorClient.getWalletAddress();
    }

    public void setChainId(int chainId) {
        jsInjectorClient.setChainId(chainId);
    }

    public int getChainId() {
        return jsInjectorClient.getChainId();
    }

    public void setRpcUrl(@NonNull String rpcUrl) {
        jsInjectorClient.setRpcUrl(rpcUrl);
    }

    public void setWebLoadCallback(URLLoadInterface iFace) {
        loadInterface = iFace;
    }

    @Nullable
    public String getRpcUrl() {
        return jsInjectorClient.getRpcUrl();
    }

    public void addUrlHandler(@NonNull UrlHandler urlHandler) {
        webViewClient.addUrlHandler(urlHandler);
    }

    public void removeUrlHandler(@NonNull UrlHandler urlHandler) {
        webViewClient.removeUrlHandler(urlHandler);
    }

    public void setOnSignTransactionListener(
            @Nullable OnSignTransactionListener onSignTransactionListener) {
        this.onSignTransactionListener = onSignTransactionListener;
    }

    public void setOnSignMessageListener(@Nullable OnSignMessageListener onSignMessageListener) {
        this.onSignMessageListener = onSignMessageListener;
    }

    public void setOnSignPersonalMessageListener(
            @Nullable OnSignPersonalMessageListener onSignPersonalMessageListener) {
        this.onSignPersonalMessageListener = onSignPersonalMessageListener;
    }

    public void setOnSignTypedMessageListener(
            @Nullable OnSignTypedMessageListener onSignTypedMessageListener) {
        this.onSignTypedMessageListener = onSignTypedMessageListener;
    }

    public void onSignTransactionSuccessful(Web3Transaction transaction, String signHex) {
        long callbackId = transaction.leafPosition;
        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, signHex);
    }

    public void onSignMessageSuccessful(Signable message, String signHex) {
        long callbackId = message.getCallbackId();
        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, signHex);
    }

    public void onCallFunctionSuccessful(long callbackId, String result) {
        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, result);
    }

    public void onCallFunctionError(long callbackId, String error) {
        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, error);
    }

    public void onSignError(Web3Transaction transaction, String error) {
        long callbackId = transaction.leafPosition;
        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, error);
    }

    public void onSignError(EthereumMessage message, String error) {
        long callbackId = message.leafPosition;
        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, error);
    }

    public void onSignCancel(long callbackId) {
        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, JS_PROTOCOL_CANCELLED);
    }

    private void callbackToJS(long callbackId, String function, String param) {
        String callback = String.format(function, callbackId, param);
        post(() -> evaluateJavascript(callback, value -> Log.d("WEB_VIEW", value)));
    }

    private final OnSignTransactionListener innerOnSignTransactionListener =
            new OnSignTransactionListener() {
                @Override
                public void onSignTransaction(Web3Transaction transaction, String url) {
                    if (onSignTransactionListener != null) {
                        onSignTransactionListener.onSignTransaction(transaction, url);
                    }
                }
            };

    private final OnSignMessageListener innerOnSignMessageListener =
            new OnSignMessageListener() {
                @Override
                public void onSignMessage(EthereumMessage message) {
                    if (onSignMessageListener != null) {
                        onSignMessageListener.onSignMessage(message);
                    }
                }
            };

    private final OnSignPersonalMessageListener innerOnSignPersonalMessageListener =
            new OnSignPersonalMessageListener() {
                @Override
                public void onSignPersonalMessage(EthereumMessage message) {
                    onSignPersonalMessageListener.onSignPersonalMessage(message);
                }
            };

    private final OnSignTypedMessageListener innerOnSignTypedMessageListener =
            new OnSignTypedMessageListener() {
                @Override
                public void onSignTypedMessage(EthereumTypedMessage message) {
                    onSignTypedMessageListener.onSignTypedMessage(message);
                }
            };

    public void setActivity(FragmentActivity activity) {
        webViewClient.setActivity(activity);
    }

    private class WrapWebViewClient extends WebViewClient {

        private final Web3ViewClient internalClient;
        private final WebViewClient externalClient;
        private final JsInjectorClient jsInjectorClient;
        private boolean loadingError = false;
        private boolean redirect = false;

        public WrapWebViewClient(
                Web3ViewClient internalClient,
                WebViewClient externalClient,
                JsInjectorClient jsInjectorClient) {
            this.internalClient = internalClient;
            this.externalClient = externalClient;
            this.jsInjectorClient = jsInjectorClient;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (loadInterface != null) {
                loadInterface.onWebpageBeginLoad(url, view.getTitle());
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (!redirect && !loadingError) {
                if (loadInterface != null) {
                    loadInterface.onWebpageLoaded(url, view.getTitle());
                }
            } else if (!loadingError && loadInterface != null) {
                loadInterface.onWebpageLoadComplete();
            }

            redirect = false;
            loadingError = false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            redirect = true;

            return externalClient.shouldOverrideUrlLoading(view, url)
                    || internalClient.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(
                WebView view, WebResourceRequest request, WebResourceError error) {
            loadingError = true;
            if (request.isForMainFrame()) {
                loadInterface.onWebpageLoadError();
            }
            if (externalClient != null) {
                externalClient.onReceivedError(view, request, error);
            }
        }

        @Override
        public void onReceivedHttpError(
                WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (request.isForMainFrame() && request.getUrl().getPath().equals(view.getUrl())) {
                int statusCode = errorResponse.getStatusCode();
                if (404 == statusCode || 500 == statusCode) {
                    loadingError = true;
                    loadInterface.onWebpageLoadError();
                }
            }
            if (externalClient != null) {
                externalClient.onReceivedHttpError(view, request, errorResponse);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            redirect = true;

            return externalClient.shouldOverrideUrlLoading(view, request)
                    || internalClient.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(
                WebView view, WebResourceRequest request) {
            WebResourceResponse response = externalClient.shouldInterceptRequest(view, request);
            if (response != null) {
                try {
                    InputStream in = response.getData();
                    int len = in.available();
                    byte[] data = new byte[len];
                    int readLen = in.read(data);
                    if (readLen == 0) {
                        throw new IOException("Nothing is read.");
                    }
                    String injectedHtml = jsInjectorClient.injectJS(new String(data));
                    response.setData(new ByteArrayInputStream(injectedHtml.getBytes()));
                } catch (IOException ex) {
                    Log.d("INJECT AFTER_EXTRNAL", "", ex);
                }
            } else {
                response = internalClient.shouldInterceptRequest(view, request);
            }
            return response;
        }
    }

    private static boolean isJson(String value) {
        try {
            JSONObject stateData = new JSONObject(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
