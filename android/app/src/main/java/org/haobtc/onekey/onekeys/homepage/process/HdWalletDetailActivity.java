package org.haobtc.onekey.onekeys.homepage.process;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.bean.GetCodeAddressBean;
import org.haobtc.onekey.utils.Daemon;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HdWalletDetailActivity extends BaseActivity {

    @BindView(R.id.text_wallet_name)
    TextView textWalletName;
    @BindView(R.id.text_address)
    TextView textAddress;
    @BindView(R.id.lin_hd_wallet_show)
    LinearLayout linHdWalletShow;
    @BindView(R.id.lin_single_show)
    LinearLayout linSingleShow;
    @BindView(R.id.text_hd_wallet)
    TextView textHdWallet;
    private String showWalletType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_hd_wallet_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        inits();

    }

    private void inits() {
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        showWalletType = preferences.getString("showWalletType", "");
        String hdWalletName = getIntent().getStringExtra("hdWalletName");
        textWalletName.setText(hdWalletName);
        if (showWalletType.contains("hd") || showWalletType.contains("derived")) {
            textHdWallet.setText(getString(R.string.hd_wallet));
            //HD wallet detail and derive wallet
            linHdWalletShow.setVisibility(View.VISIBLE);
            linSingleShow.setVisibility(View.GONE);
        } else if ("btc-standard".equals(showWalletType)) {
            textHdWallet.setText(getString(R.string.single_wallet));
            //Independent Wallet
            linHdWalletShow.setVisibility(View.GONE);
            linSingleShow.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void initData() {
        //get receive address
        mGeneratecode();
    }

    private void mGeneratecode() {
        PyObject walletAddressShowUi = null;
        try {
            walletAddressShowUi = Daemon.commands.callAttr("get_wallet_address_show_UI");

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (walletAddressShowUi != null) {

            String strCode = walletAddressShowUi.toString();
            Gson gson = new Gson();
            GetCodeAddressBean getCodeAddressBean = gson.fromJson(strCode, GetCodeAddressBean.class);
            String addr = getCodeAddressBean.getAddr();
            textAddress.setText(addr);
        }

    }

    @OnClick({R.id.img_back, R.id.img_copy, R.id.rel_export_word, R.id.rel_export_private_key, R.id.rel_export_keystore, R.id.rel_delete_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_copy:
                //copy text
                ClipboardManager cm2 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // The text is placed on the system clipboard.
                Objects.requireNonNull(cm2, "ClipboardManager not available").setPrimaryClip(ClipData.newPlainText(null, textAddress.getText()));
                Toast.makeText(HdWalletDetailActivity.this, R.string.copysuccess, Toast.LENGTH_LONG).show();
                break;
            case R.id.rel_export_word:
                break;
            case R.id.rel_export_private_key:
                break;
            case R.id.rel_export_keystore:
                break;
            case R.id.rel_delete_wallet:
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}