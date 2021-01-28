package org.haobtc.onekey.activities.transaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.StringConstant;
import org.haobtc.onekey.utils.MyDialog;
import org.haobtc.onekey.utils.WebViewPxoxyUtils;
import org.haobtc.onekey.utils.internet.NetBroadcastReceiver;

public class CheckChainDetailWebActivity extends BaseActivity
        implements NetBroadcastReceiver.NetStatusMonitor, Handler.Callback {
    private static final String HOST = "cdn.onekey.so:";
    private static final int PORT = 443;
    private static final String USER_NAME = "onekey";
    private static final String PASS = "libbitcoinconsensus";

    private static final String EXT_WEB_TITLE = "ext_web_title";
    private static final String EXT_WEB_LOAD_URL = "ext_web_load_url";

    private static final int OPT_MESSAGE_NOT_NETWORK = 100;
    private static final int OPT_MESSAGE_HAVE_NETWORK = 99;

    private static final int HAVE_NETWORK = 2;
    private static final int NOT_NETWORK = 4;

    @BindView(R.id.img_back)
    ImageView imgBack;

    @BindView(R.id.web_heckChain)
    WebView webHeckChain;

    @BindView(R.id.lin_Nonet)
    LinearLayout linNonet;

    @BindView(R.id.text_title)
    TextView textTitle;

    private int mCurrentNetStatus = HAVE_NETWORK;
    private boolean mTempNetStatus;

    private final Handler mHandler = new Handler(this);

    private MyDialog myDialog;
    private NetBroadcastReceiver netBroadcastReceiver;
    private String keyLink;
    private String loadUrl = "";

    public static void start(Context context, String loadWhere, String loadUrl) {
        Intent intent = new Intent(context, CheckChainDetailWebActivity.class);
        intent.putExtra("loadWhere", loadWhere);
        intent.putExtra("loadUrl", loadUrl);
        context.startActivity(intent);
    }

    public static void startWebUrl(Context context, String title, String loadUrl) {
        Intent intent = new Intent(context, CheckChainDetailWebActivity.class);
        intent.putExtra(EXT_WEB_TITLE, title);
        intent.putExtra(EXT_WEB_LOAD_URL, loadUrl);
        context.startActivity(intent);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == OPT_MESSAGE_HAVE_NETWORK) {
            mCurrentNetStatus = HAVE_NETWORK;
            webHeckChain.setVisibility(View.VISIBLE);
            linNonet.setVisibility(View.GONE);
        } else {
            mToast(getString(R.string.net_dont_use));
            mCurrentNetStatus = NOT_NETWORK;
            webHeckChain.setVisibility(View.GONE);
            linNonet.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_chain_detail_web;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

        myDialog = MyDialog.showDialog(CheckChainDetailWebActivity.this);
        myDialog.onTouchOutside(true);
        myDialog.show();
        Intent intent = getIntent();
        String customLoadUrl = intent.getStringExtra(EXT_WEB_LOAD_URL);

        String loadWhere = intent.getStringExtra("loadWhere");
        loadUrl = intent.getStringExtra("loadUrl");
        if (StringConstant.USER_AGREEMENT.equals(loadWhere)) {
            textTitle.setText(getString(R.string.user_agreement));
        } else if (StringConstant.PRI_POLICY.equals(loadWhere)) {
            textTitle.setText(getString(R.string.privacy_agreement));
        } else if (StringConstant.NEW_GUIDE.equals(loadWhere)) {
            textTitle.setText(getString(R.string.new_guide_tip));
        } else if (!TextUtils.isEmpty(customLoadUrl)) {
            textTitle.setText(intent.getStringExtra(EXT_WEB_TITLE));
            loadUrl = customLoadUrl;
        } else {
            keyLink = intent.getStringExtra("key_link");
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initData() {
        webHeckChain.getSettings().setAllowFileAccess(false);
        webHeckChain.getSettings().setJavaScriptEnabled(true);
        webHeckChain.getSettings().setAppCacheEnabled(true);
        webHeckChain.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webHeckChain.getSettings().setDomStorageEnabled(true);
        webHeckChain.setWebChromeClient(new WebChromeClient());
        webHeckChain.getSettings().setAllowFileAccess(false);
        webHeckChain.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        if (myDialog != null && !CheckChainDetailWebActivity.this.isFinishing()) {
                            myDialog.show();
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (myDialog != null && !CheckChainDetailWebActivity.this.isFinishing()) {
                            myDialog.dismiss();
                        }
                    }

                    @Override
                    public void onReceivedError(
                            WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        if (myDialog != null && !CheckChainDetailWebActivity.this.isFinishing()) {
                            myDialog.dismiss();
                        }
                    }
                });
        if (mCurrentNetStatus == HAVE_NETWORK) {
            if (!TextUtils.isEmpty(loadUrl)) {
                WebViewPxoxyUtils.setProxy(
                        webHeckChain,
                        HOST,
                        PORT,
                        mContext.getApplicationInfo().className,
                        USER_NAME,
                        PASS);
                webHeckChain.loadUrl(loadUrl);
            } else {
                if (!TextUtils.isEmpty(keyLink)) {
                    textTitle.setText(getString(R.string.key));
                    webHeckChain.loadUrl(keyLink);
                }
            }
        }
    }

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        if (netBroadcastReceiver == null) {
            netBroadcastReceiver = new NetBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netBroadcastReceiver, filter);
            netBroadcastReceiver.setStatusMonitor(this);
        }
    }

    @Override
    public void onNetChange(boolean netStatus) {
        this.mTempNetStatus = netStatus;
        isNetConnect();
    }

    private void isNetConnect() {
        Message message = new Message();
        if (mTempNetStatus) {
            message.what = OPT_MESSAGE_HAVE_NETWORK;
            mHandler.sendMessage(message);
        } else {
            mToast(getString(R.string.net_dont_use));
            message.what = OPT_MESSAGE_NOT_NETWORK;
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDialog.dismiss();
        if (webHeckChain != null) {
            webHeckChain.destroy();
        }
        if (netBroadcastReceiver != null) {
            unregisterReceiver(netBroadcastReceiver);
        }
        WebViewPxoxyUtils.revertBackProxy(webHeckChain, mContext.getApplicationInfo().className);
    }
}
