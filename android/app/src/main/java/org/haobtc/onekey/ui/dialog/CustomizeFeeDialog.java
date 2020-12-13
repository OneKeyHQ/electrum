package org.haobtc.onekey.ui.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.CustomizeFeeRateEvent;
import org.haobtc.onekey.event.GetFeeEvent;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * @author liyan
 * @date 12/11/20
 */

public class CustomizeFeeDialog extends BaseDialogFragment {

    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.edit_fee_rate)
    EditText editFeeByte;
    @BindView(R.id.text_time)
    TextView textTime;
    @BindView(R.id.text_size)
    TextView textSize;
    @BindView(R.id.text_fee_in_btc)
    TextView textFeeInBtc;
    @BindView(R.id.text_fee_in_cash)
    TextView textFeeInCash;
    @BindView(R.id.btn_next)
    Button btnNext;
    private int size;
    private double feeRateMin;
    private double feeRateMax;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.custom_fee;
    }

    @Override
    public void init() {
        Bundle bundle = getArguments();
        assert bundle != null;
        size = bundle.getInt(Constant.TAG_TX_SIZE, 0);
        feeRateMin = bundle.getDouble(Constant.CUSTOMIZE_FEE_RATE_MIN);
        feeRateMax = bundle.getDouble(Constant.CUSTOMIZE_FEE_RATE_MAX);
        textSize.setText(String.valueOf(size));
    }

    public TextView getTextFeeInBtc() {
        return textFeeInBtc;
    }

    public TextView getTextFeeInCash() {
        return textFeeInCash;
    }

    public TextView getTextTime() {
        return textTime;
    }

    @OnClick({R.id.img_cancel, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_cancel:
                dismiss();
                break;
            case R.id.btn_next:
                EventBus.getDefault().post(new CustomizeFeeRateEvent(editFeeByte.getText().toString()));
                dismiss();
                break;
        }
    }
    @OnTextChanged(value = R.id.edit_fee_rate, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(CharSequence text) {
        String feeRate = editFeeByte.getText().toString();
        if (!Strings.isNullOrEmpty(feeRate) && Double.parseDouble(feeRate) > 0) {
            double feeRate1 = Double.parseDouble(feeRate);
            if (feeRate1 < feeRateMin ) {
                Toast.makeText(getContext(), R.string.fee_rate_too_small, Toast.LENGTH_SHORT).show();
                return;
            } else if (feeRate1 > feeRateMax) {
                Toast.makeText(getContext(), R.string.fee_rate_too_big, Toast.LENGTH_SHORT).show();
                editFeeByte.setText(String.format(Locale.ENGLISH, "%s", feeRateMax));
                return;
            }
            EventBus.getDefault().post(new GetFeeEvent(feeRate));
        }
    }
}
