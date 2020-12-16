package org.haobtc.onekey.onekeys.homepage.process;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.activities.personalwallet.CreatFinishPersonalActivity;
import org.haobtc.onekey.activities.settings.recovery_set.BackupRecoveryActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.CurrentFeeDetails;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.TemporaryTxInfo;
import org.haobtc.onekey.bean.TransactionInfoBean;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.ButtonRequestConfirmedEvent;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.CustomizeFeeRateEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.GetFeeEvent;
import org.haobtc.onekey.event.InputPassSendEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;
import org.haobtc.onekey.ui.activity.VerifyPinActivity;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.dialog.CustomizeFeeDialog;
import org.haobtc.onekey.ui.dialog.TransactionConfirmDialog;
import org.haobtc.onekey.utils.ClipboardUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Optional;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

import static org.haobtc.onekey.constant.Constant.CURRENT_CURRENCY_GRAPHIC_SYMBOL;
import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE_SHORT;
import static org.haobtc.onekey.constant.Constant.WALLET_BALANCE;

/**
 * @author liyan
 */
public class SendHdActivity extends BaseActivity implements BusinessAsyncTask.Helper {

    static final int RECOMMENDED_FEE_RATE = 0;
    static final int SLOW_FEE_RATE = 1;
    static final int FAST_FEE_RATE = 2;
    static final int CUSTOMIZE_FEE_RATE = 3;
    private static final int DEFAULT_TX_SIZE = 220;
    @BindView(R.id.edit_amount)
    EditText editAmount;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.checkbox_slow)
    CheckBox checkboxSlow;
    @BindView(R.id.view_slow)
    View viewSlow;
    @BindView(R.id.checkbox_recommend)
    CheckBox checkboxRecommend;
    @BindView(R.id.view_recommend)
    View viewRecommend;
    @BindView(R.id.checkbox_fast)
    CheckBox checkboxFast;
    @BindView(R.id.view_fast)
    View viewFast;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.edit_receiver_address)
    EditText editReceiverAddress;
    @BindView(R.id.paste_address)
    TextView pasteAddress;
    @BindView(R.id.switch_coin_type)
    TextView switchCoinType;
    @BindView(R.id.text_max_amount)
    TextView textMaxAmount;
    @BindView(R.id.text_balance)
    TextView textBalance;
    @BindView(R.id.text_customize_fee_rate)
    TextView textCustomizeFeeRate;
    @BindView(R.id.text_fee_in_btc_0)
    TextView textFeeInBtc0;
    @BindView(R.id.text_fee_in_cash_0)
    TextView textFeeInCash0;
    @BindView(R.id.text_spend_time_0)
    TextView textSpendTime0;
    @BindView(R.id.linear_slow)
    RelativeLayout linearSlow;
    @BindView(R.id.text_fee_in_btc_1)
    TextView textFeeInBtc1;
    @BindView(R.id.text_fee_in_cash_1)
    TextView textFeeInCash1;
    @BindView(R.id.text_spend_time_1)
    TextView textSpendTime1;
    @BindView(R.id.linear_recommend)
    RelativeLayout linearRecommend;
    @BindView(R.id.text_fee_in_btc_2)
    TextView textFeeInBtc2;
    @BindView(R.id.text_fee_in_cash_2)
    TextView textFeeInCash2;
    @BindView(R.id.text_spend_time_2)
    TextView textSpendTime2;
    @BindView(R.id.linear_fast)
    RelativeLayout linearFast;
    @BindView(R.id.linear_rate_selector)
    LinearLayout linearRateSelector;
    @BindView(R.id.checkbox_custom)
    CheckBox checkboxCustom;
    @BindView(R.id.text_fee_customize_in_btc)
    TextView textFeeCustomizeInBtc;
    @BindView(R.id.text_fee_customize_in_cash)
    TextView textFeeCustomizeInCash;
    @BindView(R.id.text_customize_spend_time)
    TextView textCustomizeSpendTime;
    @BindView(R.id.text_rollback)
    TextView textRollback;
    @BindView(R.id.linear_customize)
    LinearLayout linearCustomize;
    private int screenHeight;
    private boolean mIsSoftKeyboardShowing;
    private SharedPreferences preferences;
    private String hdWalletName;
    private String baseUnit;
    private String currencySymbols;
    private String showWalletType;
    private String signedTx;
    private String rawTx;
    private TransactionConfirmDialog confirmDialog;
    private CurrentFeeDetails currentFeeDetails;
    private String currentTempTransaction;
    private String tempFastTransaction;
    private String tempSlowTransaction;
    private String tempRecommendTransaction;
    private String tempCustomizeTransaction;
    private int transactionSize;
    private double currentFeeRate;
    private double previousFeeRate;
    private String previousTempTransaction;
    private BigDecimal minAmount;
    private BigDecimal decimalBalance;
    private int scale;
    private CustomizeFeeDialog feeDialog;
    private String amounts;


    /**
     * init
     */
    @Override
    public void init() {
        hdWalletName = getIntent().getStringExtra("hdWalletName");
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String balance = getIntent().getStringExtra(WALLET_BALANCE);
        assert balance != null;
        decimalBalance = BigDecimal.valueOf(Double.parseDouble(balance));
        showWalletType = preferences.getString(CURRENT_SELECTED_WALLET_TYPE, "");
        baseUnit = preferences.getString("base_unit", "");
        currencySymbols = preferences.getString(CURRENT_CURRENCY_GRAPHIC_SYMBOL, "¥");
        getDefaultFee();
        currentFeeRate = currentFeeDetails.getFast().getFeerate();
        setMinAmount();
        String addressScan = getIntent().getStringExtra("addressScan");
        if (!TextUtils.isEmpty(addressScan)) {
            editReceiverAddress.setText(addressScan);
        }
        textBalance.setText(String.format("%s%s", balance, preferences.getString("base_unit", "")));
        registerLayoutChangeListener();
        //whether backup
        boolean whetherBackup = getIntent().getBooleanExtra("whetherBackup", false);
        if (!whetherBackup) {
            noBackupDialog();
        }
    }

    private void noBackupDialog() {
        View view1 = LayoutInflater.from(this).inflate(R.layout.unbackup_tip, null, false);
        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view1).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view1.findViewById(R.id.text_back).setOnClickListener(v -> {
            finish();
            alertDialog.dismiss();
        });
        view1.findViewById(R.id.text_i_know).setOnClickListener(v -> {
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


    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_send_hd;
    }

    private void setMinAmount() {
        switch (baseUnit) {
            case Constant.BTC_UNIT_BTC:
                minAmount = BigDecimal.valueOf(0.00000001);
                scale = 8;
                break;
            case Constant.BTC_UNIT_M_BTC:
                minAmount = BigDecimal.valueOf(0.00001);
                scale = 5;
                break;
            case Constant.BTC_UNIT_M_BITS:
                minAmount = BigDecimal.valueOf(0.01);
                scale = 2;
                break;
        }
    }

    @OnClick({R.id.img_back, R.id.switch_coin_type, R.id.text_max_amount, R.id.text_customize_fee_rate, R.id.linear_slow, R.id.linear_recommend, R.id.linear_fast, R.id.text_rollback, R.id.btn_next, R.id.paste_address})
    @SingleClick
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.switch_coin_type:
                // not support
                break;
            case R.id.text_max_amount:
                if (Strings.isNullOrEmpty(editReceiverAddress.getText().toString())) {
                    showToast(R.string.input_number);
                } else {
                    calculateMaxSpendableAmount(currentFeeRate);
                    changeButton();
                }
                break;
            case R.id.text_customize_fee_rate:
                if (sendReady()) {
                    Bundle bundle = new Bundle();
                    bundle.putDouble(Constant.CUSTOMIZE_FEE_RATE_MIN, currentFeeDetails.getSlowest().getFeerate());
                    bundle.putDouble(Constant.CUSTOMIZE_FEE_RATE_MAX, currentFeeDetails.getFast().getFeerate() * 2);
                    bundle.putInt(Constant.TAG_TX_SIZE, transactionSize);
                    feeDialog = new CustomizeFeeDialog();
                    feeDialog.setArguments(bundle);
                    feeDialog.show(getSupportFragmentManager(), "customize_fee");
                }
                break;
            case R.id.linear_slow:
                if (sendReady()) {
                    viewSlow.setVisibility(View.VISIBLE);
                    viewRecommend.setVisibility(View.GONE);
                    viewFast.setVisibility(View.GONE);
                    checkboxSlow.setVisibility(View.VISIBLE);
                    checkboxRecommend.setVisibility(View.GONE);
                    checkboxFast.setVisibility(View.GONE);
                    currentFeeRate = currentFeeDetails.getSlow().getFeerate();
                    currentTempTransaction = tempSlowTransaction;
                }
                break;
            case R.id.linear_recommend:
                if (sendReady()) {
                    viewSlow.setVisibility(View.GONE);
                    viewRecommend.setVisibility(View.VISIBLE);
                    viewFast.setVisibility(View.GONE);
                    checkboxSlow.setVisibility(View.GONE);
                    checkboxRecommend.setVisibility(View.VISIBLE);
                    checkboxFast.setVisibility(View.GONE);
                    currentFeeRate = currentFeeDetails.getNormal().getFeerate();
                    currentTempTransaction = tempRecommendTransaction;
                }
                break;
            case R.id.linear_fast:
                if (sendReady()) {
                    viewSlow.setVisibility(View.GONE);
                    viewRecommend.setVisibility(View.GONE);
                    viewFast.setVisibility(View.VISIBLE);
                    checkboxSlow.setVisibility(View.GONE);
                    checkboxRecommend.setVisibility(View.GONE);
                    checkboxFast.setVisibility(View.VISIBLE);
                    currentFeeRate = currentFeeDetails.getFast().getFeerate();
                    currentTempTransaction = tempFastTransaction;
                }
                break;
            case R.id.text_rollback:
                turnOffCustomize();
                break;
            case R.id.paste_address:
                editReceiverAddress.setText(ClipboardUtils.pasteText(this));
                break;
            case R.id.btn_next:
                send();
                break;
        }
    }

    /**
     * 取消自定义费率
     */
    private void turnOffCustomize() {
        linearRateSelector.setVisibility(View.VISIBLE);
        linearCustomize.setVisibility(View.GONE);
        currentFeeRate = previousFeeRate;
        currentTempTransaction = previousTempTransaction;
    }

    /**
     * 判断地址和金额是否正确填写完毕
     */
    private boolean sendReady() {
        if (Strings.isNullOrEmpty(editReceiverAddress.getText().toString())) {
            showToast(R.string.input_number);
            return false;
        }
        if (Strings.isNullOrEmpty(editAmount.getText().toString())) {
            showToast(R.string.input_out_number);
            return false;
        }
        return true;
    }

    /**
     * 获取费率详情
     */
    private void getDefaultFee() {
        PyResponse<String> response = PyEnv.getFeeInfo();
        String errors = response.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            currentFeeDetails = CurrentFeeDetails.objectFromDate(response.getResult());
            initFeeSelectorStatus();
        } else {
            showToast(R.string.get_fee_info_failed);
        }
    }

    /**
     * 初始化三种等级手续费的默认视图
     */
    private void initFeeSelectorStatus() {
        textSpendTime0.setText(String.format("%s%s%s", getString(R.string.about_), currentFeeDetails == null ? 0 : currentFeeDetails.getSlow().getTime(), getString(R.string.minute)));
        textFeeInBtc0.setText(String.format(Locale.ENGLISH, "%s %s", currentFeeDetails.getSlow().getFee(), baseUnit));
        PyResponse<String> response0 = PyEnv.exchange(currentFeeDetails.getSlow().getFee());
        String errors0 = response0.getErrors();
        if (Strings.isNullOrEmpty(errors0)) {
            textFeeInCash0.setText(String.format(Locale.ENGLISH, "%s %s", currencySymbols, response0.getResult()));
        } else {
            showToast(errors0);
        }
        textSpendTime1.setText(String.format("%s%s%s", getString(R.string.about_), currentFeeDetails == null ? 0 : currentFeeDetails.getNormal().getTime(), getString(R.string.minute)));
        textFeeInBtc1.setText(String.format(Locale.ENGLISH, "%s %s", currentFeeDetails.getNormal().getFee(), baseUnit));
        PyResponse<String> response1 = PyEnv.exchange(currentFeeDetails.getNormal().getFee());
        String errors1 = response1.getErrors();
        if (Strings.isNullOrEmpty(errors1)) {
            textFeeInCash1.setText(String.format(Locale.ENGLISH, "%s %s", currencySymbols, response1.getResult()));
        } else {
            showToast(errors0);
        }
        textSpendTime2.setText(String.format("%s%s%s", getString(R.string.about_), currentFeeDetails == null ? 0 : currentFeeDetails.getFast().getTime(), getString(R.string.minute)));
        textFeeInBtc2.setText(String.format(Locale.ENGLISH, "%s %s", currentFeeDetails.getFast().getFee(), baseUnit));
        PyResponse<String> response2 = PyEnv.exchange(currentFeeDetails.getFast().getFee());
        String errors2 = response2.getErrors();
        if (Strings.isNullOrEmpty(errors2)) {
            textFeeInCash2.setText(String.format(Locale.ENGLISH, "%s %s", currencySymbols, response2.getResult()));
        } else {
            showToast(errors0);
        }
    }

    /**
     * 计算最大可用余额
     */
    private void calculateMaxSpendableAmount(double feeRate) {
        BigDecimal decimalFee = new BigDecimal(feeRate).multiply(BigDecimal.valueOf(transactionSize == 0 ?
                DEFAULT_TX_SIZE : transactionSize)).multiply(minAmount);
        BigDecimal maxAmount = decimalBalance.subtract(decimalFee);
        if (maxAmount.compareTo(BigDecimal.ZERO) <= 0) {
            showToast(R.string.insufficient);
        } else {
            maxAmount = maxAmount.setScale(scale, RoundingMode.HALF_EVEN);
            editAmount.requestFocus();
            editAmount.setText(String.valueOf(maxAmount));
            editAmount.clearFocus();
        }
    }

    /**
     * 发送交易
     */
    private void send() {
        PyResponse<String> response = PyEnv.makeTx(currentTempTransaction);
        String errors = response.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            // sign
            rawTx = response.getResult();
            if (Constant.WALLET_TYPE_HARDWARE.equals(showWalletType)) {
                hardwareSign(rawTx);
            } else {
                sendConfirmDialog(rawTx);
            }
        } else {
            showToast(errors);
        }
    }

    @Subscribe
    public void onGotSoftPass(InputPassSendEvent event) {
        softSign(event.getPass());
    }

    /**
     * 软件签名交易
     */
    private void softSign(String password) {
        PyResponse<TransactionInfoBean> pyResponse = PyEnv.signTx(rawTx, password);
        String errorMsg = pyResponse.getErrors();
        if (Strings.isNullOrEmpty(errorMsg)) {
            broadcastTx(pyResponse.getResult().getTx());
        } else {
            showToast(errorMsg);
//            if (errorMsg.contains("Incorrect password")) {
//                showToast(getString(R.string.wrong_pass));
//            }
        }
    }

    /**
     * 广播交易
     */
    private void broadcastTx(String signedTx) {
        PyResponse<Void> response = PyEnv.broadcast(signedTx);
        String errors = response.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            Intent intent = new Intent(SendHdActivity.this, TransactionCompletion.class);
            intent.putExtra("txDetail", signedTx);
            intent.putExtra("amounts", amounts);
            startActivity(intent);
            finish();
        } else {
            showToast(errors);
        }
    }

    /**
     * 弹出交易确认框
     */
    private void sendConfirmDialog(String rawTx) {
        PyResponse<String> response = PyEnv.analysisRawTx(rawTx);
        String errors = response.getErrors();
        if (!Strings.isNullOrEmpty(errors)) {
            showToast(errors);
//            showToast(R.string.transaction_parse_error);
            return;
        }
        TransactionInfoBean info = TransactionInfoBean.objectFromData(response.getResult());
        // set see view
        String sender = info.getInputAddr().get(0).getAddr();
        String receiver = info.getOutputAddr().get(0).getAddr();
        amounts = info.getAmount();
        String fee = info.getFee();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TRANSACTION_SENDER, sender);
        bundle.putString(Constant.TRANSACTION_RECEIVER, receiver);
        bundle.putString(Constant.TRANSACTION_AMOUNT, amounts);
        bundle.putString(Constant.TRANSACTION_FEE, fee);
        bundle.putString(Constant.WALLET_LABEL, hdWalletName);
        bundle.putInt(Constant.WALLET_TYPE, Constant.WALLET_TYPE_HARDWARE.equals(showWalletType) ?
                Constant.WALLET_TYPE_HARDWARE_PERSONAL : Constant.WALLET_TYPE_SOFTWARE);
        confirmDialog = new TransactionConfirmDialog();
        confirmDialog.setArguments(bundle);
        confirmDialog.show(getSupportFragmentManager(), "confirm");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfirm(ButtonRequestConfirmedEvent event) {
        if (Constant.WALLET_TYPE_HARDWARE.equals(showWalletType)) {
            TransactionInfoBean info = TransactionInfoBean.objectFromData(signedTx);
            broadcastTx(info.getTx());
        } else {
            // 获取主密码
            if (SOFT_HD_PASS_TYPE_SHORT.equals(preferences.getString(SOFT_HD_PASS_TYPE, SOFT_HD_PASS_TYPE_SHORT))) {
                Intent intent = new Intent(this, SetHDWalletPassActivity.class);
                intent.putExtra("importHdword", "send");
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, SetLongPassActivity.class);
                intent.putExtra("importHdword", "send");
                startActivity(intent);
            }
        }
    }

    private boolean getFee(String feeRate, int type) {
        PyResponse<TemporaryTxInfo> pyResponse = PyEnv.getFeeByFeeRate(editReceiverAddress.getText().toString(), editAmount.getText().toString(), feeRate);
        String errors = pyResponse.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            TemporaryTxInfo temporaryTxInfo = pyResponse.getResult();
            String fee = BigDecimal.valueOf(temporaryTxInfo.getFee()).toString();
            int time = temporaryTxInfo.getTime();
            String temp = temporaryTxInfo.getTx();
            transactionSize = temporaryTxInfo.getSize();
            switch (type) {
                case RECOMMENDED_FEE_RATE:
                    textSpendTime1.setText(String.format("%s%s%s", getString(R.string.about_), time + "", getString(R.string.minute)));
                    textFeeInBtc1.setText(String.format(Locale.ENGLISH, "%s %s", fee, baseUnit));
                    PyResponse<String> response1 = PyEnv.exchange(fee);
                    String errors1 = response1.getErrors();
                    if (Strings.isNullOrEmpty(errors1)) {
                        textFeeInCash1.setText(String.format(Locale.ENGLISH, "%s %s", currencySymbols, response1.getResult()));
                    } else {
                        showToast(errors1);
                        return false;
                    }
                    tempRecommendTransaction = temp;
                    currentTempTransaction = tempRecommendTransaction;
                    break;
                case SLOW_FEE_RATE:
                    textSpendTime0.setText(String.format("%s%s%s", getString(R.string.about_), time + "", getString(R.string.minute)));
                    textFeeInBtc0.setText(String.format(Locale.ENGLISH, "%s %s", fee, baseUnit));
                    PyResponse<String> response0 = PyEnv.exchange(fee);
                    String errors0 = response0.getErrors();
                    if (Strings.isNullOrEmpty(errors0)) {
                        textFeeInCash0.setText(String.format(Locale.ENGLISH, "%s %s", currencySymbols, response0.getResult()));
                    } else {
                        showToast(errors0);
                        return false;
                    }
                    tempSlowTransaction = temp;
                    break;
                case FAST_FEE_RATE:
                    textSpendTime2.setText(String.format("%s%s%s", getString(R.string.about_), time + "", getString(R.string.minute)));
                    textFeeInBtc2.setText(String.format(Locale.ENGLISH, "%s %s", fee, baseUnit));
                    PyResponse<String> response2 = PyEnv.exchange(fee);
                    String errors2 = response2.getErrors();
                    if (Strings.isNullOrEmpty(errors2)) {
                        textFeeInCash2.setText(String.format(Locale.ENGLISH, "%s %s", currencySymbols, response2.getResult()));
                    } else {
                        showToast(errors2);
                        return false;
                    }
                    tempFastTransaction = temp;
                    break;
                case CUSTOMIZE_FEE_RATE:
                    textCustomizeSpendTime.setText(String.format("%s%s%s", getString(R.string.about_), time + "", getString(R.string.minute)));
                    feeDialog.getTextTime().setText(String.format("%s %s", time, getString(R.string.minute)));
                    textFeeCustomizeInBtc.setText(String.format(Locale.ENGLISH, "%s %s", fee, baseUnit));
                    feeDialog.getTextFeeInBtc().setText(String.format(Locale.ENGLISH, "%s %s", fee, baseUnit));
                    PyResponse<String> response3 = PyEnv.exchange(fee);
                    String errors3 = response3.getErrors();
                    if (Strings.isNullOrEmpty(errors3)) {
                        textFeeCustomizeInCash.setText(String.format(Locale.ENGLISH, "%s %s", currencySymbols, response3.getResult()));
                        feeDialog.getTextFeeInCash().setText(String.format(Locale.ENGLISH, "%s %s", currencySymbols, response3.getResult()));
                    } else {
                        showToast(errors3);
                        return false;
                    }
                    tempCustomizeTransaction = temp;
                    break;
            }
        } else {
            showToast(errors);
        }
        return true;
    }

    /**
     * 改变发送按钮状态
     */
    private void changeButton() {
        if (!TextUtils.isEmpty(editReceiverAddress.getText().toString()) && !TextUtils.isEmpty(editAmount.getText().toString())) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    /**
     * 自定义费率确认响应
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCustomizeFee(CustomizeFeeRateEvent event) {
        linearRateSelector.setVisibility(View.GONE);
        linearCustomize.setVisibility(View.VISIBLE);
        previousFeeRate = currentFeeRate;
        currentFeeRate = Double.parseDouble(event.getFeeRate());
        previousTempTransaction = currentTempTransaction;
        currentTempTransaction = tempCustomizeTransaction;
    }

    /**
     * 自定义费率监听
     */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGetFee(GetFeeEvent event) {
        String feeRate = event.getFeeRate();
        getFee(feeRate, CUSTOMIZE_FEE_RATE);
    }

    /**
     * 注册全局视图监听器
     */
    private void registerLayoutChangeListener() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenHeight = metric.heightPixels;
        mIsSoftKeyboardShowing = false;
        //Determine the size of window visible area
        //If the difference between screen height and window visible area height is greater than 1 / 3 of the whole screen height, it means that the soft keyboard is in display, otherwise, the soft keyboard is hidden.
        // If the status of the soft keyboard was previously displayed, it is now closed, or it was previously closed, it is now displayed, it means that the status of the soft keyboard has changed
        ViewTreeObserver.OnGlobalLayoutListener mLayoutChangeListener = () -> {
            //Determine the size of window visible area
            Rect r = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            //If the difference between screen height and window visible area height is greater than 1 / 3 of the whole screen height, it means that the soft keyboard is in display, otherwise, the soft keyboard is hidden.
            int heightDifference = screenHeight - (r.bottom - r.top);
            boolean isKeyboardShowing = heightDifference > screenHeight / 3;
            // If the status of the soft keyboard was previously displayed, it is now closed, or it was previously closed, it is now displayed, it means that the status of the soft keyboard has changed
            if ((mIsSoftKeyboardShowing && !isKeyboardShowing) || (!mIsSoftKeyboardShowing && isKeyboardShowing)) {
                mIsSoftKeyboardShowing = isKeyboardShowing;
                if (!mIsSoftKeyboardShowing) {
                    editAmount.clearFocus();
                }
            }
        };
        // Register layout change monitoring
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(mLayoutChangeListener);
    }

    /**
     * 收币地址输入框实时监听
     */
    @OnFocusChange(value = R.id.edit_receiver_address)
    public void onFocusChanged(boolean focused) {
        if (!focused) {
            String address = editReceiverAddress.getText().toString();
            if (!Strings.isNullOrEmpty(address)) {
                boolean invalid = PyEnv.verifyAddress(address);
                if (!invalid) {
                    editReceiverAddress.setText("");
                    showToast(R.string.invalid_address);
                } else {
                    changeButton();
                }
            }
        }
    }

    /**
     * 交易金额实时监听
     */
    @OnTextChanged(value = R.id.edit_amount, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChangeAmount(CharSequence sequence) {
        if (Strings.isNullOrEmpty(editReceiverAddress.getText().toString())) {
            showToast(R.string.input_address);
            editAmount.setText("");
            return;
        }
        String amount = editAmount.getText().toString();
        if (!Strings.isNullOrEmpty(amount) && Double.parseDouble(amount) > 0) {
            BigDecimal decimal = BigDecimal.valueOf(Double.parseDouble(amount));
            if (decimal.compareTo(minAmount) < 0) {
                editAmount.setText(String.format(Locale.ENGLISH, "%s", minAmount.toString()));
            } else if (decimal.compareTo(decimalBalance) >= 0) {
                showToast(R.string.insufficient);
                return;
            }
            changeButton();
        }
    }

    /**
     * 交易金额输入框失焦监听
     */
    @OnFocusChange(value = R.id.edit_amount)
    public void onFocusChange(boolean focused) {
        if (!focused) {
            refreshFeeView();
        }
    }

    /**
     * 获取三种不同费率对应的临时交易
     */
    private void refreshFeeView() {
        Optional.ofNullable(currentFeeDetails).ifPresent((currentFeeDetails1 -> {
            if (sendReady()) {
                synchronized (SendHdActivity.class) {
                    double fast = currentFeeDetails1.getFast().getFeerate();
                    boolean success = getFee(Double.toString(fast), FAST_FEE_RATE);
                    if (success) {
                        double normal = currentFeeDetails1.getNormal().getFeerate();
                        getFee(Double.toString(normal), RECOMMENDED_FEE_RATE);
                        double slow = currentFeeDetails1.getSlow().getFeerate();
                        getFee(Double.toString(slow), SLOW_FEE_RATE);
                    }
                }
            }
        }));
    }

    /**
     * 硬件签名方法
     */
    private void hardwareSign(String rawTx) {
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.SIGN_TX,
                rawTx,
                MyApplication.getInstance().getDeviceWay());
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
                sendConfirmDialog(rawTx);
                break;
            default:
        }
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
            signedTx = s;
            if (confirmDialog != null) {
                confirmDialog.getBtnConfirmPay().setEnabled(true);
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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean needEvents() {
        return true;
    }
}