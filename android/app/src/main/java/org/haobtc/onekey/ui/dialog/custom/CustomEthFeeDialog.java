package org.haobtc.onekey.ui.dialog.custom;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import com.google.common.base.Strings;
import com.lxj.xpopup.core.BottomPopupView;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Locale;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CurrentFeeDetails;
import org.haobtc.onekey.business.wallet.SystemConfigManager;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.CustomizeFeeRateEvent;
import org.haobtc.onekey.utils.MyDialog;
import org.haobtc.onekey.utils.ToastUtils;
import org.haobtc.onekey.viewmodel.AppWalletViewModel;

/** @Description: 自定义Eth费率弹框 @Author: peter Qin */
public class CustomEthFeeDialog extends BottomPopupView {

    @BindView(R.id.img_cancel)
    ImageView imgCancel;

    @BindView(R.id.edit_fee_rate)
    EditText editFeeByte;

    @BindView(R.id.text_time)
    TextView textTime;

    @BindView(R.id.text_size)
    EditText textSize;

    @BindView(R.id.text_fee_in_btc)
    TextView textFeeInBtc;

    @BindView(R.id.text_fee_in_cash)
    TextView textFeeInCash;

    @BindView(R.id.btn_next)
    Button btnNext;

    @BindView(R.id.title1_show)
    TextView titleLeft;

    @BindView(R.id.title2_show)
    TextView titleRight;

    private int size;
    private static final double feeRateMin = 0.4;
    private double feeRateMax;
    private String time;
    private String fee;
    private String fiat;
    private String mWalletName;
    private SystemConfigManager mSystemConfigManager;
    private double feeRate;
    private Context mContext;
    private onCustomInterface mOnCustomInterface;
    private Unbinder bind;
    private CompositeDisposable mDisposable;
    private String mAddress;
    private String mSendAmount;
    private AppWalletViewModel mAppWalletViewModel;
    private BigDecimal coinBalance;

    public void setCurrentFeeDetails(CurrentFeeDetails currentFeeDetails) {
        this.mCurrentFeeDetails = currentFeeDetails;
    }

    private MyDialog mProgressDialog;
    private CurrentFeeDetails mCurrentFeeDetails;

    public CustomEthFeeDialog(
            @NonNull Context context,
            int size,
            double feeRateMax,
            String walletName,
            double nowRate,
            AppWalletViewModel appWalletViewModel) {
        super(context);
        this.size = size;
        this.feeRateMax = feeRateMax;
        this.mWalletName = walletName;
        this.feeRate = nowRate;
        this.mContext = context;
        this.mAppWalletViewModel = appWalletViewModel;
    }

    public CustomEthFeeDialog(@NonNull Context context) {
        super(context);
    }

    public void setOnCustomInterface(onCustomInterface onCustomInterface) {
        mOnCustomInterface = onCustomInterface;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        coinBalance =
                mAppWalletViewModel
                        .currentWalletAssetsList
                        .getValue()
                        .getCoinAsset()
                        .getBalance()
                        .getBalance();
        bind = ButterKnife.bind(this);
        mDisposable = new CompositeDisposable();
        titleLeft.setText(R.string.eth_gas_fee);
        titleRight.setText(R.string.eth_gas_limit);
        textSize.setBackground(mContext.getResources().getDrawable(R.drawable.gray_broken));
        textSize.setTextColor(mContext.getColor(R.color.text_two));
        mSystemConfigManager = new SystemConfigManager(mContext);
        textSize.setText(String.valueOf(size));
        if (String.valueOf(feeRate).contains(".")) {
            String rate =
                    String.valueOf(feeRate).substring(0, String.valueOf(feeRate).indexOf("."));
            editFeeByte.setText(rate);
            editFeeByte.setSelection(rate.length());
        } else {
            editFeeByte.setText(String.valueOf(feeRate));
            editFeeByte.setSelection(String.valueOf(feeRate).length());
        }
    }

    @OnClick({R.id.img_cancel, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_cancel:
                dismiss();
                break;
            case R.id.btn_next:
                if (!judgeGasPrice(editFeeByte.getText().toString())) {
                    return;
                }
                if (!judgeGasLimit(textSize.getText().toString())) {
                    return;
                }
                if (mOnCustomInterface != null) {
                    CustomizeFeeRateEvent customizeFeeRateEvent =
                            new CustomizeFeeRateEvent(
                                    editFeeByte.getText().toString(),
                                    fee,
                                    fiat,
                                    String.valueOf(time));
                    customizeFeeRateEvent.setGasLimit(
                            new BigInteger(textSize.getText().toString()).intValue());
                    customizeFeeRateEvent.setGasPrice(editFeeByte.getText().toString().trim());
                    mOnCustomInterface.onCustomComplete(customizeFeeRateEvent);
                    dismiss();
                }
                break;
        }
    }

