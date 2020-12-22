package org.haobtc.onekey.ui.dialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CurrentFeeDetails;
import org.haobtc.onekey.bean.CustomFeeInfo;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.CustomizeFeeRateEvent;
import org.haobtc.onekey.event.GetFeeEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static android.content.Context.MODE_PRIVATE;
import static org.haobtc.onekey.constant.Constant.CURRENT_CURRENCY_GRAPHIC_SYMBOL;

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
    private SharedPreferences preferences;
    private int time;
    private String fee;
    private String fiat;

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
        preferences = getActivity().getSharedPreferences("Preferences", MODE_PRIVATE);
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
                EventBus.getDefault().post(new CustomizeFeeRateEvent(editFeeByte.getText().toString(), fee, fiat, String.valueOf(time)));
                dismiss();
                break;
        }
    }

    @OnTextChanged(value = R.id.edit_fee_rate, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(CharSequence text) {
        String feeRate = editFeeByte.getText().toString();
        if (!Strings.isNullOrEmpty(feeRate) && Double.parseDouble(feeRate) > 0) {
            btnNext.setEnabled(true);
            PyResponse<String> customFeeInfo = PyEnv.getCustomFeeInfo(editFeeByte.getText().toString());
            String errors = customFeeInfo.getErrors();
            if (Strings.isNullOrEmpty(errors)) {
                CustomFeeInfo fromDate = CustomFeeInfo.objectFromDate(customFeeInfo.getResult());
                CustomFeeInfo.CustomerBean customer = fromDate.getCustomer();
                time = customer.getTime();
                fiat = customer.getFiat();
                fee = customer.getFee();
                textTime.setText(String.format("%s%s%s", getString(R.string.about_), time, getString(R.string.minute)));
                textFeeInBtc.setText(String.format(Locale.ENGLISH, "%s %s", fee, preferences.getString("base_unit", "")));
                textSize.setText(String.valueOf(customer.getSize()));
//                textFeeInCash.setText(String.format(Locale.ENGLISH, "%s %s", preferences.getString(CURRENT_CURRENCY_GRAPHIC_SYMBOL, "¥"), customer.getFiat()));
            } else {
                Toast.makeText(getActivity(), errors, Toast.LENGTH_SHORT).show();
            }
            double feeRate1 = Double.parseDouble(feeRate);
            if (feeRate1 < feeRateMin) {
                Toast.makeText(getContext(), R.string.fee_rate_too_small, Toast.LENGTH_SHORT).show();
                return;
            } else if (feeRate1 > feeRateMax) {
                Toast.makeText(getContext(), R.string.fee_rate_too_big, Toast.LENGTH_SHORT).show();
                editFeeByte.setText(String.format(Locale.ENGLISH, "%s", feeRateMax));
                return;
            }
            EventBus.getDefault().post(new GetFeeEvent(feeRate));
        } else {
            btnNext.setEnabled(false);
            textTime.setText("-------");
            textFeeInBtc.setText("-------");
        }
    }
}
