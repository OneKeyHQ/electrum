package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.chaquo.python.Kwarg;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.ResultEvent;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportPrivateKeyActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.edit_input_private)
    EditText editInputPrivate;
    @BindView(R.id.btn_import)
    Button btnImport;
    private SharedPreferences.Editor edit;
    private RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_private_key;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        rxPermissions = new RxPermissions(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        editInputPrivate.addTextChangedListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean requireSecure() {
        return true;
    }

    @SingleClick(value = 1000)
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
                isRightPrivate();
                break;
        }
    }

    private void isRightPrivate() {
        try {
            Daemon.commands.callAttr("verify_legality", editInputPrivate.getText().toString(), new Kwarg("flag", "private"));
        } catch (Exception e) {
            mToast(e.getMessage());
            e.printStackTrace();
            return;
        }
        EventBus.getDefault().post(new ResultEvent(editInputPrivate.getText().toString()));
        Intent intent = new Intent(ImportPrivateKeyActivity.this, ImportWalletSetNameActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                editInputPrivate.setText(content);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 禁止EditText输入空格
        if (s.toString().contains(" ")) {
            String[] str = s.toString().split(" ");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str.length; i++) {
                sb.append(str[i]);
            }
            editInputPrivate.setText(sb.toString());
            editInputPrivate.setSelection(start);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s.toString())) {
            btnImport.setEnabled(true);
            btnImport.setBackground(getDrawable(R.drawable.btn_checked));

        } else {
            btnImport.setEnabled(false);
            btnImport.setBackground(getDrawable(R.drawable.btn_no_check));
        }

    }
}