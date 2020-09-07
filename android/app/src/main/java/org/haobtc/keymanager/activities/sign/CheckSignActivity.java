package org.haobtc.keymanager.activities.sign;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.bean.GetCodeAddressBean;
import org.haobtc.keymanager.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckSignActivity extends BaseActivity {

    @BindView(R.id.editInputAddress)
    EditText editInputAddress;
    @BindView(R.id.editInputPublicKey)
    EditText editInputPublicKey;
    @BindView(R.id.editInputSignedMsg)
    EditText editInputSignedMsg;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;
    private RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_sign;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        rxPermissions = new RxPermissions(this);
        TextChange textChange = new TextChange();
        editInputAddress.addTextChangedListener(textChange);
        editInputPublicKey.addTextChangedListener(textChange);
        editInputSignedMsg.addTextChangedListener(textChange);
    }

    @Override
    public void initData() {
        mGeneratecode();
    }

    //get sign address
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
            String strinputAddress = getCodeAddressBean.getAddr();
            editInputPublicKey.setText(strinputAddress);
        }
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.sweepAddress, R.id.pasteAddress, R.id.pastePublicKey, R.id.pasteSignedMsg, R.id.btnConfirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.sweepAddress:
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
            case R.id.pasteAddress:
                pasteMessage(editInputAddress);
                break;
            case R.id.pastePublicKey:
                pasteMessage(editInputPublicKey);
                break;
            case R.id.pasteSignedMsg:
                pasteMessage(editInputSignedMsg);
                break;
            case R.id.btnConfirm:
                checkSigned();
                break;
            default:
        }
    }

    private void checkSigned() {
        String strMsg = editInputAddress.getText().toString();
        String strAddress = editInputPublicKey.getText().toString();
        String strSigned = editInputSignedMsg.getText().toString();
        PyObject verify_message = null;
        try {
            verify_message = Daemon.commands.callAttr("verify_message", strAddress, strMsg, strSigned);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Invalid Bitcoin address")) {
                mToast(getString(R.string.changeaddress));
            }
        }
        if (verify_message != null) {
            boolean verify = verify_message.toBoolean();
            if (verify) {
                Intent intent = new Intent(CheckSignActivity.this, CheckSignResultActivity.class);
                intent.putExtra("verify", verify);
                startActivity(intent);
            } else {
                Intent intent = new Intent(CheckSignActivity.this, CheckSignResultActivity.class);
                intent.putExtra("verify", verify);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Scan QR code / barcode return
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                editInputAddress.setText(content);
            }
        }
    }

    public void pasteMessage(EditText editString) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData data = clipboard.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                editString.setText(data.getItemAt(0).getText());
            }
        }
    }

    class TextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            //judge button status
            buttonColorStatus();
        }
    }

    //judge button status
    private void buttonColorStatus() {
        String strinputAddress = editInputAddress.getText().toString();
        String strInputPublicKey = editInputPublicKey.getText().toString();
        String strInputSignedMsg = editInputSignedMsg.getText().toString();
        if (TextUtils.isEmpty(strinputAddress) || TextUtils.isEmpty(strInputPublicKey) || TextUtils.isEmpty(strInputSignedMsg)) {
            btnConfirm.setEnabled(false);
            btnConfirm.setBackground(getDrawable(R.drawable.button_bk_grey));
        } else {
            btnConfirm.setEnabled(true);
            btnConfirm.setBackground(getDrawable(R.drawable.button_bk));
        }

    }
}
