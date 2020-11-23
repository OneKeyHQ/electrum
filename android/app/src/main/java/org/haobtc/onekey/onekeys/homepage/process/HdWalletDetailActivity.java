package org.haobtc.onekey.onekeys.homepage.process;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.sign.SignActivity;
import org.haobtc.onekey.bean.GetCodeAddressBean;
import org.haobtc.onekey.event.FixWalletNameEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
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
    @BindView(R.id.lin_single)
    LinearLayout linSingle;
    @BindView(R.id.text_sign)
    TextView textSign;
    @BindView(R.id.lin_hardware)
    LinearLayout linHardware;
    private String type;

    @Override
    public int getLayoutId() {
        return R.layout.activity_hd_wallet_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        inits();
    }

    private void inits() {
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String showWalletType = preferences.getString("showWalletType", "");
        String hdWalletName = getIntent().getStringExtra("hdWalletName");
        textWalletName.setText(hdWalletName);
        if (showWalletType.contains("hd") || showWalletType.contains("derived")) {
            textHdWallet.setText(getString(R.string.hd_wallet));
            //HD wallet detail and derive wallet
            linHdWalletShow.setVisibility(View.VISIBLE);
            linSingleShow.setVisibility(View.GONE);
        } else if (showWalletType.contains("hw")) {
            textHdWallet.setText(getString(R.string.multi_sig));
            linSingleShow.setVisibility(View.VISIBLE);
            linSingle.setVisibility(View.GONE);
            linHardware.setVisibility(View.VISIBLE);
            type = showWalletType.substring(showWalletType.indexOf("hw-") + 3);
            textSign.setText(String.format("%s %s", type, getString(R.string.sign_num)));

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

    @OnClick({R.id.img_back, R.id.img_copy, R.id.rel_export_word, R.id.rel_export_private_key, R.id.rel_export_keystore, R.id.rel_delete_wallet, R.id.text_wallet_name, R.id.text_sign})
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
            case R.id.text_wallet_name:
                //fix wallet name
                fixWalletNameDialog(HdWalletDetailActivity.this, R.layout.edit_wallet_name);
                break;
            case R.id.rel_export_word:
                exportTipDialog(HdWalletDetailActivity.this, R.layout.export_mnemonic_tip, "importHdword");
                break;
            case R.id.rel_export_private_key:
                exportTipDialog(HdWalletDetailActivity.this, R.layout.export_mnemonic_tip, "exportPrivateKey");
                break;
            case R.id.rel_export_keystore:
//                exportTipDialog(HdWalletDetailActivity.this, R.layout.export_mnemonic_tip,"exportPrivateKey");
                break;
            case R.id.rel_delete_wallet:
                Intent intent = new Intent(HdWalletDetailActivity.this, SetHDWalletPassActivity.class);
                intent.putExtra("importHdword", "deleteSingleWallet");
                intent.putExtra("walletName", textWalletName.getText().toString());
                startActivity(intent);
                break;
            case R.id.text_sign:
                Intent intent1 = new Intent(HdWalletDetailActivity.this, SignActivity.class);
                intent1.putExtra("personceType", type);
                startActivity(intent1);
                break;

        }
    }

    private void exportTipDialog(Context context, @LayoutRes int resource, String export) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.btn_i_know).setOnClickListener(v -> {
            Intent intent1 = new Intent(context, SetHDWalletPassActivity.class);
            intent1.putExtra("importHdword", export);
            startActivity(intent1);
            dialogBtoms.dismiss();
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            dialogBtoms.dismiss();
        });
        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.setCanceledOnTouchOutside(true);
        dialogBtoms.show();

    }

    private void fixWalletNameDialog(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        EditText walletName = view.findViewById(R.id.edit_wallet_name);
        walletName.setText(textWalletName.getText().toString());
        view.findViewById(R.id.btn_import).setOnClickListener(v -> {
            if (TextUtils.isEmpty(walletName.getText().toString())) {
                mToast(getString(R.string.please_input_walletname));
                return;
            }
            try {
                Daemon.commands.callAttr("rename_wallet", textWalletName.getText().toString(), walletName.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                mToast(e.getMessage());
                return;
            }
            mToast(getString(R.string.fix_success));
            textWalletName.setText(walletName.getText().toString());
            EventBus.getDefault().post(new FixWalletNameEvent(walletName.getText().toString()));
            dialogBtoms.dismiss();
        });
        view.findViewById(R.id.img_cancel).setOnClickListener(v -> {
            dialogBtoms.dismiss();
        });
        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.setCanceledOnTouchOutside(true);
        dialogBtoms.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if ("finish".equals(msgVote)) {
            finish();
        }
    }

}