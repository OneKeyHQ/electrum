package org.haobtc.onekey.activities.sign;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.TransactionDetailsActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.GetCodeAddressBean;
import org.haobtc.onekey.bean.GetnewcreatTrsactionListBean;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.entries.FsActivity;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.event.SignMessageEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.activity.VerifyPinActivity;
import org.haobtc.onekey.ui.base.BaseActivity;
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
    private String personceType;
    private String strSoftMsg;
    public static String strinputAddress;
    String hidePhrass;
    private ArrayList<GetnewcreatTrsactionListBean.OutputAddrBean> outputAddr;
    private List<GetnewcreatTrsactionListBean.InputAddrBean> inputAddr;
    private String fee;

    /**
     * init
     */
    @Override
    public void init() {
        Intent intent = getIntent();
        personceType = intent.getStringExtra("personceType");
        hidePhrass = intent.getStringExtra("hide_phrass");
        rxPermissions = new RxPermissions(this);
        radioGroup.setOnCheckedChangeListener(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String showWalletType = preferences.getString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE, "");
        //get sign address
        mGeneratecode();
//        if (showWalletType.contains("hw")) {
//            radioSignMsg.setVisibility(View.VISIBLE);
//            textCheckSign.setVisibility(View.GONE);
//        }
        if (!TextUtils.isEmpty(personceType)) {
            if (!"1-1".equals(personceType) && !personceType.contains("standard")) {
                signMessage.setVisibility(View.GONE);
                verifySignature.setVisibility(View.GONE);
            }
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


    // get sign address
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
            Log.i("strCode", "mGenerate--: " + strCode);
            Gson gson = new Gson();
            GetCodeAddressBean getCodeAddressBean = gson.fromJson(strCode, GetCodeAddressBean.class);
            strinputAddress = getCodeAddressBean.getAddr();

        }
    }
    @OnTextChanged(value = R.id.edit_transaction_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChange() {
        String strRaw = editTransactionText.getText().toString();
        if (!Strings.isNullOrEmpty(strRaw)) {
            btnConfirm.setEnabled(true);
            btnConfirm.setBackground(getDrawable(R.drawable.btn_checked));
        } else {
            btnConfirm.setEnabled(false);
            btnConfirm.setBackground(getDrawable(R.drawable.btn_no_check));
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.sign_transaction:
                editTransactionText.setHint(getString(R.string.input_tsaction_text));
                break;
            case R.id.sign_message:
                editTransactionText.setHint(getString(R.string.input_sign_msg));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + checkedId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignMessage(SignMessageEvent event) {
        strSoftMsg = editTransactionText.getText().toString();
        String signedMsg = event.getSignedRaw();
        Intent intentMsg = new Intent(SignActivity.this, CheckSignMessageActivity.class);
        intentMsg.putExtra("signMsg", strSoftMsg);
        intentMsg.putExtra("signAddress", strinputAddress);
        intentMsg.putExtra("signedFinish", signedMsg);
        startActivity(intentMsg);
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
            default:
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            //scan
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Log.i("xiaomionActivityResult", "onActivityResult: " + content);
                editTransactionText.setText(content);
            }
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            //import file
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
    @SingleClick
    @OnClick({R.id.img_back, R.id.verify_signature, R.id.sign_transaction, R.id.sign_message, R.id.edit_transaction_text, R.id.btn_import_file, R.id.btn_scan, R.id.btn_parse, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.verify_signature:
                startActivity(new Intent(this, CheckSignActivity.class));
                break;
            case R.id.btn_import_file:
                rxPermissions
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                Intent intent1 = new Intent();
                                intent1.setClass(getApplicationContext(), FsActivity.class);
                                intent1.putExtra(FileSelectConstant.SELECTOR_REQUEST_CODE_KEY, FileSelectConstant.SELECTOR_MODE_FILE);
                                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                                intent1.putExtra("keyFile", "1");
                                startActivityForResult(intent1, 1);

                            } else { // Oups permission denied
                                Toast.makeText(this, R.string.reservatpion_photo, Toast.LENGTH_SHORT).show();
                            }
                        }).dispose();
                break;
            case R.id.btn_scan:
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
            case R.id.btn_parse:
                editTransactionText.setText(ClipboardUtils.pasteText(this));
                break;
            case R.id.btn_confirm:
                // Software Wallet sign
                if ("standard".equals(personceType)) {
                    strSoftMsg = editTransactionText.getText().toString();
                    if (TextUtils.isEmpty(strSoftMsg)) {
                        showToast(getString(R.string.raw));
                        return;
                    }
                    // sign trsaction
                    if (signTransaction.isChecked()) {
                        softwareSignTrsaction(strSoftMsg);
                    } else { //sign message
                        signDialog();
                    }
                } else {
                    // Hardware wallet signature
                    String strTest = editTransactionText.getText().toString();
                    if (signTransaction.isChecked()) {
                        // sign trsaction
                        PyObject defGetTxInfoFromRaw = null;
                        try {
                            defGetTxInfoFromRaw = Daemon.commands.callAttr("get_tx_info_from_raw", strTest);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i("ffffffffff", "onViewClicked: " + e.getMessage());
                            if (e.getMessage().contains("non-hexadecimal number found in fromhex() arg at position")) {
                                showToast(getString(R.string.transaction_wrong));
                            } else if (e.getMessage().contains("failed to recognize transaction encoding for txt")) {
                                showToast(getString(R.string.transaction_wrong));
                            }
                            return;
                        }
                        if (defGetTxInfoFromRaw != null) {
                            String jsondefGet = defGetTxInfoFromRaw.toString();
                            Gson gson = new Gson();
                            GetnewcreatTrsactionListBean listBean = gson.fromJson(jsondefGet, GetnewcreatTrsactionListBean.class);
                            outputAddr = listBean.getOutputAddr();
                            inputAddr = listBean.getInputAddr();
                            fee = listBean.getFee();
                        }

                    } else {
                        // sign message
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
    private void signDialog() {
        View view1 = LayoutInflater.from(SignActivity.this).inflate(R.layout.input_wallet_pass, null, false);
        AlertDialog alertDialog = new AlertDialog.Builder(SignActivity.this).setView(view1).create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText strPass = view1.findViewById(R.id.edit_password);
        view1.findViewById(R.id.btn_enter_wallet).setOnClickListener(v -> {
            String strPassword = strPass.getText().toString();
            if (TextUtils.isEmpty(strPassword)) {
                showToast(getString(R.string.please_input_pass));
                return;
            }
            PyObject signMessage = null;
            try {
                signMessage = Daemon.commands.callAttr("sign_message", strinputAddress, strSoftMsg, "", new Kwarg("password", strPassword));
            } catch (Exception e) {
                if (Objects.requireNonNull(e.getMessage()).contains("Incorrect password")) {
                    showToast(getString(R.string.wrong_pass));
                }
                alertDialog.dismiss();
                e.printStackTrace();
                return;
            }
            if (signMessage != null) {
                String signedMessage = signMessage.toString();
                Intent intent = new Intent(SignActivity.this, CheckSignMessageActivity.class);
                intent.putExtra("signMsg", strSoftMsg);
                intent.putExtra("signAddress", strinputAddress);
                intent.putExtra("signedFinish", signedMessage);
                startActivity(intent);
                alertDialog.dismiss();
            }

        });
        view1.findViewById(R.id.cancel_select_wallet).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
        //show center
        Window dialogWindow = alertDialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.95);
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
    }

    private void softwareSignTrsaction(String strTest) {
        View view1 = LayoutInflater.from(SignActivity.this).inflate(R.layout.input_wallet_pass, null, false);
        AlertDialog alertDialog = new AlertDialog.Builder(SignActivity.this).setView(view1).create();
        EditText strPass = view1.findViewById(R.id.edit_password);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view1.findViewById(R.id.btn_enter_wallet).setOnClickListener(v -> {
            String strPassword = strPass.getText().toString();
            if (TextUtils.isEmpty(strPassword)) {
                showToast(getString(R.string.please_input_pass));
                return;
            }
            PyObject signMessage = null;
            try {
                signMessage = Daemon.commands.callAttr("sign_tx", strTest, "", new Kwarg("password", strPassword));
            } catch (Exception e) {
                if (e.getMessage().contains("Incorrect password")) {
                    showToast(getString(R.string.wrong_pass));
                } else if (e.getMessage().contains("failed to recognize transaction encoding for txt")) {
                    showToast(getString(R.string.transaction_wrong));
                }
                e.printStackTrace();
                return;
            }
            if (signMessage != null) {
                String signedMessage = signMessage.toString();
                EventBus.getDefault().post(new FirstEvent("22"));
                Intent intent = new Intent(SignActivity.this, TransactionDetailsActivity.class);
                intent.putExtra("signTransction", signedMessage);
                intent.putExtra("keyValue", "Sign");
                intent.putExtra("is_mine", true);
                startActivity(intent);
                alertDialog.dismiss();
            }

        });
        view1.findViewById(R.id.cancel_select_wallet).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
        //show center
        Window dialogWindow = alertDialog.getWindow();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.95);
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onException(Exception e) {

    }

    @Override
    public void onResult(String s) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void currentMethod(String methodName) {

    }
}
