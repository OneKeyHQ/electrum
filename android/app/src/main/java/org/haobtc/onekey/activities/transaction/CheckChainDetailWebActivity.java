package org.haobtc.onekey.activities.transaction;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.utils.MyDialog;
import org.haobtc.onekey.utils.internet.NetBroadcastReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckChainDetailWebActivity extends BaseActivity implements NetBroadcastReceiver.NetStatusMonitor {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.web_heckChain)
    WebView webHeckChain;
    @BindView(R.id.lin_Nonet)
    LinearLayout linNonet;
    @BindView(R.id.text_title)
    TextView textTitle;
    private String checkTxid;
    private int nets = 2;
    private boolean netStatus;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 99) {
                nets = 2;
                webHeckChain.setVisibility(View.VISIBLE);
                linNonet.setVisibility(View.GONE);
            } else {
                mToast(getString(R.string.net_dont_use));
                nets = 4;
                webHeckChain.setVisibility(View.GONE);
                linNonet.setVisibility(View.VISIBLE);
            }
        }
    };
    private MyDialog myDialog;
    private NetBroadcastReceiver netBroadcastReceiver;
    private String blockServerLine;
    private String keyLink;
    private String loadUrl = "";

    public static void gotoCheckChainDetailWebActivity (Context context, String loadWhere, String loadUrl) {
        Intent intent = new Intent(context, CheckChainDetailWebActivity.class);
        intent.putExtra("loadWhere", loadWhere);
        intent.putExtra("loadUrl", loadUrl);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId () {
        return R.layout.activity_check_chain_detail_web;
    }

    @Override
    public void initView () {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        blockServerLine = preferences.getString("blockServerLine", "https://btc.com/");
        myDialog = MyDialog.showDialog(CheckChainDetailWebActivity.this);
        myDialog.show();
        Intent intent = getIntent();
        String loadWhere = intent.getStringExtra("loadWhere");
        if ("userAgreement".equals(loadWhere)) {
            loadUrl = intent.getStringExtra("loadUrl");
            textTitle.setText(getString(R.string.user_agreement));
        } else if ("privacyAgreement".equals(loadWhere)) {
            loadUrl = intent.getStringExtra("loadUrl");
            textTitle.setText(getString(R.string.privacy_agreement));
        } else {
            checkTxid = intent.getStringExtra("checkTxid");
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
        webHeckChain.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                myDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                myDialog.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                myDialog.dismiss();
            }
        });

        if (nets == 2) {
            if (!TextUtils.isEmpty(loadUrl)) {
                webHeckChain.loadUrl(loadUrl);
            } else {
                if (!TextUtils.isEmpty(keyLink)) {
                    textTitle.setText(getString(R.string.key));
                    webHeckChain.loadUrl(keyLink);
                } else {
                    webHeckChain.loadUrl(blockServerLine + checkTxid);
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
        this.netStatus = netStatus;
        isNetConnect();
    }

    private void isNetConnect() {
        Message message = new Message();
        if (netStatus) {
            message.what = 99;
            handler.sendMessage(message);
        } else {
            Toast.makeText(this, getString(R.string.net_dont_use), Toast.LENGTH_SHORT).show();
            message.what = 100;
            handler.sendMessage(message);
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

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
    }

}
