package org.haobtc.keymanager.activities.sign;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.ConfirmOnHardware;
import org.haobtc.keymanager.activities.TransactionDetailsActivity;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.activities.service.NfcNotifyHelper;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.bean.GetCodeAddressBean;
import org.haobtc.keymanager.bean.GetnewcreatTrsactionListBean;
import org.haobtc.keymanager.bean.HardwareFeatures;
import org.haobtc.keymanager.entries.FsActivity;
import org.haobtc.keymanager.event.ButtonRequestEvent;
import org.haobtc.keymanager.event.FirstEvent;
import org.haobtc.keymanager.event.HandlerEvent;
import org.haobtc.keymanager.event.SignMessageEvent;
import org.haobtc.keymanager.utils.Daemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import dr.android.fileselector.FileSelectConstant;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.isNFC;

public class SignActivity extends BaseActivity implements TextWatcher, RadioGroup.OnCheckedChangeListener {

    public static final String TAG = SignActivity.class.getSimpleName();
    public static final String TAG1 = "SIGN_MESSAGE";
    public static final String TAG2 = "HARDWARE_SIGN_TRANSACTION";
    public static final String TAG3 = "HARDWARE_SIGN_MESSAGE";

    public boolean set;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.editTrsactionTest)
    EditText editTrsactionTest;
    @BindView(R.id.btn_import_file)
    Button btnImportFile;
    @BindView(R.id.btn_sweep)
    Button btnSweep;
    @BindView(R.id.pasteSignTrsaction)
    Button pasteSignTrsaction;
    @BindView(R.id.btnConfirm)
    Button buttonConfirm;
    @BindView(R.id.textCheckSign)
    TextView textCheckSign;
    @BindView(R.id.test_sign_tips)
    TextView testSignTips;
    @BindView(R.id.radioSignMsg)
    RadioButton radioSignMsg;
    private RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;
    private boolean signWhich = true;
    private String personceType;
    private String strSoftMsg;
    public static String strinputAddress;
    String hidePhrass;
    private ArrayList<GetnewcreatTrsactionListBean.OutputAddrBean> outputAddr;
    private List<GetnewcreatTrsactionListBean.InputAddrBean> inputAddr;
    private String fee;

    public SignActivity(Context mMockContext) {
        super();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_sign;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        personceType = intent.getStringExtra("personceType");
        hidePhrass = intent.getStringExtra("hide_phrass");
        rxPermissions = new RxPermissions(this);
        editTrsactionTest.addTextChangedListener(this);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void initData() {
        //get sign address
        mGeneratecode();
        if (!TextUtils.isEmpty(personceType)) {
            if (!"1-1".equals(personceType) && !personceType.contains("standard")) {
                radioSignMsg.setVisibility(View.GONE);
                textCheckSign.setVisibility(View.GONE);
            }
        }
    }

    //get sign address
    public void mGeneratecode() {
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String strRaw = editTrsactionTest.getText().toString();
        if (!TextUtils.isEmpty(strRaw)) {
            buttonConfirm.setEnabled(true);
            buttonConfirm.setBackground(getResources().getDrawable(R.drawable.button_bk));
        } else {
            buttonConfirm.setEnabled(false);
            buttonConfirm.setBackground(getResources().getDrawable(R.drawable.button_bk_grey));
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioSignTrsaction:
                signWhich = true;
                testSignTips.setText(getString(R.string.prompt_sig));
                editTrsactionTest.setHint(getString(R.string.input_tsaction_text));
                break;
            case R.id.radioSignMsg:
                signWhich = false;
                testSignTips.setText(getString(R.string.input_message));
                editTrsactionTest.setHint(getString(R.string.input_sign_msg));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + checkedId);
        }
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_import_file, R.id.btn_sweep, R.id.pasteSignTrsaction, R.id.btnConfirm, R.id.textCheckSign})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
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
            case R.id.btn_sweep:
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
            case R.id.pasteSignTrsaction:
                pasteMessage(editTrsactionTest);
                break;
            case R.id.btnConfirm:
                if ("standard".equals(personceType)) {//Software Wallet sign
                    strSoftMsg = editTrsactionTest.getText().toString();
                    if (TextUtils.isEmpty(strSoftMsg)) {
                        mToast(getString(R.string.raw));
                        return;
                    }
                    if (signWhich) { //sign trsaction
                        softwareSignTrsaction(strSoftMsg);
                    } else { //sign message
                        signDialog();
                    }
                } else { //Hardware wallet signature
                    String strTest = editTrsactionTest.getText().toString();
                    if (signWhich) { //sign trsaction
                        PyObject defGetTxInfoFromRaw = null;
                        try {
                            defGetTxInfoFromRaw = Daemon.commands.callAttr("get_tx_info_from_raw", strTest);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (e.getMessage().contains("non-hexadecimal number found in fromhex() arg at position")) {
                                mToast(getString(R.string.transaction_wrong));
                            } else if (e.getMessage().contains("failed to recognize transaction encoding for txt")) {
                                mToast(getString(R.string.transaction_wrong));
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
                        if ("1-1".equals(personceType) && Ble.getInstance().getConnetedDevices().size() != 0) {
                            String deviceId = Daemon.commands.callAttr("get_device_info").toString().replaceAll("\"", "");
                            SharedPreferences devices = getSharedPreferences("devices", MODE_PRIVATE);
                            String feature = devices.getString(deviceId, "");
                            if (!Strings.isNullOrEmpty(feature)) {
                                HardwareFeatures features = HardwareFeatures.objectFromData(feature);
                                if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(features.getBleName())) {
                                    EventBus.getDefault().postSticky(new HandlerEvent());
                                }
                            }
                        }
                        CommunicationModeSelector.runnables.clear();
                        CommunicationModeSelector.runnables.add(runnable);
                        Intent intent = new Intent(this, CommunicationModeSelector.class);
                        intent.putExtra("tag", Strings.isNullOrEmpty(hidePhrass) ? TAG : TAG2);
                        intent.putExtra("extras", strTest);
                        startActivity(intent);
                    } else {//sign message
                        if ("1-1".equals(personceType) && Ble.getInstance().getConnetedDevices().size() != 0) {
                            String deviceId = Daemon.commands.callAttr("get_device_info").toString().replaceAll("\"", "");
                            SharedPreferences devices = getSharedPreferences("devices", MODE_PRIVATE);
                            String feature = devices.getString(deviceId, "");
                            if (!Strings.isNullOrEmpty(feature)) {
                                HardwareFeatures features = HardwareFeatures.objectFromData(feature);
                                if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(features.getBleName())) {
                                    EventBus.getDefault().postSticky(new HandlerEvent());
                                }
                            }
                        }
                        CommunicationModeSelector.runnables.clear();
                        CommunicationModeSelector.runnables.add(runnable);
                        Intent intent = new Intent(this, CommunicationModeSelector.class);
                        intent.putExtra("tag", Strings.isNullOrEmpty(hidePhrass) ? TAG1 : TAG3);
                        intent.putExtra("extras", strTest);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.textCheckSign:
                mIntent(CheckSignActivity.class);
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
                mToast(getString(R.string.please_input_pass));
                return;
            }
            PyObject signMessage = null;
            try {
                signMessage = Daemon.commands.callAttr("sign_message", strinputAddress, strSoftMsg, "", new Kwarg("password", strPassword));
            } catch (Exception e) {
                if (Objects.requireNonNull(e.getMessage()).contains("Incorrect password")) {
                    mToast(getString(R.string.wrong_pass));
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
                mToast(getString(R.string.please_input_pass));
                return;
            }
            PyObject signMessage = null;
            try {
                signMessage = Daemon.commands.callAttr("sign_tx", strTest, "", new Kwarg("password", strPassword));
            } catch (Exception e) {
                if (e.getMessage().contains("Incorrect password")) {
                    mToast(getString(R.string.wrong_pass));
                } else if (e.getMessage().contains("failed to recognize transaction encoding for txt")) {
                    mToast(getString(R.string.transaction_wrong));
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

    private Runnable runnable = this::gotoConfirmOnHardware;

    private void gotoConfirmOnHardware() {
        if (signWhich) {
            Intent intentCon = new Intent(SignActivity.this, ConfirmOnHardware.class);
            intentCon.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle bundle = new Bundle();
            bundle.putSerializable("output", outputAddr);
            bundle.putString("pay_address", inputAddr.get(0).getAddr());
            bundle.putString("fee", fee);
            intentCon.putExtra("outputs", bundle);
            startActivity(intentCon);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            //scan
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Log.i("xiaomionActivityResult", "onActivityResult: " + content);
                editTrsactionTest.setText(content);
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
                    editTrsactionTest.setText(readFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.filestyle_wrong), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignMessage(SignMessageEvent event) {
        strSoftMsg = editTrsactionTest.getText().toString();
        String signedMsg = event.getSignedRaw();
        Intent intentMsg = new Intent(SignActivity.this, CheckSignMessageActivity.class);
        intentMsg.putExtra("signMsg", strSoftMsg);
        intentMsg.putExtra("signAddress", strinputAddress);
        intentMsg.putExtra("signedFinish", signedMsg);
        startActivity(intentMsg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        if (isNFC) {
            Intent intent = new Intent(this, NfcNotifyHelper.class);
            intent.putExtra("is_button_request", true);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }
}
