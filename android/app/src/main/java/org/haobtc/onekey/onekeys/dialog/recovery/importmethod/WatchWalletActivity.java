package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.bean.MainSweepcodeBean;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WatchWalletActivity extends BaseActivity {

    @BindView(R.id.edit_address)
    EditText editAddress;
    private RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;
    private SharedPreferences.Editor edit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_watch_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        rxPermissions = new RxPermissions(this);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.img_scan, R.id.btn_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_scan:
                rxPermissions
                        .request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                //If you have already authorized it, you can directly jump to the QR code scanning interface
                                Intent intent2 = new Intent(this, CaptureActivity.class);
                                ZxingConfig config = new ZxingConfig();
                                config.setPlayBeep(true);
                                config.setShake(true);
                                config.setDecodeBarCode(false);
                                config.setFullScreenScan(true);
                                config.setShowAlbum(false);
                                config.setShowbottomLayout(false);
                                intent2.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                startActivityForResult(intent2, REQUEST_CODE);
                            } else { // Oups permission denied
                                Toast.makeText(this, R.string.photopersion, Toast.LENGTH_SHORT).show();
                            }
                        }).dispose();
                break;
            case R.id.btn_import:
                if (TextUtils.isEmpty(editAddress.getText().toString())) {
                    mToast(getString(R.string.please_input_addr_or_xpub));
                    return;
                }
                Intent intent = new Intent(WatchWalletActivity.this, ImportWalletSetNameActivity.class);
                intent.putExtra("watchAddress",editAddress.getText().toString());
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                editAddress.setText(content);
            }
        }
    }
}