package org.haobtc.onekey.ui.dialog;

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
        if (amount.contains("(")) {
            String btcAmount = amount.substring(0, amount.indexOf("("));
            textTxAmount.setText(btcAmount);
        } else {
            textTxAmount.setText(amount);
        }

        textTxFee.setText(fee);
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
