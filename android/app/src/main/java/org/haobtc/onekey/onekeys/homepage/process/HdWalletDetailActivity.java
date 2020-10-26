package org.haobtc.onekey.onekeys.homepage.process;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_hd_wallet_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        String hdWalletName = getIntent().getStringExtra("hdWalletName");
        textWalletName.setText(hdWalletName);
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

    @OnClick({R.id.img_back, R.id.img_copy})
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

        }
    }
}