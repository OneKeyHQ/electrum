package org.haobtc.onekey.activities.sign;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.TransactionDetailsActivity;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.CurrentAddressDetail;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.TransactionInfoBean;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.entries.FsActivity;
import org.haobtc.onekey.event.ButtonRequestConfirmedEvent;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.activity.VerifyPinActivity;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.dialog.PassInputDialog;
import org.haobtc.onekey.ui.dialog.TransactionConfirmDialog;
import org.haobtc.onekey.utils.ClipboardUtils;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import dr.android.fileselector.FileSelectConstant;

/**
 * @author liyan
 */
public class SignActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, BusinessAsyncTask.Helper {

    public static final String TAG = SignActivity.class.getSimpleName();
    public static final String TAG1 = "SIGN_MESSAGE";
    public static final String TAG2 = "HARDWARE_SIGN_TRANSACTION";
    public static final String TAG3 = "HARDWARE_SIGN_MESSAGE";
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.verify_signature)
    TextView verifySignature;
    @BindView(R.id.sign_transaction)
    RadioButton signTransaction;
    @BindView(R.id.sign_message)
    RadioButton signMessage;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.edit_transaction_text)
    EditText editTransactionText;
    @BindView(R.id.btn_import_file)
    Button btnImportFile;
    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.btn_parse)
    Button btnParse;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;
    private String strSoftMsg;
    public static String strinputAddress;
    String hidePhrass;
    private ArrayList<TransactionInfoBean.OutputAddrBean> outputAddr;
    private List<TransactionInfoBean.InputAddrBean> inputAddr;
    private int showWalletType;
    private String currentMethod;
    private TransactionConfirmDialog confirmDialog;
    private String walletLabel;
    private TransactionInfoBean infoBean;
    private String signedTx;
    private String signature;

    /**
     * init
     */
    @Override
    public void init() {
        Intent intent = getIntent();
        hidePhrass = intent.getStringExtra("hide_phrass");
        walletLabel = intent.getStringExtra(org.haobtc.onekey.constant.Constant.WALLET_LABEL);
        rxPermissions = new RxPermissions(this);
        radioGroup.setOnCheckedChangeListener(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        showWalletType = getIntent().getIntExtra(org.haobtc.onekey.constant.Constant.WALLET_TYPE, org.haobtc.onekey.constant.Constant.WALLET_TYPE_SOFTWARE);
        getAddress();
        switch (showWalletType) {
            case org.haobtc.onekey.constant.Constant.WALLET_TYPE_SOFTWARE:
            case org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE_PERSONAL:
                break;
            case org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE_MULTI:
                signMessage.setVisibility(View.GONE);
                verifySignature.setVisibility(View.GONE);
        }
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_sign;
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.verify_signature, R.id.btn_import_file, R.id.btn_scan, R.id.btn_parse, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.verify_signature:
                toVerifyActivity(null);
                break;
            case R.id.btn_import_file:
                importTxFromFile();
                break;
            case R.id.btn_scan:
                scanMessage();
                break;
            case R.id.btn_parse:
                editTransactionText.setText(ClipboardUtils.pasteText(this));
                break;
            case R.id.btn_confirm:
                dealSign(editTransactionText.getText().toString());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @OnTextChanged(value = R.id.edit_transaction_text)
    public void onTextChange() {
        String strRaw = editTransactionText.getText().toString();
        if (!Strings.isNullOrEmpty(strRaw)) {
            btnConfirm.setEnabled(true);
        } else {
            btnConfirm.setEnabled(false);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.sign_transaction:
                editTransactionText.setText("");
                editTransactionText.setHint(getString(R.string.input_tsaction_text));
                break;
            case R.id.sign_message:
                editTransactionText.setText("");
                editTransactionText.setHint(getString(R.string.input_sign_msg));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + checkedId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePin(ChangePinEvent event) {
        // 回写PIN码
        PyEnv.setPin(event.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        switch (event.getType()) {
            case PyConstant.PIN_CURRENT:
                Intent intent = new Intent(this, VerifyPinActivity.class);
                startActivity(intent);
                break;
            case PyConstant.BUTTON_REQUEST_7:
                break;
            case PyConstant.BUTTON_REQUEST_8:
                EventBus.getDefault().post(new ExitEvent());
                if (BusinessAsyncTask.SIGN_TX.equals(currentMethod)) {
                    popupDialog(infoBean);
                }
                break;
            default:

        }
    }

    /**
     * 跳转到验证界面
     * */
    public void toVerifyActivity(Bundle bundle) {
        Intent intent = new Intent(this, CheckSignActivity.class);
        if (bundle != null) {
            intent.putExtra(org.haobtc.onekey.constant.Constant.VERIFY_DETAIL, bundle);
        }
        startActivity(intent);
    }
    public void popupDialog(TransactionInfoBean info) {
        String sender = info.getInputAddr().get(0).getPrevoutHash();
        String receiver = info.getOutputAddr().get(0).getAddr();
        String amount = String.format("%s%s", info.getAmount(), PreferencesManager.get(this, "Preferences", "base_unit", ""));
        String fee = info.getFee();
        Bundle bundle = new Bundle();
        bundle.putString(org.haobtc.onekey.constant.Constant.TRANSACTION_SENDER, sender);
        bundle.putString(org.haobtc.onekey.constant.Constant.TRANSACTION_RECEIVER, receiver);
        bundle.putString(org.haobtc.onekey.constant.Constant.TRANSACTION_AMOUNT, amount);
        bundle.putString(org.haobtc.onekey.constant.Constant.TRANSACTION_FEE, fee);
        bundle.putString(org.haobtc.onekey.constant.Constant.WALLET_LABEL, walletLabel);
        bundle.putInt(org.haobtc.onekey.constant.Constant.WALLET_TYPE, showWalletType);
        confirmDialog = new TransactionConfirmDialog();
        confirmDialog.setArguments(bundle);
        confirmDialog.show(getSupportFragmentManager(), "confirm");
    }

    /**
     * 签名逻辑处理
     * */
    private void dealSign(@NonNull String rawMessage) {
        TransactionInfoBean infoBean;
        if (signTransaction.isChecked()) {
            // sign transaction
           PyResponse<String> response = PyEnv.analysisRawTx(rawMessage);
           String errors = response.getErrors();
           if (Strings.isNullOrEmpty(errors)) {
             infoBean = TransactionInfoBean.objectFromData(response.getResult());
           } else {
               showToast(errors);
               return;
           }
            switch (showWalletType) {
                case org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE_PERSONAL:
                    new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.SIGN_TX,
                            infoBean.getTx(),
                            MyApplication.getInstance().getDeviceWay());
                    break;
                case org.haobtc.onekey.constant.Constant.WALLET_TYPE_SOFTWARE:
                    PassInputDialog passInputDialog = new PassInputDialog();
                    passInputDialog.show(getSupportFragmentManager(), "");
                    break;
                case org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE_MULTI:
                    break;
            }
        } else {
            // sign message
            switch (showWalletType) {
                case org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE_PERSONAL:
                    new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.SIGN_MESSAGE,
                            strinputAddress,
                            rawMessage,
                            MyApplication.getInstance().getDeviceWay());
                    break;
                case org.haobtc.onekey.constant.Constant.WALLET_TYPE_SOFTWARE:
                    PassInputDialog passInputDialog = new PassInputDialog();
                    passInputDialog.show(getSupportFragmentManager(), "");
                    break;
                case org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE_MULTI:
                    break;
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequestConfirmedEvent(ButtonRequestConfirmedEvent event) {

    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onException(Exception e) {
        showToast(e.getMessage());
    }

    @Override
    public void onResult(String s) {
        if (!Strings.isNullOrEmpty(s)) {
            switch (currentMethod) {
                case BusinessAsyncTask.SIGN_TX:
                    signedTx = s;
                    if (confirmDialog != null) {
                        confirmDialog.getBtnConfirmPay().setEnabled(true);
                    }
                    break;
                case BusinessAsyncTask.SIGN_MESSAGE:
                    signature = s;
                    Bundle bundle = new Bundle();
                    bundle.putString(org.haobtc.onekey.constant.Constant.RAW_MESSAGE, editTransactionText.getText().toString());
                    bundle.putString(org.haobtc.onekey.constant.Constant.SIGNATURE, signature);
                    toVerifyActivity(bundle);
                    break;
            }
        } else {
            finish();
        }
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void currentMethod(String methodName) {
        currentMethod = methodName;
    }

    @Override
    public boolean needEvents() {
        return true;
    }

    /**
     * 获取地址
     * */
    private void getAddress() {
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
            CurrentAddressDetail currentAddressDetail = gson.fromJson(strCode, CurrentAddressDetail.class);
            strinputAddress = currentAddressDetail.getAddr();

        }
    }

    /**
     * 软件签名处理
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotPass(GotPassEvent event) {
        String password = event.getPassword();
        if (signTransaction.isChecked()) {
            // sign transaction
            String signedTx = null;
            try {
                signedTx = Daemon.commands.callAttr("sign_tx", strSoftMsg, "", new Kwarg("password", password)).toString();
            } catch (Exception e) {
                if (e.getMessage().contains("Incorrect password")) {
                    showToast(getString(R.string.wrong_pass));
                } else if (e.getMessage().contains("failed to recognize transaction encoding for txt")) {
                    showToast(getString(R.string.transaction_wrong));
                }
                e.printStackTrace();
                return;
            }
            EventBus.getDefault().post(new FirstEvent("22"));
            Intent intent = new Intent(SignActivity.this, TransactionDetailsActivity.class);
            intent.putExtra("signTransction", signedTx);
            intent.putExtra("keyValue", "Sign");
            intent.putExtra("is_mine", true);
            startActivity(intent);
        } else {
            // sign Message
            String signedMessage = null;
            try {
                signedMessage = Daemon.commands.callAttr("sign_message", strinputAddress, strSoftMsg, "", new Kwarg("password", password)).toString();
            } catch (Exception e) {
                if (Objects.requireNonNull(e.getMessage()).contains("Incorrect password")) {
                    showToast(getString(R.string.wrong_pass));
                }
                e.printStackTrace();
                return;
            }
            Intent intent = new Intent(SignActivity.this, CheckSignMessageActivity.class);
            intent.putExtra("signMsg", strSoftMsg);
            intent.putExtra("signAddress", strinputAddress);
            intent.putExtra("signedFinish", signedMessage);
            startActivity(intent);
        }
    }

    /**
     * 扫描二维码
     * */
    private void scanMessage() {
        rxPermissions
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        // If you have already authorized it, you can directly jump to the QR code scanning interface
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
                    } else {
                        Toast.makeText(this, R.string.photopersion, Toast.LENGTH_SHORT).show();
                    }
                }).dispose();
    }
    /**
     * 从文件系统读取交易文件
     * */
    private void importTxFromFile() {
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        Intent intent1 = new Intent();
                        intent1.setClass(getApplicationContext(), FsActivity.class);
                        intent1.putExtra(FileSelectConstant.SELECTOR_REQUEST_CODE_KEY, FileSelectConstant.SELECTOR_MODE_FILE);
                        intent1.addCategory(Intent.CATEGORY_OPENABLE);
                        intent1.putExtra("keyFile", "1");
                        startActivityForResult(intent1, 1);

                    } else {
                        Toast.makeText(this, R.string.reservatpion_photo, Toast.LENGTH_SHORT).show();
                    }
                }).dispose();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            // scan
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                editTransactionText.setText(content);
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            // import file
            assert data != null;
            ArrayList<String> listExtra = data.getStringArrayListExtra(FileSelectConstant.SELECTOR_BUNDLE_PATHS);
            assert listExtra != null;
            String str = listExtra.toString();
            String substring = str.substring(1);
            String strPath = substring.substring(0, substring.length() - 1);
            try {
                //read file
                PyObject txFromFile = Daemon.commands.callAttr("read_tx_from_file", strPath);
                if (txFromFile != null) {
                    String readFile = txFromFile.toString();
                    editTransactionText.setText(readFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.filestyle_wrong), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
