package org.haobtc.onekey.activities.personalwallet.hidewallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaquo.python.PyObject;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.ReceivedPageActivity;
import org.haobtc.onekey.activities.SendOne2OneMainPageActivity;
import org.haobtc.onekey.activities.TransactionDetailsActivity;
import org.haobtc.onekey.activities.TransactionRecordsActivity;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.personalwallet.WalletDetailsActivity;
import org.haobtc.onekey.activities.sign.SignActivity;
import org.haobtc.onekey.adapter.MaindowndatalistAdapetr;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.MainNewWalletBean;
import org.haobtc.onekey.bean.MaintrsactionlistEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.MyDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckHideWalletActivity extends BaseActivity implements OnRefreshListener {

    @BindView(R.id.wallet_card_tv4)
    TextView walletCardTv4;
    @BindView(R.id.tet_fiat)
    TextView tetFiat;
    @BindView(R.id.tet_Cny)
    TextView tetCny;
    @BindView(R.id.recy_data)
    RecyclerView recyData;
    @BindView(R.id.smart_RefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tet_None)
    TextView tetNone;
    @BindView(R.id.wallet_card_tv3)
    TextView walletCard;
    private PyObject selectWallet;
    private MaindowndatalistAdapetr trsactionlistAdapter;
    private ArrayList<MaintrsactionlistEvent> maintrsactionlistEvents;
    private MyDialog myDialog;
    private JSONArray jsonArray;
    private boolean isMine;
    private String date;
    private SharedPreferences.Editor edit;
    private SharedPreferences preferences;

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_hide_wallet;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        //Eventbus register
        EventBus.getDefault().register(this);
        myDialog = MyDialog.showDialog(CheckHideWalletActivity.this);
        Intent intent = getIntent();
        inits();
    }

    private void inits() {
        //get wallet unit
        String baseUnit = preferences.getString("base_unit", "");
        walletCard.setText(String.format("%s（%s）", getString(R.string.balance), baseUnit));
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void initData() {
        maintrsactionlistEvents = new ArrayList<>();
        //Binder Adapter
        trsactionlistAdapter = new MaindowndatalistAdapetr(maintrsactionlistEvents);
        recyData.setAdapter(trsactionlistAdapter);
        getWalletMsg();
        //trsaction list data
        downMainListdata();
    }

    //get wallet message
    public void getWalletMsg() {
        try {
            selectWallet = Daemon.commands.callAttr("select_wallet", "隐藏钱包");
        } catch (Exception e) {
//            Log.i("select_wallet", "--------- " + e.getMessage());
            e.printStackTrace();
        }

        if (selectWallet != null) {
            String toString = selectWallet.toString();
//            Log.i("CheckHideWalletActivity", "select_wallet+++: " + toString);
            Gson gson = new Gson();
            MainNewWalletBean mainWheelBean = gson.fromJson(toString, MainNewWalletBean.class);
            String balanceC = mainWheelBean.getBalance();
            if (!TextUtils.isEmpty(balanceC)) {
                if (balanceC.contains("(")) {
                    String substring = balanceC.substring(0, balanceC.indexOf("("));
                    Log.e("substring", "substring: " + substring);
                    Log.e("substring", "balanceC: " + balanceC);
                    walletCardTv4.setText(substring);
                    String strCny = balanceC.substring(balanceC.indexOf("(") + 1, balanceC.indexOf(")"));
                    if (!TextUtils.isEmpty(strCny)) {
                        tetCny.setText(String.format("≈ %s", strCny));
                    }
                } else {
                    walletCardTv4.setText(balanceC);
                }
            }
        }
    }

    private void downMainListdata() {
        maintrsactionlistEvents.clear();
        trsactionlistAdapter.notifyDataSetChanged();
        PyObject getHistoryTx = null;
        try {
            //get transaction json
            getHistoryTx = Daemon.commands.callAttr("get_all_tx_list");
        } catch (Exception e) {
            e.printStackTrace();
            myDialog.dismiss();
            refreshLayout.finishRefresh();
            tetNone.setText(getString(R.string.no_records));
            tetNone.setVisibility(View.VISIBLE);
            recyData.setVisibility(View.GONE);
            Log.i("downMainListdata", "downMaina===: " + e.getMessage());
            return;
        }
        //get transaction list
        if (getHistoryTx != null) {
            tetNone.setVisibility(View.GONE);
            recyData.setVisibility(View.VISIBLE);
            String strHistory = getHistoryTx.toString();
            //Log.i("strHistory", "onPage----: " + strHistory);
            refreshLayout.finishRefresh();
            if (strHistory.length() == 2) {
                myDialog.dismiss();
                tetNone.setText(getString(R.string.no_records));
                tetNone.setVisibility(View.VISIBLE);
                recyData.setVisibility(View.GONE);
            } else {
                //show trsaction list
                showTrsactionlist(strHistory);
            }

        } else {
            myDialog.dismiss();
            refreshLayout.finishRefresh();
            tetNone.setText(getString(R.string.no_records));
            tetNone.setVisibility(View.VISIBLE);
            recyData.setVisibility(View.GONE);
        }

    }

    //show trsaction list
    private void showTrsactionlist(String strHistory) {
        maintrsactionlistEvents.clear();
        try {
            jsonArray = new JSONArray(strHistory);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MaintrsactionlistEvent maintrsactionlistEvent = new MaintrsactionlistEvent();
                String type = jsonObject.getString("type");
                String txHash = jsonObject.getString("tx_hash");
                String amount = jsonObject.getString("amount");
                //false ->get   true ->push
                isMine = jsonObject.getBoolean("is_mine");
                date = jsonObject.getString("date");
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
                    maintrsactionlistEvents.add(maintrsactionlistEvent);
                } else {

                    String txCreatTrsaction = jsonObject.getString("tx");
                    String invoiceId = jsonObject.getString("invoice_id");//delete use
                    //add attribute
                    maintrsactionlistEvent.setTxHash(txHash);
                    maintrsactionlistEvent.setDate(date);
                    maintrsactionlistEvent.setAmount(amount);
                    maintrsactionlistEvent.setMine(isMine);
                    maintrsactionlistEvent.setType(type);
                    maintrsactionlistEvent.setTxStatus(txStatus);
                    maintrsactionlistEvent.setInvoiceId(invoiceId);
                    maintrsactionlistEvents.add(maintrsactionlistEvent);
                }
            }
            myDialog.dismiss();
            trsactionlistAdapter.notifyDataSetChanged();
            trsactionlistAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                private String txHash1;
                private boolean status;

                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    String typeDele = maintrsactionlistEvents.get(position).getType();
                    switch (view.getId()) {
                        case R.id.lin_Item:
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(position);
                                txHash1 = jsonObject.getString("tx_hash");
                                isMine = jsonObject.getBoolean("is_mine");
                                date = jsonObject.getString("date");
                                Intent intent = new Intent(CheckHideWalletActivity.this, TransactionDetailsActivity.class);
                                intent.putExtra("hideWallet", "hideWallet");
                                intent.putExtra("keyValue", "B");
                                intent.putExtra("dataTime", date);
                                intent.putExtra("is_mine", isMine);
                                intent.putExtra("listTxStatus", maintrsactionlistEvents.get(position).getTxStatus());
                                intent.putExtra("tx_hash", txHash1);
                                intent.putExtra("strwalletType", "1-1");
                                intent.putExtra("listType", typeDele);
                                if ("tx".equals(typeDele)) {
                                    String txOnclick = jsonObject.getString("tx");
                                    intent.putExtra("txCreatTrsaction", txOnclick);
                                    startActivity(intent);
                                } else {
                                    startActivity(intent);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case R.id.txt_delete:
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(position);
                                txHash1 = jsonObject.getString("tx_hash");
                                PyObject getRemoveFlag = Daemon.commands.callAttr("get_remove_flag", txHash1);
                                status = getRemoveFlag.toBoolean();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (status) {
//                                String invoice_id = maintrsactionlistEvents.get(position).getInvoice_id();
                                try {
                                    Daemon.commands.callAttr("remove_local_tx", txHash1);
                                    maintrsactionlistEvents.remove(position);
                                    trsactionlistAdapter.notifyItemChanged(position);
                                    trsactionlistAdapter.notifyDataSetChanged();
                                    downMainListdata();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                mToast(getString(R.string.delete_unBroad));
                            }

                            break;
                        default:
                   throw new IllegalStateException("Unexpected value: " + view.getId());
                    }
                }
            });

        } catch (JSONException e) {
            Log.e("sndkjnskjn", "type++++: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.linear_send, R.id.linear_receive, R.id.linear_sign, R.id.conlay_back, R.id.textView_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.linear_send:
                edit.putString("wallet_type_to_sign", "1-1");
                edit.apply();
                Intent intent1 = new Intent(CheckHideWalletActivity.this, SendOne2OneMainPageActivity.class);
                intent1.putExtra("wallet_name", "隐藏钱包");
                intent1.putExtra("wallet_type", "1-1");
                intent1.putExtra("strNowBtc", walletCardTv4.getText().toString());
                intent1.putExtra("strNowCny", tetCny.getText().toString());
                intent1.putExtra("hideRefresh", "hideRefresh");
                startActivity(intent1);
                break;
            case R.id.linear_receive:
                Intent intent2 = new Intent(CheckHideWalletActivity.this, ReceivedPageActivity.class);
                intent2.putExtra("hideWalletReceive","hideWalletReceive");
                startActivity(intent2);
                break;
            case R.id.linear_sign:
                Intent intent3 = new Intent(CheckHideWalletActivity.this, SignActivity.class);
                intent3.putExtra("hide_phrass", "hideWallet");
                intent3.putExtra("personceType", "1-1");
                startActivity(intent3);
                break;
            case R.id.conlay_back:
                Intent intent = new Intent(CheckHideWalletActivity.this, WalletDetailsActivity.class);
                intent.putExtra("wallet_name", "隐藏钱包");
                startActivity(intent);
                break;
            case R.id.textView_more:
                Intent intent4 = new Intent(this, TransactionRecordsActivity.class);
                startActivity(intent4);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        Log.i("JXMmsgVote", "event: " + msgVote);
        if (!TextUtils.isEmpty(msgVote) || msgVote.length() != 2 && !"finish".equals(msgVote)) {
            //Rolling Wallet
            try {
                JSONObject jsonObject = new JSONObject(msgVote);
                if (msgVote.contains("balance")) {
                    String balance = jsonObject.getString("balance");
                    walletCardTv4.setText(balance);
                }
                if (msgVote.contains("fiat")) {
                    String fiat = jsonObject.getString("fiat");
                    tetCny.setText(fiat);
                }
                if (msgVote.contains("unconfirmed")) {
                    String unconfirmed = jsonObject.getString("unconfirmed");
                    tetFiat.setText(String.format("%s%s", unconfirmed, getString(R.string.unconfirm)));
                } else {
                    tetFiat.setText("");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //trsaction list data
            downMainListdata();

        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        maintrsactionlistEvents.clear();
        //trsaction list data
        downMainListdata();
        if (trsactionlistAdapter != null) {
            trsactionlistAdapter.notifyDataSetChanged();
        }
    }

}
