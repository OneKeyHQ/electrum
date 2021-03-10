package org.haobtc.onekey.onekeys.homepage.process;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.base.Strings;
import com.lxj.xpopup.XPopup;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.Assets;
import org.haobtc.onekey.bean.CurrentFeeDetails;
import org.haobtc.onekey.bean.MainSweepcodeBean;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.TemporaryTxInfo;
import org.haobtc.onekey.bean.TransactionInfoBean;
import org.haobtc.onekey.business.qrdecode.QRDecode;
import org.haobtc.onekey.business.wallet.AccountManager;
import org.haobtc.onekey.business.wallet.SystemConfigManager;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.ButtonRequestConfirmedEvent;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.CustomizeFeeRateEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.GetFeeEvent;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.manager.MySPManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.haobtc.onekey.ui.activity.VerifyPinActivity;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.dialog.CustomizeFeeDialog;
import org.haobtc.onekey.ui.dialog.TransactionConfirmDialog;
import org.haobtc.onekey.ui.dialog.UnBackupTipDialog;
import org.haobtc.onekey.ui.dialog.custom.CustomCenterDialog;
import org.haobtc.onekey.ui.dialog.custom.CustomWatchWalletDialog;
import org.haobtc.onekey.ui.widget.PasteEditText;
import org.haobtc.onekey.ui.widget.PointLengthFilter;
import org.haobtc.onekey.utils.ClipboardUtils;
import org.haobtc.onekey.utils.CoinDisplayUtils;
import org.haobtc.onekey.viewmodel.AppWalletViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/** @author liyan */
public class SendHdActivity extends BaseActivity implements BusinessAsyncTask.Helper {

    private static final String EXT_WALLET_NAME = "hdWalletName";
    private static final String EXT_SCAN_ADDRESS = "addressScan";
    private static final String EXT_SCAN_AMOUNT = "amountScan";

    public static void start(
            Context context, String name, @Nullable String address, @Nullable String amount) {
        Intent intent = new Intent(context, SendHdActivity.class);
        intent.putExtra(EXT_WALLET_NAME, name);
        if (!TextUtils.isEmpty(address)) {
            intent.putExtra(EXT_SCAN_ADDRESS, address);
            intent.putExtra(EXT_SCAN_AMOUNT, amount);
        }
        context.startActivity(intent);
    }

    static final int RECOMMENDED_FEE_RATE = 0;
    static final int SLOW_FEE_RATE = 1;
    static final int FAST_FEE_RATE = 2;
    static final int CUSTOMIZE_FEE_RATE = 3;
    private static final int DEFAULT_TX_SIZE = 220;

    @BindView(R.id.edit_amount)
    PasteEditText editAmount;

    @BindView(R.id.btn_next)
    Button btnNext;

    @BindView(R.id.img_scan)
    ImageView imgScan;

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
    PasteEditText editReceiverAddress;

    @BindView(R.id.paste_address)
    TextView pasteAddress;

    @BindView(R.id.wallet_name)
    TextView switchCoinType;

    @BindView(R.id.switch_icon)
    ImageView switchIcon;

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
    private RxPermissions rxPermissions;
    private SharedPreferences preferences;
    private String hdWalletName;
    private String baseUnit;
    private String currencySymbols;
    private String showWalletType;
    private String signedTx;
    private String rawTx;
    private TransactionConfirmDialog confirmDialog;
    private CurrentFeeDetails mCurrentFeeDetails;
    private String currentTempTransaction;
    private String tempFastTransaction;
    private String tempSlowTransaction;
    private String tempRecommendTransaction;
    private int transactionSize;
    private double currentFeeRate;
    private double previousFeeRate;
    private BigDecimal minAmount;
    private BigDecimal decimalBalance;
    private int scale;
    private CustomizeFeeDialog feeDialog;
    private boolean addressInvalid;
    private String amount;
    private boolean isFeeValid;
    private String amounts;
    private int customSize;
    private boolean isCustom;
    private boolean isSetBig;
    private String balance;
    private BigDecimal maxAmount;
    private int selectFlag = 0;
    private boolean isResume;
    private double slowRate;
    private double normalRate;
    private double fastRate;
    private AppWalletViewModel mAppWalletViewModel;
    private static final int REQUEST_SCAN_CODE = 0;
    private boolean signClickable = true;
    io.reactivex.disposables.Disposable subscribe;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private SystemConfigManager mSystemConfigManager;
    private AccountManager mAccountManager;
    private boolean isAddressClickPaste;
    private boolean isAmountClickPaste;
    private String mWalletType;

