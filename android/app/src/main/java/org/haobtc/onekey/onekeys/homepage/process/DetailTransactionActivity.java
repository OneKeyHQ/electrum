package org.haobtc.onekey.onekeys.homepage.process;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.bean.GetnewcreatTrsactionListBean;
import org.haobtc.onekey.utils.Daemon;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailTransactionActivity extends BaseActivity {

    @BindView(R.id.text_tx_amount)
    TextView textTxAmount;
    @BindView(R.id.text_send_address)
    TextView textSendAddress;
    @BindView(R.id.text_receive_address)
    TextView textReceiveAddress;
    @BindView(R.id.text_confirm_num)
    TextView textConfirmNum;
    @BindView(R.id.text_tx_time)
    TextView textTxTime;
    @BindView(R.id.text_fee)
    TextView textFee;
    @BindView(R.id.text_remarks)
    TextView textRemarks;
    @BindView(R.id.text_block_high)
    TextView textBlockHigh;
    @BindView(R.id.text_tx_num)
    TextView textTxNum;
    @BindView(R.id.lin_choose_fee)
    LinearLayout linChooseFee;
    private String tx;
    private String hashDetail;
    private String txTime;

    @Override
    public int getLayoutId() {
        return R.layout.activity_detail_transaction;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        tx = getIntent().getStringExtra("txDetail");
        hashDetail = getIntent().getStringExtra("hashDetail");
        txTime = getIntent().getStringExtra("txTime");

    }

    @Override
    public void initData() {
        String detailType = getIntent().getStringExtra("detailType");
        if ("homeScanDetail".equals(detailType)) {
            String nowDatetime = mGetNowDatetime();
            textTxTime.setText(nowDatetime);
            String scanDetail = getIntent().getStringExtra("scanDetail");
            jsonDetailData(scanDetail);
        } else {
            getTxDetail();
        }
    }

    private void getTxDetail() {
        PyObject infoFromRaw = null;
        if (!TextUtils.isEmpty(tx)) {
            try {
                infoFromRaw = Daemon.commands.callAttr("get_tx_info_from_raw", tx);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (!TextUtils.isEmpty(infoFromRaw.toString())) {
                String nowDatetime = mGetNowDatetime();
                textTxTime.setText(nowDatetime);
                jsonDetailData(infoFromRaw.toString());
            }
        } else {
            try {
                infoFromRaw = Daemon.commands.callAttr("get_tx_info", hashDetail);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (!TextUtils.isEmpty(infoFromRaw.toString())) {
                textTxTime.setText(txTime);
                jsonDetailData(infoFromRaw.toString());
            }
        }
    }

    private void jsonDetailData(String detailMsg) {
        Log.i("detailMsg====", "jsonDetailData--: " + detailMsg);
        Gson gson = new Gson();
        GetnewcreatTrsactionListBean listBean = gson.fromJson(detailMsg, GetnewcreatTrsactionListBean.class);
        String amount = listBean.getAmount();
        if (listBean.getInputAddr() != null && listBean.getInputAddr().size() != 0) {
            String inputAddr = listBean.getInputAddr().get(0).getAddr();
            textSendAddress.setText(inputAddr);
        } else {
            linChooseFee.setVisibility(View.GONE);
        }
        if (listBean.getOutputAddr() != null && listBean.getOutputAddr().size() != 0) {
            String outputAddr = listBean.getOutputAddr().get(0).getAddr();
            textReceiveAddress.setText(outputAddr);
        }
        String txStatus = listBean.getTxStatus();
        String txid = listBean.getTxid();
        String fee = listBean.getFee();
        String description = listBean.getDescription();
        if (amount.contains(" (")) {
            String txAmount = amount.substring(0, amount.indexOf(" ("));
            textTxAmount.setText(txAmount);
        } else {
            textTxAmount.setText(amount);
        }


        if (txStatus.contains("confirmations")) {
            String confirms = txStatus.substring(0, txStatus.indexOf(" "));
            textConfirmNum.setText(confirms);
        } else {
            textConfirmNum.setText(txStatus);
        }
        textTxNum.setText(txid);
        String txFee = fee.substring(0, fee.indexOf(" ("));
        textFee.setText(txFee);
        if (!TextUtils.isEmpty(description)) {
            textRemarks.setText(description);
        } else {
            textRemarks.setText(getString(R.string.no));
        }
        String high = String.valueOf(listBean.getHeight());
        textBlockHigh.setText(high);
    }

    @OnClick({R.id.img_back, R.id.img_send_copy, R.id.img_receive_copy, R.id.text_block_high, R.id.text_tx_num})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_send_copy:
                //copy text
                ClipboardManager cm1 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // The text is placed on the system clipboard.
                Objects.requireNonNull(cm1, "ClipboardManager not available").setPrimaryClip(ClipData.newPlainText(null, textSendAddress.getText()));
                Toast.makeText(DetailTransactionActivity.this, R.string.copysuccess, Toast.LENGTH_LONG).show();
                break;
            case R.id.img_receive_copy:
                //copy text
                ClipboardManager cm2 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // The text is placed on the system clipboard.
                Objects.requireNonNull(cm2, "ClipboardManager not available").setPrimaryClip(ClipData.newPlainText(null, textReceiveAddress.getText()));
                Toast.makeText(DetailTransactionActivity.this, R.string.copysuccess, Toast.LENGTH_LONG).show();
                break;
            case R.id.text_block_high:
                //copy text
                ClipboardManager cm3 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // The text is placed on the system clipboard.
                Objects.requireNonNull(cm3, "ClipboardManager not available").setPrimaryClip(ClipData.newPlainText(null, textBlockHigh.getText()));
                Toast.makeText(DetailTransactionActivity.this, R.string.copysuccess, Toast.LENGTH_LONG).show();
                break;
            case R.id.text_tx_num:
                //copy text
                ClipboardManager cm4 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // The text is placed on the system clipboard.
                Objects.requireNonNull(cm4, "ClipboardManager not available").setPrimaryClip(ClipData.newPlainText(null, textTxNum.getText()));
                Toast.makeText(DetailTransactionActivity.this, R.string.copysuccess, Toast.LENGTH_LONG).show();
                break;
        }
    }

}