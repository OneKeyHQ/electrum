package org.haobtc.onekey.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.service.NfcNotifyHelper;
import org.haobtc.onekey.adapter.HardwareAdapter;
import org.haobtc.onekey.bean.GetnewcreatTrsactionListBean;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.event.SendMoreAddressEvent;
import org.haobtc.onekey.event.SendSignBroadcastEvent;
import org.haobtc.onekey.event.SignFailedEvent;
import org.haobtc.onekey.event.SignResultEvent;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.isNFC;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.nfc;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.protocol;

public class ConfirmOnHardware extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "org.haobtc.onekey.activities.ConfirmOnHardware";
    @BindView(R.id.linBitcoin)
    LinearLayout linBitcoin;
    @BindView(R.id.testConfirmMsg)
    TextView testConfirmMsg;
    @BindView(R.id.confirm_layout)
    LinearLayout confirmLayout;
    @BindView(R.id.confirm_on_hardware)
    Button confirmOnHardware;
    private Dialog dialog;
    private View view;
    private ImageView imageViewCancel;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tet_payAddress)
    TextView tetPayAddress;
    @BindView(R.id.tet_feeNum)
    TextView tetFeeNum;
    @BindView(R.id.recl_Msg)
    RecyclerView reclMsg;
    //    private TextView signSuccess;
    private String txHash;
    private boolean needTouch;

    @Override
    public int getLayoutId() {
        return R.layout.confirm_on_hardware;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        reclMsg.setNestedScrollingEnabled(false);
        findViewById(R.id.confirm_on_hardware).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
        EventBus.getDefault().register(this);
        if (!isNFC) {
            confirmLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void initData() {
        ArrayList<SendMoreAddressEvent> addressEventList = new ArrayList<>();
        Bundle bundle = getIntent().getBundleExtra("outputs");
        if (bundle != null) {
            String payAddress = bundle.getString("pay_address");
            String fee = bundle.getString("fee");
            ArrayList<GetnewcreatTrsactionListBean.OutputAddrBean> outputs = (ArrayList<GetnewcreatTrsactionListBean.OutputAddrBean>) bundle.getSerializable("output");
            if (outputs != null) {
                for (GetnewcreatTrsactionListBean.OutputAddrBean output : outputs) {
                    SendMoreAddressEvent sendMoreAddressEvent = new SendMoreAddressEvent();
                    String addr = output.getAddr();
                    String amount = output.getAmount();
                    Boolean isChange = output.getIs_change();
                    sendMoreAddressEvent.setInputAddress(addr);
                    sendMoreAddressEvent.setInputAmount(amount);
                    sendMoreAddressEvent.setIs_change(isChange);
                    addressEventList.add(sendMoreAddressEvent);
                }
                //    Log.i("addressEventList", "-----: " + addressEventList);
            }
            tetPayAddress.setText(payAddress);
            tetFeeNum.setText(fee);
            HardwareAdapter hardwareAdapter = new HardwareAdapter(addressEventList);
            reclMsg.setAdapter(hardwareAdapter);
        } else {
            linBitcoin.setVisibility(View.GONE);
            testConfirmMsg.setText(getString(R.string.confirm_hardware_msg));
        }
    }

    private void showPopupSignFailed() {
        view = LayoutInflater.from(this).inflate(R.layout.signature_fail_popupwindow, null);
        imageViewCancel = view.findViewById(R.id.cancel_sign_fail);
        dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(view);
        imageViewCancel.setOnClickListener(this);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AnimBottom);
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignSuccessful(SignResultEvent resultEvent) {

//        Drawable drawableStart = getDrawable(R.drawable.chenggong);
//        Objects.requireNonNull(drawableStart).setBounds(0, 0, drawableStart.getMinimumWidth(), drawableStart.getMinimumHeight());
//        signSuccess.setCompoundDrawables(drawableStart, null, null, null);
        String signedRaw = resultEvent.getSignedRaw();
        if (!TextUtils.isEmpty(signedRaw)) {
            EventBus.getDefault().post(new SecondEvent("finish"));
            Intent intent1 = new Intent(this, TransactionDetailsActivity.class);
            intent1.putExtra("signed_raw_tx", signedRaw);
            intent1.putExtra("is_mine", true);
            startActivity(intent1);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onSignSuccessful(SendSignBroadcastEvent resultEvent) {
        EventBus.getDefault().removeStickyEvent(SendSignBroadcastEvent.class);
        String signedTx = resultEvent.getSignTx();
        if (!TextUtils.isEmpty(signedTx)) {
            try {
                Gson gson = new Gson();
                GetnewcreatTrsactionListBean getnewcreatTrsactionListBean = gson.fromJson(signedTx, GetnewcreatTrsactionListBean.class);
                String tx = getnewcreatTrsactionListBean.getTx();
                txHash = getnewcreatTrsactionListBean.getTxid();
                //  Log.i("onSignSuccessful", "onSignSuccessful:++ " + tx);
                Daemon.commands.callAttr("broadcast_tx", tx);
            } catch (Exception e) {
                String message = e.getMessage();
                if (message.contains(".")) {
                    if (message.endsWith(".")) {
                        message = message.substring(0, message.length() - 1);
                        mToast(message);
                    }
                    mToast(message.substring(message.lastIndexOf(".") + 1));
                }
                e.printStackTrace();
            }
            EventBus.getDefault().post(new SecondEvent("finish"));
            EventBus.getDefault().post(new FirstEvent("22"));
            EventBus.getDefault().postSticky(new SecondEvent("ActivateFinish"));
            Intent intent1 = new Intent(this, TransactionDetailsActivity.class);
            intent1.putExtra("listType", "history");
            intent1.putExtra("keyValue", "B");
            intent1.putExtra("tx_hash", txHash);
            intent1.putExtra("is_mine", true);
            intent1.putExtra("unConfirmStatus", "broadcast_complete");
            startActivity(intent1);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignFailed(SignFailedEvent failedEvent) {
        if ("BaseException: Sign failed, May be BiXin cannot pair with your device".equals(failedEvent.getException().getMessage())) {
            mToast(getString(R.string.sign_failed_device));
        }
        showPopupSignFailed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onButtonRequest(ButtonRequestEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (isNFC) {
            needTouch = true;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(FinishEvent event) {
        if (hasWindowFocus()) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_again:
            case R.id.img_back:
                nfc.put("IS_CANCEL", true);
                protocol.callAttr("notify");
                finish();
                break;
            case R.id.confirm_on_hardware:
                if (isNFC && needTouch) {
                    Intent intent = new Intent(this, NfcNotifyHelper.class);
                    // only used to fix exception with samsung s10
                    intent.putExtra("is_button_request", true);
                    startActivity(intent);
                    needTouch = false;
                }
                break;
            default:
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