    /** init */
    @Override
    public void init() {
        mAppWalletViewModel =
                new ViewModelProvider(MyApplication.getInstance()).get(AppWalletViewModel.class);
        mAccountManager = new AccountManager(mContext);
        mSystemConfigManager = new SystemConfigManager(this);
        rxPermissions = new RxPermissions(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        showWalletType = mAccountManager.getCurrentWalletAccurateType();
        mWalletType = mAccountManager.getCurWalletType();
        baseUnit = mSystemConfigManager.getCurrentBaseUnit();
        currencySymbols = mSystemConfigManager.getCurrentFiatSymbol();
        switchCoinType.setText(Vm.CoinType.BTC.coinName);
        switchIcon.setVisibility(View.GONE);

        mAppWalletViewModel.currentWalletAssetsList.observe(
                this,
                assets -> {
                    Assets mAssets =
                            mAppWalletViewModel
                                    .currentWalletAssetsList
                                    .getValue()
                                    .getByUniqueIdOrZero(-1);

                    hdWalletName = mAssets.getName();
                    balance = CoinDisplayUtils.getCoinBalanceDisplay(mAssets);
                    if (!Strings.isNullOrEmpty(balance)) {
                        decimalBalance = BigDecimal.valueOf(Double.parseDouble(balance));
                    }
                    textBalance.setText(String.format("%s%s", balance, baseUnit));
                });

        getDefaultFee();
        setMinAmount();
        editAmount.setFilters(
                new InputFilter[] {
                    new PointLengthFilter(
                            scale,
                            maxNum ->
                                    showToast(
                                            String.format(
                                                    Locale.getDefault(),
                                                    mContext.getString(R.string.accuracy_num),
                                                    scale)))
                });
        String addressScan = getIntent().getStringExtra(EXT_SCAN_ADDRESS);
        if (!TextUtils.isEmpty(addressScan)) {
            editReceiverAddress.setText(addressScan);
            String amountScan = getIntent().getStringExtra(EXT_SCAN_AMOUNT);
            String amountConvert = new QRDecode().getAmountByPythonResultAmount(amountScan);
            String amountStr = checkAndConvertAmount(amountConvert);
            if (amountStr == null) {
                getAddressIsValid(true);
            } else {
                editAmount.setText(amountStr);
                keyBoardHideRefresh();
            }
        } else {
            // whether backup
            boolean walletBackup = mAccountManager.getWalletBackup();
            if (!walletBackup) {
                new XPopup.Builder(mContext)
                        .dismissOnTouchOutside(false)
                        .isDestroyOnDismiss(true)
                        .asCustom(
                                new UnBackupTipDialog(
                                        mContext,
                                        getString(R.string.receive_unbackup_tip),
                                        new UnBackupTipDialog.onClick() {
                                            @Override
                                            public void onBack() {
                                                finish();
                                            }
                                        }))
                        .show();
            }
        }
        if (Constant.BTC_WATCH.equals(showWalletType)) {
            showWatchTipDialog();
        }
        registerLayoutChangeListener();
        editReceiverAddress.setOnPasteCallback(() -> isAddressClickPaste = true);
        editAmount.setOnPasteCallback(() -> isAmountClickPaste = true);
    }

    private void showWatchTipDialog() {
        CustomCenterDialog centerDialog =
                new CustomCenterDialog(
                        mContext,
                        new CustomCenterDialog.onConfirmClick() {
                            @Override
                            public void onConfirm() {
                                finish();
                            }
                        });
        centerDialog.setContent(getString(R.string.watch_wallet_tip));
        new XPopup.Builder(mContext).asCustom(centerDialog).show();
    }

    /**
     * * init layout
     *
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_send_hd;
    }

    private void setMinAmount() {
        switch (baseUnit) {
            case Constant.BTC_UNIT_BTC:
                minAmount = BigDecimal.valueOf(0.00000546);
                scale = 8;
                break;
            case Constant.BTC_UNIT_M_BTC:
                minAmount = BigDecimal.valueOf(0.00546);
                scale = 5;
                break;
            case Constant.BTC_UNIT_M_BITS:
                minAmount = BigDecimal.valueOf(5.46);
                scale = 2;
                break;
        }
    }

    @OnClick({
        R.id.img_back,
        R.id.text_max_amount,
        R.id.text_customize_fee_rate,
        R.id.linear_slow,
        R.id.linear_recommend,
        R.id.linear_fast,
        R.id.text_rollback,
        R.id.btn_next,
        R.id.paste_address,
        R.id.img_scan
    })
    @SingleClick
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_max_amount:
                if (Strings.isNullOrEmpty(editReceiverAddress.getText().toString())) {
                    showToast(R.string.input_number);
                } else {
                    isSetBig = true;
                    // 点击最大之后也需要刷新当前
                    calculateMaxSpendableAmount();
                    refreshFeeView();
                }
                break;
            case R.id.text_customize_fee_rate:
                if (mCurrentFeeDetails != null && mCurrentFeeDetails.getSlow() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putDouble(
                            Constant.CUSTOMIZE_FEE_RATE_MIN,
                            mCurrentFeeDetails.getSlow().getFeerate());
                    bundle.putDouble(
                            Constant.CUSTOMIZE_FEE_RATE_MAX,
                            mCurrentFeeDetails.getFast().getFeerate() * 20);
                    bundle.putInt(Constant.TAG_TX_SIZE, transactionSize);
                    bundle.putString(Constant.HDWALLET_NAME, hdWalletName);
                    String customFeeRemember =
                            (String) MySPManager.getInstance().get(hdWalletName, "");
                    if (!Strings.isNullOrEmpty(customFeeRemember)) {
                        bundle.putDouble(Constant.FEE_RATE, Double.parseDouble(customFeeRemember));
                    } else {
                        if (selectFlag == RECOMMENDED_FEE_RATE) {
                            bundle.putDouble(Constant.FEE_RATE, normalRate);
                        } else if (selectFlag == SLOW_FEE_RATE) {
                            bundle.putDouble(Constant.FEE_RATE, slowRate);
                        } else if (selectFlag == FAST_FEE_RATE) {
                            bundle.putDouble(Constant.FEE_RATE, fastRate);
                        }
                    }
                    feeDialog = new CustomizeFeeDialog();
                    feeDialog.setArguments(bundle);
                    feeDialog.show(getSupportFragmentManager(), "customize_fee");
                } else {
                    showToast(R.string.feerate_failure);
                }
                break;
            case R.id.linear_slow:
                viewSlow.setVisibility(View.VISIBLE);
                viewRecommend.setVisibility(View.GONE);
                viewFast.setVisibility(View.GONE);
                checkboxSlow.setVisibility(View.VISIBLE);
                checkboxRecommend.setVisibility(View.GONE);
                checkboxFast.setVisibility(View.GONE);
                if (mCurrentFeeDetails != null && mCurrentFeeDetails.getSlow() != null) {
                    currentFeeRate = mCurrentFeeDetails.getSlow().getFeerate();
                    currentTempTransaction = tempSlowTransaction;
                    selectFlag = SLOW_FEE_RATE;
                    keyBoardHideRefresh();
                } else {
                    showToast(R.string.feerate_failure);
                }
                break;
            case R.id.linear_recommend:
                viewSlow.setVisibility(View.GONE);
                viewRecommend.setVisibility(View.VISIBLE);
                viewFast.setVisibility(View.GONE);
                checkboxSlow.setVisibility(View.GONE);
                checkboxRecommend.setVisibility(View.VISIBLE);
                checkboxFast.setVisibility(View.GONE);
                if (mCurrentFeeDetails != null && mCurrentFeeDetails.getNormal() != null) {
                    currentFeeRate = mCurrentFeeDetails.getNormal().getFeerate();
                    currentTempTransaction = tempRecommendTransaction;
                    selectFlag = RECOMMENDED_FEE_RATE;
                    keyBoardHideRefresh();
                } else {
                    showToast(R.string.feerate_failure);
                }
                break;
            case R.id.linear_fast:
                viewSlow.setVisibility(View.GONE);
                viewRecommend.setVisibility(View.GONE);
                viewFast.setVisibility(View.VISIBLE);
                checkboxSlow.setVisibility(View.GONE);
                checkboxRecommend.setVisibility(View.GONE);
                checkboxFast.setVisibility(View.VISIBLE);
                if (mCurrentFeeDetails != null && mCurrentFeeDetails.getFast() != null) {
                    currentFeeRate = mCurrentFeeDetails.getFast().getFeerate();
                    currentTempTransaction = tempFastTransaction;
                    selectFlag = FAST_FEE_RATE;
                    keyBoardHideRefresh();
                } else {
                    showToast(R.string.feerate_failure);
                }
                break;
            case R.id.text_rollback:
                turnOffCustomize();
                break;
            case R.id.paste_address:
                editReceiverAddress.setText(ClipboardUtils.pasteText(this));
                if (!mIsSoftKeyboardShowing) {
                    keyBoardHideRefresh();
                }
                break;
            case R.id.btn_next:
                if (signClickable) {
                    send();
                } else {
                    showToast(R.string.confirm_hardware_msg);
                }
                break;
            case R.id.img_scan:
                subscribe =
                        rxPermissions
                                .request(Manifest.permission.CAMERA)
                                .subscribe(
                                        granted -> {
                                            if (granted) {
                                                // If you have already authorized it, you can
                                                // directly jump to the QR code scanning interface
                                                Intent intent2 =
                                                        new Intent(
                                                                getActivity(),
                                                                CaptureActivity.class);
                                                ZxingConfig config = new ZxingConfig();
                                                config.setPlayBeep(true);
                                                config.setShake(true);
                                                config.setDecodeBarCode(false);
                                                config.setFullScreenScan(true);
                                                config.setShowAlbum(false);
                                                config.setShowbottomLayout(false);
                                                intent2.putExtra(
                                                        com.yzq.zxinglibrary.common.Constant
                                                                .INTENT_ZXING_CONFIG,
                                                        config);
                                                startActivityForResult(intent2, REQUEST_SCAN_CODE);
                                            } else {
                                                Toast.makeText(
                                                                getActivity(),
                                                                R.string.photopersion,
                                                                Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                break;
            default:
                break;
        }
    }

    /** 取消自定义费率 */
    private void turnOffCustomize() {
        isCustom = false;
        linearRateSelector.setVisibility(View.VISIBLE);
        linearCustomize.setVisibility(View.GONE);
        if (selectFlag == RECOMMENDED_FEE_RATE) {
            if (mCurrentFeeDetails != null && mCurrentFeeDetails.getNormal() != null) {
                currentFeeRate = mCurrentFeeDetails.getNormal().getFeerate();
            }
        } else if (selectFlag == SLOW_FEE_RATE) {
            if (mCurrentFeeDetails != null && mCurrentFeeDetails.getSlow() != null) {
                currentFeeRate = mCurrentFeeDetails.getSlow().getFeerate();
            }
        } else if (selectFlag == FAST_FEE_RATE) {
            if (mCurrentFeeDetails != null && mCurrentFeeDetails.getFast() != null) {
                currentFeeRate = mCurrentFeeDetails.getFast().getFeerate();
            }
        }
        keyBoardHideRefresh();
    }

    private String getTransferTime(double time) {
        if (time >= 1) {
            return String.format(
                    "%s%s%s",
                    getString(R.string.about_), (int) time + "", getString(R.string.minute));
        } else {
            return String.format(
                    "%s%s%s",
                    getString(R.string.about_), (int) (time * 60) + "", getString(R.string.second));
        }
    }

    /** 判断地址和金额是否正确填写完毕 */
    private boolean sendReady() {
        if (Strings.isNullOrEmpty(editReceiverAddress.getText().toString())) {
            showToast(R.string.input_number);
            return false;
        }
        if (Strings.isNullOrEmpty(editAmount.getText().toString())) {
            showToast(R.string.input_out_number);
            return false;
        }
        return !(Double.parseDouble(amount) <= 0);
    }

    /** 获取费率详情 */
    private void getDefaultFee() {
        try {
            PyResponse<String> response = PyEnv.getFeeInfo("", "", "", "", "");
            String errors = response.getErrors();
            if (Strings.isNullOrEmpty(errors)) {
                Logger.json(response.getResult());
                mCurrentFeeDetails = CurrentFeeDetails.objectFromDate(response.getResult());
                transactionSize = mCurrentFeeDetails.getFast().getSize();
                initFeeSelectorStatus();
            } else {
                showToast(R.string.get_fee_info_failed);
            }
            currentFeeRate = mCurrentFeeDetails.getNormal().getFeerate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 初始化三种等级手续费的默认视图 */
    private void initFeeSelectorStatus() {
        try {
            textSpendTime0.setText(
                    String.format(
                            "%s %s %s",
                            getString(R.string.about_),
                            mCurrentFeeDetails == null ? 0 : mCurrentFeeDetails.getSlow().getTime(),
                            getString(R.string.minute)));
            customSize = mCurrentFeeDetails.getSlow().getSize();
            textFeeInBtc0.setText(
                    String.format(
                            Locale.ENGLISH,
                            "%s %s",
                            mCurrentFeeDetails.getSlow().getFee(),
                            baseUnit));
            slowRate = mCurrentFeeDetails.getSlow().getFeerate();
            PyResponse<String> response0 = PyEnv.exchange(mCurrentFeeDetails.getSlow().getFee());
            String errors0 = response0.getErrors();
            if (Strings.isNullOrEmpty(errors0)) {
                textFeeInCash0.setText(
                        String.format(
                                Locale.ENGLISH, "%s %s", currencySymbols, response0.getResult()));
            } else {
                showToast(errors0);
            }
            textSpendTime1.setText(
                    String.format(
                            "%s %s %s",
                            getString(R.string.about_),
                            mCurrentFeeDetails == null
                                    ? 0
                                    : mCurrentFeeDetails.getNormal().getTime(),
                            getString(R.string.minute)));
            textFeeInBtc1.setText(
                    String.format(
                            Locale.ENGLISH,
                            "%s %s",
                            mCurrentFeeDetails.getNormal().getFee(),
                            baseUnit));
            normalRate = mCurrentFeeDetails.getNormal().getFeerate();
            PyResponse<String> response1 = PyEnv.exchange(mCurrentFeeDetails.getNormal().getFee());
            String errors1 = response1.getErrors();
            if (Strings.isNullOrEmpty(errors1)) {
                textFeeInCash1.setText(
                        String.format(
                                Locale.ENGLISH, "%s %s", currencySymbols, response1.getResult()));
            }
            textSpendTime2.setText(
                    String.format(
                            "%s %s %s",
                            getString(R.string.about_),
                            mCurrentFeeDetails == null ? 0 : mCurrentFeeDetails.getFast().getTime(),
                            getString(R.string.minute)));
            textFeeInBtc2.setText(
                    String.format(
                            Locale.ENGLISH,
                            "%s %s",
                            mCurrentFeeDetails.getFast().getFee(),
                            baseUnit));
            fastRate = mCurrentFeeDetails.getFast().getFeerate();
            PyResponse<String> response2 = PyEnv.exchange(mCurrentFeeDetails.getFast().getFee());
            String errors2 = response2.getErrors();
            if (Strings.isNullOrEmpty(errors2)) {
                textFeeInCash2.setText(
                        String.format(
                                Locale.ENGLISH, "%s %s", currencySymbols, response2.getResult()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 计算最大可用余额 */
    private void calculateMaxSpendableAmount() {
        PyResponse<TemporaryTxInfo> pyResponse =
                PyEnv.getBtcFeeByFeeRate(
                        mWalletType,
                        editReceiverAddress.getText().toString(),
                        "!",
                        String.valueOf(currentFeeRate));
        String errorMsg = pyResponse.getErrors();
        if (Strings.isNullOrEmpty(errorMsg)) {
            TemporaryTxInfo temporaryTxInfo = pyResponse.getResult();
            maxAmount =
                    BigDecimal.valueOf(Double.parseDouble(balance))
                            .subtract(BigDecimal.valueOf(temporaryTxInfo.getFee()));
            if (isSetBig) {
                editAmount.setText(maxAmount.toPlainString());
            } else {
                // 不能大于最大值，
                if (!Strings.isNullOrEmpty(amount) && Double.parseDouble(amount) > 0) {
                    BigDecimal decimal = BigDecimal.valueOf(Double.parseDouble(amount));
                    if (decimal.compareTo(maxAmount) >= 0) {
                        editAmount.setText(maxAmount.toPlainString());
                    }
                }
            }
            editAmount.setFocusable(true);
        } else {
            showToast(errorMsg);
        }
    }

    /** 发送交易 */
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
    public void onGotSoftPass(GotPassEvent event) {
        softSign(event.getPassword());
    }

    /** 软件签名交易 */
    private void softSign(String password) {
        PyResponse<TransactionInfoBean> pyResponse = PyEnv.signTx(rawTx, password);
        String errorMsg = pyResponse.getErrors();
        if (Strings.isNullOrEmpty(errorMsg)) {
            broadcastTx(pyResponse.getResult().getTx());
        } else {
            showToast(errorMsg);
        }
    }

    /** 广播交易 */
    private void broadcastTx(String signedTx) {
        PyResponse<Void> response = PyEnv.broadcast(signedTx);
        String errors = response.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            TransactionCompletion.start(mContext, Vm.CoinType.BTC, signedTx, amounts);
            finish();
        } else {
            showToast(errors);
        }
    }

    /** 弹出交易确认框 */
    private void sendConfirmDialog(String rawTx) {
        PyResponse<String> response = PyEnv.analysisRawTx(rawTx);
        String errors = response.getErrors();
        if (!Strings.isNullOrEmpty(errors)) {
            showToast(errors);
            return;
        }
        TransactionInfoBean info = TransactionInfoBean.objectFromData(response.getResult());
        // set see view
        String sender = info.getInputAddr().get(0).getAddress();
        String receiver = info.getOutputAddr().get(0).getAddr();
        amounts = info.getAmount();
        String fee = info.getFee();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.TRANSACTION_SENDER, sender);
        bundle.putString(Constant.TRANSACTION_RECEIVER, receiver);
        bundle.putString(Constant.TRANSACTION_AMOUNT, amounts);
        bundle.putString(Constant.TRANSACTION_FEE, fee);
        bundle.putString(Constant.WALLET_LABEL, hdWalletName);
        bundle.putInt(
                Constant.WALLET_TYPE,
                Constant.WALLET_TYPE_HARDWARE.equals(showWalletType)
                        ? Constant.WALLET_TYPE_HARDWARE_PERSONAL
                        : Constant.WALLET_TYPE_SOFTWARE);
        confirmDialog = new TransactionConfirmDialog();
        confirmDialog.setArguments(bundle);
        confirmDialog.show(getSupportFragmentManager(), "confirm");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfirm(ButtonRequestConfirmedEvent event) {
        if (Constant.WALLET_TYPE_HARDWARE.equals(showWalletType)) {
            TransactionInfoBean info = TransactionInfoBean.objectFromData(signedTx);
            broadcastTx(info.getTx());
        } else if (Constant.BTC_WATCH.equals(showWalletType)) {
            showWatchQrDialog();
        } else {
            // 获取主密码
            startActivity(new Intent(this, SoftPassActivity.class));
        }
    }

    private void showWatchQrDialog() {
        new XPopup.Builder(mContext).asCustom(new CustomWatchWalletDialog(mContext, rawTx)).show();
    }

    private boolean getFee(String feeRate, int type) {
        PyResponse<TemporaryTxInfo> pyResponse =
                PyEnv.getBtcFeeByFeeRate(
                        mWalletType,
                        editReceiverAddress.getText().toString(),
                        isSetBig ? "!" : amount,
                        feeRate);
        Logger.d(" currentFeeRate-->" + feeRate);
        String errors = pyResponse.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            TemporaryTxInfo temporaryTxInfo = pyResponse.getResult();
            String fee = BigDecimal.valueOf(temporaryTxInfo.getFee()).toPlainString();
            String time = getTransferTime(temporaryTxInfo.getTime());

            String temp = temporaryTxInfo.getTx();
            transactionSize = temporaryTxInfo.getSize();
            switch (type) {
                case RECOMMENDED_FEE_RATE:
                    textSpendTime1.setText(time);
                    textFeeInBtc1.setText(String.format(Locale.ENGLISH, "%s %s", fee, baseUnit));
                    PyResponse<String> response1 = PyEnv.exchange(fee);
                    String errors1 = response1.getErrors();
                    if (Strings.isNullOrEmpty(errors1)) {
                        textFeeInCash1.setText(
                                String.format(
                                        Locale.ENGLISH,
                                        "%s %s",
                                        currencySymbols,
                                        response1.getResult()));
                    } else {
                        showToast(errors1);
                        return false;
                    }
                    tempRecommendTransaction = temp;
                    if (selectFlag == RECOMMENDED_FEE_RATE) {
                        currentTempTransaction = temp;
                    }
                    return true;
                case SLOW_FEE_RATE:
                    textSpendTime0.setText(time);
                    textFeeInBtc0.setText(String.format(Locale.ENGLISH, "%s %s", fee, baseUnit));
                    PyResponse<String> response0 = PyEnv.exchange(fee);
                    String errors0 = response0.getErrors();
                    if (Strings.isNullOrEmpty(errors0)) {
                        textFeeInCash0.setText(
                                String.format(
                                        Locale.ENGLISH,
                                        "%s %s",
                                        currencySymbols,
                                        response0.getResult()));
                    } else {
                        return false;
                    }
                    if (selectFlag == SLOW_FEE_RATE) {
                        currentTempTransaction = temp;
                    }
                    tempSlowTransaction = temp;
                    return true;
                case FAST_FEE_RATE:
                    textSpendTime2.setText(time);
                    textFeeInBtc2.setText(String.format(Locale.ENGLISH, "%s %s", fee, baseUnit));
                    PyResponse<String> response2 = PyEnv.exchange(fee);
                    String errors2 = response2.getErrors();
                    if (Strings.isNullOrEmpty(errors2)) {
                        textFeeInCash2.setText(
                                String.format(
                                        Locale.ENGLISH,
                                        "%s %s",
                                        currencySymbols,
                                        response2.getResult()));
                    } else {
                        return false;
                    }
                    if (selectFlag == FAST_FEE_RATE) {
                        currentTempTransaction = temp;
                    }
                    tempFastTransaction = temp;
                    return true;
                case CUSTOMIZE_FEE_RATE:
                    textCustomizeSpendTime.setText(time);
                    textFeeCustomizeInBtc.setText(
                            String.format(Locale.ENGLISH, "%s %s", fee, baseUnit));
                    PyResponse<String> response3 = PyEnv.exchange(fee);
                    String errors3 = response3.getErrors();
                    if (Strings.isNullOrEmpty(errors3)) {
                        String string =
                                String.format(
                                        Locale.ENGLISH,
                                        "%s %s",
                                        currencySymbols,
                                        response3.getResult());
                        if (feeDialog.isVisible()) {
                        } else {
                            textFeeCustomizeInCash.setText(
                                    String.format(
                                            Locale.ENGLISH,
                                            "%s %s",
                                            currencySymbols,
                                            response3.getResult()));
                        }
                    } else {
                        return false;
                    }
                    currentTempTransaction = temp;
                    return true;
                default:
                    return false;
            }
        } else {
            if (type == FAST_FEE_RATE) {
                showToast(errors);
            }
        }
        return false;
    }

    /** 改变发送按钮状态 */
    private void changeButton() {
        if (addressInvalid && isFeeValid && !Strings.isNullOrEmpty(amount)) {
            BigDecimal decimal = BigDecimal.valueOf(Double.parseDouble(amount));
            if (decimal.compareTo(minAmount) < 0) {
                btnNext.setEnabled(false);
            } else {
                btnNext.setEnabled(true);
            }
        } else {
            btnNext.setEnabled(false);
        }
    }

    /** 自定义费率确认响应 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCustomizeFee(CustomizeFeeRateEvent event) {
        isCustom = true;
        linearRateSelector.setVisibility(View.GONE);
        linearCustomize.setVisibility(View.VISIBLE);
        currentFeeRate = Double.parseDouble(event.getFeeRate());
        textFeeCustomizeInBtc.setText(
                String.format(
                        Locale.ENGLISH,
                        "%s %s",
                        event.getFee(),
                        mSystemConfigManager.getCurrentBaseUnit()));
        textFeeCustomizeInCash.setText(
                String.format(
                        Locale.ENGLISH,
                        "%s %s",
                        mSystemConfigManager.getCurrentFiatSymbol(),
                        event.getCash()));
        textCustomizeSpendTime.setText(
                String.format(
                        "%s%s%s",
                        getString(R.string.about_), event.getTime(), getString(R.string.minute)));
        keyBoardHideRefresh();
    }

    /** 自定义费率监听 */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGetFee(GetFeeEvent event) {
        String feeRate = event.getFeeRate();
        isFeeValid = getFee(feeRate, CUSTOMIZE_FEE_RATE);
    }

    /** 注册全局视图监听器 */
    private void registerLayoutChangeListener() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenHeight = metric.heightPixels;
        mIsSoftKeyboardShowing = false;
        ViewTreeObserver.OnGlobalLayoutListener mLayoutChangeListener =
                () -> {
                    // Determine the size of window visible area
                    Rect r = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                    // If the difference between screen height and window visible area height is
                    // greater than 1 / 3 of the whole screen height, it means that the soft
                    // keyboard is in display, otherwise, the soft keyboard is hidden.
                    int heightDifference = screenHeight - (r.bottom - r.top);
                    boolean isKeyboardShowing = heightDifference > screenHeight / 3;
                    // If the status of the soft keyboard was previously displayed, it is now
                    // closed, or it was previously closed, it is now displayed, it means that the
                    // status of the soft keyboard has changed
                    if ((mIsSoftKeyboardShowing && !isKeyboardShowing)
                            || (!mIsSoftKeyboardShowing && isKeyboardShowing)) {
                        mIsSoftKeyboardShowing = isKeyboardShowing;
                        if (!mIsSoftKeyboardShowing && isResume) {
                            keyBoardHideRefresh();
                        }
                    }
                };
        // Register layout change monitoring
        getWindow()
                .getDecorView()
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(mLayoutChangeListener);
    }

    private void keyBoardHideRefresh() {
        if (editAmount.getText().toString().endsWith(".")) {
            amount =
                    editAmount
                            .getText()
                            .toString()
                            .substring(0, editAmount.getText().toString().length() - 1)
                            .trim();
        } else {
            amount = editAmount.getText().toString().trim();
        }
        if (Strings.isNullOrEmpty(editReceiverAddress.getText().toString().trim())) {
            showToast(R.string.input_address);
            btnNext.setEnabled(false);
            return;
        } else {
            // 收起键盘地址认为 focus，所以再次校验地址正确性
            getAddressIsValid(false);
        }
        if (Strings.isNullOrEmpty(editAmount.getText().toString().trim())) {
            showToast(R.string.inoutnum);
            btnNext.setEnabled(false);
            return;
        } else {
            if (!Strings.isNullOrEmpty(amount) && Double.parseDouble(amount) > 0) {
                if (addressInvalid) {
                    calculateMaxSpendableAmount();
                }
            } else {
                showToast(R.string.inoutnum);
                btnNext.setEnabled(false);
                return;
            }
        }
        refreshFeeView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
    }

    /**
     * 判断验证 BTC 金额有效性，并去掉多余的 0
     *
     * @param amount BTC 金额
     * @return 返回处理过的 BTC 金额
     */
    @WorkerThread
    @Nullable
    private String checkAndConvertAmount(@Nullable String amount) {
        BigDecimal amountBigDecimal;
        if (TextUtils.isEmpty(amount)) {
            amountBigDecimal = new BigDecimal("0");
        } else {
            try {
                amountBigDecimal = new BigDecimal(amount);
            } catch (NumberFormatException e) {
                amountBigDecimal = new BigDecimal("0");
            }
        }
        if (amountBigDecimal.equals(BigDecimal.ZERO)) {
            return null;
        }
        int scale;
        switch (baseUnit) {
            case Constant.BTC_UNIT_BTC:
                scale = 8;
                break;
            case Constant.BTC_UNIT_M_BTC:
                scale = 5;
                break;
            case Constant.BTC_UNIT_M_BITS:
                scale = 2;
                break;
            default:
                scale = 0;
        }
        return amountBigDecimal
                .setScale(scale, RoundingMode.DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Scan QR code return
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String content =
                        data.getStringExtra(com.yzq.zxinglibrary.common.Constant.CODED_CONTENT);
                Disposable disposable =
                        Observable.create(
                                        (ObservableOnSubscribe<MainSweepcodeBean.DataBean>)
                                                emitter -> {
                                                    MainSweepcodeBean.DataBean dataBean =
                                                            new QRDecode().decodeAddress(content);
                                                    if (dataBean == null) {
                                                        emitter.onError(
                                                                new RuntimeException(
                                                                        "Parse failure"));
                                                    } else {
                                                        emitter.onNext(dataBean);
                                                    }
                                                    emitter.onComplete();
                                                })
                                .map(
                                        dataBean -> {
                                            String amountStr =
                                                    checkAndConvertAmount(dataBean.getAmount());
                                            if (!TextUtils.isEmpty(amountStr)) {
                                                dataBean.setAmount(amountStr);
                                            }
                                            return dataBean;
                                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        output -> {
                                            editReceiverAddress.setText(output.getAddress());
                                            if (null != output
                                                    && !TextUtils.isEmpty(output.getAmount())) {
                                                editAmount.setText(output.getAmount());
                                                keyBoardHideRefresh();
                                            } else {
                                                getAddressIsValid(true);
                                            }
                                        },
                                        e -> {
                                            e.printStackTrace();
                                            showToast(R.string.invalid_address);
                                        });
                mCompositeDisposable.add(disposable);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    /** 校验收款地址是否有效 */
    private void getAddressIsValid(boolean isScan) {
        String address = editReceiverAddress.getText().toString();
        if (!Strings.isNullOrEmpty(address)) {
            PyResponse<Void> response = PyEnv.VerifyLegality(address, "address", mWalletType);
            if (Strings.isNullOrEmpty(response.getErrors())) {
                addressInvalid = true;
            } else {
                addressInvalid = false;
            }
            if (!addressInvalid) {
                editReceiverAddress.setText("");
                btnNext.setEnabled(false);
                if (isScan) {
                    CustomCenterDialog centerDialog = new CustomCenterDialog(mContext, false);
                    centerDialog.setContent(getString(R.string.re_input));
                    centerDialog.setTitle(getString(R.string.invalid_btc));
                    new XPopup.Builder(mContext).asCustom(centerDialog).show();
                } else {
                    showToast(R.string.invalid_address);
                }
            }
        }
    }

    /** 交易金额实时监听 */
    @OnTextChanged(value = R.id.edit_amount, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChangeAmount(CharSequence sequence) {
        amount = sequence.toString();
        if (!String.valueOf(maxAmount).equals(amount)) {
            isSetBig = false;
        }
        // 金额以点开头
        if (amount.startsWith(".")) {
            editAmount.setText("");
        }
        if (isAmountClickPaste) {
            isAmountClickPaste = false;
            refreshFeeView();
        }
    }

    @OnTextChanged(
            value = R.id.edit_receiver_address,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onAddress(CharSequence sequence) {
        if (isAddressClickPaste) {
            isAddressClickPaste = false;
            keyBoardHideRefresh();
        }
    }

    @OnFocusChange(value = R.id.edit_receiver_address)
    public void onFocusChanged(boolean focused) {
        if (!focused) {
            keyBoardHideRefresh();
        }
    }

    @OnFocusChange(value = R.id.edit_amount)
    public void onEditAmountFocusChange(boolean focused) {
        if (!focused) {
            if (!Strings.isNullOrEmpty(amount) && Double.parseDouble(amount) > 0) {
                BigDecimal decimal = BigDecimal.valueOf(Double.parseDouble(amount));
                if (decimal.compareTo(minAmount) < 0) {
                    String min =
                            String.format(
                                    Locale.ENGLISH,
                                    "%s",
                                    minAmount.stripTrailingZeros().toPlainString());
                    editAmount.setText(min);
                    editAmount.setSelection(min.length());
                } else if (decimal.compareTo(decimalBalance) >= 0) {
                    if (addressInvalid) {
                        calculateMaxSpendableAmount();
                    }
                }
            }
        }
    }

    /** 获取三种不同费率对应的临时交易 */
    private void refreshFeeView() {
        isFeeValid = isCanRefresh();
        if (isFeeValid) {
            if (!isCustom) {
                refreshOther();
            }
        }
        changeButton();
    }

    private boolean isCanRefresh() {
        boolean success;
        if (isCustom) {
            return getFee(String.valueOf(currentFeeRate), CUSTOMIZE_FEE_RATE);
        } else {
            try {
                if (mCurrentFeeDetails != null) {
                    double fast = mCurrentFeeDetails.getFast().getFeerate();
                    return getFee(Double.toString(fast), FAST_FEE_RATE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void refreshOther() {
        Optional.ofNullable(mCurrentFeeDetails)
                .ifPresent(
                        (currentFeeDetails1 -> {
                            if (sendReady()) {
                                synchronized (SendHdActivity.class) {
                                    double normal = currentFeeDetails1.getNormal().getFeerate();
                                    getFee(Double.toString(normal), RECOMMENDED_FEE_RATE);
                                    double slow = currentFeeDetails1.getSlow().getFeerate();
                                    getFee(Double.toString(slow), SLOW_FEE_RATE);
                                }
                            }
                        }));
    }

    /** 硬件签名方法 */
    private void hardwareSign(String rawTx) {
        new BusinessAsyncTask()
                .setHelper(this)
                .execute(
                        BusinessAsyncTask.SIGN_TX,
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
        signClickable = false;
    }

    @Override
    public void onException(Exception e) {
        showToast(e.getMessage());
        signClickable = true;
    }

    @Override
    public void onResult(String s) {
        if (!Strings.isNullOrEmpty(s)) {
            signedTx = s;
            if (confirmDialog != null && confirmDialog.getBtnConfirmPay() != null) {
                confirmDialog.getBtnConfirmPay().setEnabled(true);
            }
        } else {
            finish();
        }
        signClickable = true;
    }

    @Override
    public void onCancelled() {
        signClickable = true;
    }

    @Override
    public void currentMethod(String methodName) {}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean needEvents() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if ("finish".equals(msgVote)) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mCompositeDisposable.clear();
        if (Objects.nonNull(subscribe)) {
            subscribe.dispose();
        }
    }
}
