package org.haobtc.onekey.ui.dialog.custom;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.common.base.Strings;
import com.lxj.xpopup.core.BottomPopupView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.TemporaryTxInfo;
import org.haobtc.onekey.business.wallet.SystemConfigManager;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.CustomizeFeeRateEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.utils.MyDialog;

import java.math.BigDecimal;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @Description: 自定义Eth费率弹框
 * @Author: peter Qin
 */
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
    private boolean isSetBig;
    private MyDialog mProgressDialog;

    public void setSetBig(boolean setBig) {
        this.isSetBig = setBig;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public void setSendAmount(String sendAmount) {
        this.mSendAmount = sendAmount;
    }

    public CustomEthFeeDialog(@NonNull Context context, int size, double feeRateMax, String walletName, double nowRate) {
        super(context);
        this.size = size;
        this.feeRateMax = feeRateMax;
        this.mWalletName = walletName;
        this.feeRate = nowRate;
        this.mContext = context;
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
        bind = ButterKnife.bind(this);
        mDisposable = new CompositeDisposable();
        titleLeft.setText(R.string.eth_gas_fee);
        titleRight.setText(R.string.eth_gas_limit);
        textSize.setBackground(mContext.getResources().getDrawable(R.drawable.gray_broken));
        textSize.setTextColor(mContext.getColor(R.color.text_two));
        mSystemConfigManager = new SystemConfigManager(mContext);
        textSize.setText(String.valueOf(size));
        if (String.valueOf(feeRate).contains(".")) {
            String rate = String.valueOf(feeRate).substring(0, String.valueOf(feeRate).indexOf("."));
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
                // 如果费率小于限制，不可创建
                boolean isGasPricePass = judgeGasPrice(editFeeByte.getText().toString());
                // gas_limit 不能小于默认值，不能大于10倍
                boolean isGasLimit = judgeGasLimit(textSize.getText().toString());
                if (isGasLimit && isGasPricePass) {
                    if (mOnCustomInterface != null) {
                        CustomizeFeeRateEvent customizeFeeRateEvent = new CustomizeFeeRateEvent(editFeeByte.getText().toString(), fee, fiat, String.valueOf(time));
                        customizeFeeRateEvent.setGasLimit(Integer.parseInt(textSize.getText().toString()));
                        mOnCustomInterface.onCustomComplete(customizeFeeRateEvent);
                    }
                    dismiss();
                }
                break;
        }
    }

    @OnTextChanged(value = R.id.text_size, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSizeChanged(CharSequence text) {
        String gasLimitString = text.toString();

        if (!Strings.isNullOrEmpty(gasLimitString) && Integer.parseInt(gasLimitString) > 0) {
            if (!Strings.isNullOrEmpty(editFeeByte.getText().toString())
                    && Double.parseDouble(editFeeByte.getText().toString()) > 0) {
                boolean isPass = judgeGasLimit(gasLimitString);
                if (isPass) {
                    Disposable disposable = getEthFeeRateObservable(editFeeByte.getText().toString().trim(), gasLimitString)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(temporaryTxInfoPyResponse ->
                                    dealWithData(temporaryTxInfoPyResponse, editFeeByte.getText().toString())
                            );
                    mDisposable.add(disposable);
                }
            }
        } else {
            cleanShow();
        }
    }

    private void dealWithData(PyResponse<TemporaryTxInfo> customFeeInfo, String feeRate) {
        String errors = customFeeInfo.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            TemporaryTxInfo temporaryTxInfo = customFeeInfo.getResult();
            fee = BigDecimal.valueOf(temporaryTxInfo.getFee()).toPlainString();
            fiat = temporaryTxInfo.getFiat();
            time = getTransferTime(temporaryTxInfo.getTime());
            textTime.setText(time);
            textFeeInBtc.setText(String.format(Locale.ENGLISH, "%s %s", fee, mSystemConfigManager.getCurrentBaseUnit(Vm.convertCoinType(Constant.ETH))));
            textFeeInCash.setVisibility(View.VISIBLE);
            textFeeInCash.setText(String.format(Locale.ENGLISH, "≈ %s %s", mSystemConfigManager.getCurrentFiatSymbol(), temporaryTxInfo.getFiat().substring(0, temporaryTxInfo.getFiat().indexOf(" "))));
            btnNext.setEnabled(true);
        } else {
            cleanShow();
        }
    }

    @OnTextChanged(value = R.id.edit_fee_rate, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(CharSequence text) {
        String feeRate = text.toString();
        if (!Strings.isNullOrEmpty(feeRate) && Double.parseDouble(feeRate) > 0 && !Strings.isNullOrEmpty(textSize.getText().toString())
                && Double.parseDouble(textSize.getText().toString()) > 0) {
            boolean isPass = judgeGasPrice(feeRate);
            if (isPass) {
                Disposable disposable = getEthFeeRateObservable(feeRate, textSize.getText().toString().trim())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(temporaryTxInfoPyResponse ->
                                dealWithData(temporaryTxInfoPyResponse, feeRate)
                        );
                mDisposable.add(disposable);
            }
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


    private Observable<PyResponse<TemporaryTxInfo>> getEthFeeRateObservable(String feeRate, String mGasLimit) {
        return Observable.create(emitter -> {
            PyResponse<TemporaryTxInfo> pyResponse = PyEnv.getEthFeeByFeeRate(Constant.ETH, mAddress, isSetBig ? "!" : mSendAmount, feeRate, mGasLimit);
            emitter.onNext(pyResponse);
            emitter.onComplete();
        });
    }


    public void showProgress() {
        dismissProgress();
        mProgressDialog = MyDialog.showDialog(mContext);
        mProgressDialog.show();
        mProgressDialog.onTouchOutside(false);

    }

    public void dismissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private String getTransferTime(double time) {
        if (time >= 1) {
            return String.format("%s%s%s", mContext.getString(R.string.about_), (int) time + "", mContext.getString(R.string.minute));
        } else {
            return String.format("%s%s%s", mContext.getString(R.string.about_), (int) (time * 60) + "", mContext.getString(R.string.second));
        }
    }

    private boolean judgeGasLimit(String gasLimit) {
        int limitSize = Integer.parseInt(gasLimit);
        if (limitSize < size || limitSize > size * 10) {
            Toast.makeText(mContext, String.format(mContext.getString(R.string.gas_limit_tip), size, (size * 10)), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean judgeGasPrice(String gasPrice) {
        double feeRate1 = Double.parseDouble(gasPrice);
        if (feeRate1 < feeRateMin || feeRate1 > feeRateMax) {
            Toast.makeText(getContext(), String.format(mContext.getString(R.string.gas_price_tip), String.valueOf(feeRateMin), String.valueOf(feeRateMax)), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
