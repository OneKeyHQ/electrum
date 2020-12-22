package org.haobtc.onekey.ui.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.ButtonRequestConfirmedEvent;
import org.haobtc.onekey.event.NextFragmentEvent;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.CURRENT_CURRENCY_GRAPHIC_SYMBOL;

/**
 * @author liyan
 * @date 12/11/20
 */

public class TransactionConfirmDialog extends BaseDialogFragment {

    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.text_tx_amount)
    TextView textTxAmount;
    @BindView(R.id.text_send_address)
    TextView textSendAddress;
    @BindView(R.id.text_send_name)
    TextView textSendName;
    @BindView(R.id.text_receive_address)
    TextView textReceiveAddress;
    @BindView(R.id.text_tx_fee)
    TextView textTxFee;
    @BindView(R.id.btn_confirm_pay)
    Button btnConfirmPay;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.send_confirm_dialog;
    }

    @Override
    public void init() {
        SharedPreferences preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        Bundle bundle = getArguments();
        int type = bundle.getInt(Constant.WALLET_TYPE);
        String sender = bundle.getString(Constant.TRANSACTION_SENDER);
        String receiver = bundle.getString(Constant.TRANSACTION_RECEIVER);
        String fee = bundle.getString(Constant.TRANSACTION_FEE);
        String amount = bundle.getString(Constant.TRANSACTION_AMOUNT);
        String name = bundle.getString(Constant.WALLET_LABEL);
        textSendName.setText(name);
        textSendAddress.setText(sender);
        textReceiveAddress.setText(receiver);
        String amounts = amount.replace("-", "");
        if (amounts.contains("(")) {
            String btcAmount = amounts.substring(0, amounts.indexOf("("));
            textTxAmount.setText(btcAmount);
        } else {
            textTxAmount.setText(amounts);
        }
        if (fee.contains("(")) {
            String feeAmount = fee.substring(0, fee.indexOf("("));
            String strCny = fee.substring(fee.indexOf("(") + 1, fee.indexOf(")"));
            String cny = strCny.substring(0, strCny.indexOf(" "));
            textTxFee.setText(String.format("%s ≈ %s %s", feeAmount, preferences.getString(CURRENT_CURRENCY_GRAPHIC_SYMBOL, "¥"), cny));
        } else {
            textTxFee.setText(fee);
        }
        switch (type) {
            case Constant.WALLET_TYPE_SOFTWARE:
                btnConfirmPay.setEnabled(true);
                break;
            case Constant.WALLET_TYPE_HARDWARE_PERSONAL:
                btnConfirmPay.setText(R.string.cold_device_confirm);
                break;
            case Constant.WALLET_TYPE_HARDWARE_MULTI:
                break;
        }
    }

    @OnClick({R.id.img_cancel, R.id.btn_confirm_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_cancel:
                dismiss();
                break;
            case R.id.btn_confirm_pay:
                EventBus.getDefault().post(new ButtonRequestConfirmedEvent());
                dismiss();
                break;
        }
    }

    public Button getBtnConfirmPay() {
        return btnConfirmPay;
    }
}
