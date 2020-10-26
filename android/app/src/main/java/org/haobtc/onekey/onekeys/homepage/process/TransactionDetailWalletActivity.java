package org.haobtc.onekey.onekeys.homepage.process;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.OnekeyTxListAdapter;
import org.haobtc.onekey.bean.MaintrsactionlistEvent;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransactionDetailWalletActivity extends BaseActivity {

    @BindView(R.id.text_wallet_amount)
    TextView textWalletAmount;
    @BindView(R.id.text_wallet_dollar)
    TextView textWalletDollar;
    @BindView(R.id.text_All)
    TextView textAll;
    @BindView(R.id.text_into)
    TextView textInto;
    @BindView(R.id.text_output)
    TextView textOutput;
    @BindView(R.id.recl_transaction_list)
    RecyclerView reclTransactionList;
    @BindView(R.id.tet_None)
    TextView tetNone;
    private ArrayList<MaintrsactionlistEvent> listBeans;
    private OnekeyTxListAdapter onekeyTxListAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_detail_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        String walletBalance = getIntent().getStringExtra("walletBalance");
        String walletDollar = getIntent().getStringExtra("walletDollar");
        textWalletAmount.setText(walletBalance);
        textWalletDollar.setText(walletDollar);

    }

    @Override
    public void initData() {
        listBeans = new ArrayList<>();
        onekeyTxListAdapter = new OnekeyTxListAdapter(listBeans);
        reclTransactionList.setAdapter(onekeyTxListAdapter);
        //get transaction list
        getTxList("all");

    }

    private void getTxList(String status) {
        PyObject getHistoryTx = null;
        try {
            //get transaction json
            if ("all".equals(status)) {
                getHistoryTx = Daemon.commands.callAttr("get_all_tx_list");
            } else {
                getHistoryTx = Daemon.commands.callAttr("get_all_tx_list", status);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mToast(getString(R.string.switch_server));
            reclTransactionList.setVisibility(View.GONE);
            tetNone.setVisibility(View.VISIBLE);
            return;

        }
        if (getHistoryTx.toString().length() > 2) {
            reclTransactionList.setVisibility(View.VISIBLE);
            tetNone.setVisibility(View.GONE);
            try {
                JSONArray jsonArray = new JSONArray(getHistoryTx.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    MaintrsactionlistEvent maintrsactionlistEvent = new MaintrsactionlistEvent();
                    String type = jsonObject.getString("type");
                    String txHash = jsonObject.getString("tx_hash");
                    String amount = jsonObject.getString("amount");
                    //false ->get   true ->push
                    boolean isMine = jsonObject.getBoolean("is_mine");
                    String date = jsonObject.getString("date");
                    String txStatus = jsonObject.getString("tx_status");
                    if ("history".equals(type)) {
                        String confirmations = jsonObject.getString("confirmations");
                        //add attribute
                        maintrsactionlistEvent.setTxHash(txHash);
                        maintrsactionlistEvent.setDate(date);
                        maintrsactionlistEvent.setAmount(amount);
                        maintrsactionlistEvent.setMine(isMine);
                        maintrsactionlistEvent.setConfirmations(confirmations);
                        maintrsactionlistEvent.setType(type);
                        maintrsactionlistEvent.setTxStatus(txStatus);
                        listBeans.add(maintrsactionlistEvent);
                    }
                    onekeyTxListAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            reclTransactionList.setVisibility(View.GONE);
            tetNone.setVisibility(View.VISIBLE);
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @OnClick({R.id.img_back, R.id.text_All, R.id.text_into, R.id.text_output, R.id.btn_forward, R.id.btn_collect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_All:
                listBeans.clear();
                textAll.setBackground(getDrawable(R.drawable.back_white_6));
                textInto.setBackgroundColor(getColor(R.color.t_white));
                textOutput.setBackgroundColor(getColor(R.color.t_white));
                //get transaction list
                getTxList("all");
                break;
            case R.id.text_into:
                listBeans.clear();
                textAll.setBackgroundColor(getColor(R.color.t_white));
                textInto.setBackground(getDrawable(R.drawable.back_white_6));
                textOutput.setBackgroundColor(getColor(R.color.t_white));
                //get transaction list
                getTxList("receive");
                break;
            case R.id.text_output:
                listBeans.clear();
                textAll.setBackgroundColor(getColor(R.color.t_white));
                textInto.setBackgroundColor(getColor(R.color.t_white));
                textOutput.setBackground(getDrawable(R.drawable.back_white_6));
                //get transaction list
                getTxList("send");
                break;
            case R.id.btn_forward:
                Intent intent2 = new Intent(TransactionDetailWalletActivity.this, SendHdActivity.class);
                intent2.putExtra("sendNum", textWalletAmount.getText().toString());
                startActivity(intent2);
                break;
            case R.id.btn_collect:
                mIntent(ReceiveHDActivity.class);
                break;
        }
    }

}