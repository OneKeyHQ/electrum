package org.haobtc.onekey.activities.sign;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.CurrentAddressDetail;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.utils.ClipboardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * @author xiaomin
 */
public class CheckSignActivity extends BaseActivity {

    @BindView(R.id.edit_message)
    EditText editRawMessage;
    @BindView(R.id.edit_address)
    EditText editAddress;
    @BindView(R.id.edit_signature)
    EditText editSignature;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;
    @BindView(R.id.scan)
    ImageView scan;
    @BindView(R.id.paste_message)
    TextView pasteMessage;
    @BindView(R.id.scan_public)
    ImageView scanPublic;
    @BindView(R.id.paste_address)
    TextView pasteAddress;
    @BindView(R.id.scan_signed)
    ImageView scanSigned;
    @BindView(R.id.paste_signature)
    TextView pasteSignature;
    private RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_CODE_PUBLIC = 1;
    private static final int REQUEST_CODE_SIGNED = 2;
    private Bundle bundle;

    /**
     * init
     */
    @Override
    public void init() {
        rxPermissions = new RxPermissions(this);
        getCurrentAddress();
        bundle = getIntent().getBundleExtra(org.haobtc.onekey.constant.Constant.VERIFY_DETAIL);
        if (bundle != null) {
            editRawMessage.setText(bundle.getString(org.haobtc.onekey.constant.Constant.RAW_MESSAGE));
            editSignature.setText(bundle.getString(org.haobtc.onekey.constant.Constant.SIGNATURE));
            viewStatus();
        }
    }

    private void viewStatus() {
        scan.setVisibility(View.GONE);
        scanPublic.setVisibility(View.GONE);
        scanSigned.setVisibility(View.GONE);
        editRawMessage.setEnabled(false);
        editAddress.setEnabled(false);
        editSignature.setEnabled(false);
        pasteMessage.setText(getString(R.string.copy_));
        pasteAddress.setText(getString(R.string.copy_));
        pasteSignature.setText(getString(R.string.copy_));
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_check_sign;
    }

    /**
     * 获取当前钱包的地址
     */
    private void getCurrentAddress() {
        PyResponse<CurrentAddressDetail> response = PyEnv.getCurrentAddressInfo();
        String errors = response.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            editAddress.setText(response.getResult().getAddr());
        } else {
            showToast(errors);
        }
    }

    @OnTextChanged(value = R.id.edit_address, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChangedAddress(CharSequence sequence) {
        btnConfirm.setEnabled(!Strings.isNullOrEmpty(sequence.toString()));
    }

    @OnTextChanged(value = R.id.edit_signature, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChangedSignature(CharSequence sequence) {
        btnConfirm.setEnabled(!Strings.isNullOrEmpty(sequence.toString()));
    }

    @OnTextChanged(value = R.id.edit_message, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChangedMessage(CharSequence sequence) {
        btnConfirm.setEnabled(!Strings.isNullOrEmpty(sequence.toString()));
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.scan, R.id.paste_message, R.id.paste_address, R.id.paste_signature, R.id.btnConfirm, R.id.scan_public, R.id.scan_signed})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.scan:
                scan(REQUEST_CODE);
                break;
            case R.id.paste_message:
                if (bundle != null) {
                    ClipboardUtils.copyText(this, editRawMessage.getText().toString());
                } else {
                    editRawMessage.setText(ClipboardUtils.pasteText(this));
                }
                break;
            case R.id.paste_address:
                if (bundle != null) {
                    ClipboardUtils.copyText(this, editAddress.getText().toString());
                } else {
                    editAddress.setText(ClipboardUtils.pasteText(this));
                }
                break;
            case R.id.paste_signature:
                if (bundle != null) {
                    ClipboardUtils.copyText(this, editSignature.getText().toString());
                } else {
                    editSignature.setText(ClipboardUtils.pasteText(this));
                }
                break;
            case R.id.btnConfirm:
                verify();
                break;
            case R.id.scan_public:
                scan(REQUEST_CODE_PUBLIC);
                break;
            case R.id.scan_signed:
                scan(REQUEST_CODE_SIGNED);
                break;
            default:
        }
    }

    private void scan(int code) {
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
                        startActivityForResult(intent2, code);
                    } else { // Oups permission denied
                        Toast.makeText(this, R.string.photopersion, Toast.LENGTH_SHORT).show();
                    }
                }).dispose();
    }

    private void verify() {
        String message = editRawMessage.getText().toString();
        String address = editAddress.getText().toString();
        String signature = editSignature.getText().toString();
        PyResponse<Boolean> response = PyEnv.verifySignature(address, message, signature);
        String errors = response.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            Intent intent = new Intent(CheckSignActivity.this, CheckSignResultActivity.class);
            intent.putExtra("verify", response.getResult());
            startActivity(intent);
        } else {
            showToast(errors);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Scan QR code / barcode return
        if (data != null) {
            String content = data.getStringExtra(Constant.CODED_CONTENT);
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                editRawMessage.setText(content);
            } else if (requestCode == REQUEST_CODE_PUBLIC && resultCode == RESULT_OK) {
                editAddress.setText(content);
            } else if (requestCode == REQUEST_CODE_SIGNED && resultCode == RESULT_OK) {
                editSignature.setText(content);
            }
        }
    }
}