    @OnTextChanged(value = R.id.text_size, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSizeChanged(CharSequence text) {
        String gasLimitString = text.toString();
        if (!Strings.isNullOrEmpty(gasLimitString)
                && new BigInteger(gasLimitString).intValue() > 0
                && !Strings.isNullOrEmpty(editFeeByte.getText().toString())
                && new BigDecimal(editFeeByte.getText().toString()).doubleValue() > 0) {
            calculateData(editFeeByte.getText().toString().trim(), gasLimitString);
        } else {
            cleanShow();
        }
    }

    @OnTextChanged(value = R.id.edit_fee_rate, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(CharSequence text) {
        String feeRate = text.toString();
        if (!Strings.isNullOrEmpty(feeRate)
                && BigDecimal.valueOf(feeRateMax).doubleValue() > 0
                && !Strings.isNullOrEmpty(textSize.getText().toString())
                && new BigInteger(textSize.getText().toString()).intValue() > 0) {
            calculateData(feeRate, textSize.getText().toString().trim());
        } else {
            cleanShow();
        }
    }

    private void cleanShow() {
        btnNext.setEnabled(false);
        textTime.setText(mContext.getString(R.string.line));
        textFeeInBtc.setText(mContext.getString(R.string.line));
        textFeeInCash.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
        mDisposable.dispose();
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_fee_eth;
    }

    public interface onCustomInterface {
        void onCustomComplete(CustomizeFeeRateEvent customizeFeeRateEvent);
    }

    /**
     * local calculate
     *
     * @param gasPrice
     * @param gasLimit
     */
    private void calculateData(String gasPrice, String gasLimit) {
        if (!judgeGasPrice(gasPrice)) {
            btnNext.setEnabled(false);
            return;
        }
        if (!judgeGasLimit(gasLimit)) {
            btnNext.setEnabled(false);
            return;
        }
        fee =
                BigDecimal.valueOf(
                                (new BigDecimal(gasLimit).multiply(new BigDecimal(gasPrice)))
                                                .doubleValue()
                                        / Math.pow(10, 9))
                        .toPlainString();
        BigDecimal cash =
                new BigDecimal(
                        mCurrentFeeDetails
                                .getFast()
                                .getFiat()
                                .substring(0, mCurrentFeeDetails.getFast().getFiat().indexOf(" ")));

        BigDecimal bigDecimal =
                BigDecimal.valueOf(
                        new BigDecimal(fee).doubleValue()
                                / new BigDecimal(mCurrentFeeDetails.getFast().getFee())
                                        .doubleValue()
                                * cash.doubleValue());
        fiat =
                bigDecimal
                        .setScale(2, RoundingMode.DOWN)
                        .stripTrailingZeros()
                        .toPlainString(); // 当前展示的法币
        int timeTemp;
        if (new BigDecimal(fee).compareTo(new BigDecimal(mCurrentFeeDetails.getFast().getFee()))
                >= 0) {
            timeTemp = mCurrentFeeDetails.getFast().getTime();
        } else if (new BigDecimal(fee)
                        .compareTo(new BigDecimal(mCurrentFeeDetails.getNormal().getFee()))
                >= 0) {
            timeTemp = mCurrentFeeDetails.getNormal().getTime();
        } else {
            timeTemp = mCurrentFeeDetails.getSlow().getTime();
        }
        time =
                String.format(
                        "%s %s %s",
                        mContext.getString(R.string.about_),
                        timeTemp,
                        mContext.getString(R.string.minute));
        textTime.setText(time);
        textFeeInBtc.setText(
                String.format(
                        Locale.ENGLISH,
                        "%s %s",
                        fee,
                        mSystemConfigManager.getCurrentBaseUnit(Vm.convertCoinType(Constant.ETH))));
        textFeeInCash.setVisibility(View.VISIBLE);
        textFeeInCash.setText(
                String.format(
                        Locale.ENGLISH,
                        "≈ %s %s",
                        mSystemConfigManager.getCurrentFiatSymbol(),
                        fiat));
        double balance = new BigDecimal(fee).subtract(coinBalance).doubleValue();
        if (new BigDecimal(fee).subtract(coinBalance).doubleValue() >= 0) {
            ToastUtils.toast(mContext.getString(R.string.balance_zero));
            btnNext.setEnabled(false);
            return;
        }
        btnNext.setEnabled(true);
    }

    private String getTransferTime(double time) {
        if (time >= 1) {
            return String.format(
                    "%s%s%s",
                    mContext.getString(R.string.about_),
                    (int) time + "",
                    mContext.getString(R.string.minute));
        } else {
            return String.format(
                    "%s%s%s",
                    mContext.getString(R.string.about_),
                    (int) (time * 60) + "",
                    mContext.getString(R.string.second));
        }
    }

    private boolean judgeGasLimit(String gasLimit) {
        BigInteger bigLimit = new BigInteger(gasLimit);
        if (bigLimit.compareTo(BigInteger.valueOf(size)) < 0) {
            ToastUtils.toast(
                    String.format(
                            Locale.getDefault(),
                            mContext.getString(R.string.gas_limit_small),
                            size));
            return false;
        } else if (bigLimit.compareTo(BigInteger.valueOf(size * 10)) > 0) {
            ToastUtils.toast(
                    String.format(
                            Locale.getDefault(),
                            mContext.getString(R.string.gas_limit_large),
                            size * 10));

            return false;
        } else {
            return true;
        }
    }

    private boolean judgeGasPrice(String gasPrice) {
        BigDecimal feeRate1 = new BigDecimal(gasPrice);
        if (feeRate1.compareTo(BigDecimal.valueOf(feeRateMin)) < 0) {
            ToastUtils.toast(
                    String.format(
                            Locale.getDefault(),
                            mContext.getString(R.string.gas_price_small),
                            String.valueOf(feeRateMin)));

            return false;
        } else if (feeRate1.compareTo(BigDecimal.valueOf(feeRateMax)) > 0) {
            ToastUtils.toast(
                    String.format(
                            mContext.getString(R.string.gas_price_large),
                            String.valueOf(feeRateMax)));

            return false;
        } else {
            return true;
        }
    }
}
