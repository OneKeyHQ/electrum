package org.haobtc.onekey.onekeys.homepage.process;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;
import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.OnekeyTxListAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.MaintrsactionlistEvent;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.BleConnectedEvent;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.viewmodel.AppWalletViewModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.haobtc.onekey.constant.Constant.WALLET_BALANCE;

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
    private String hdWalletName;
    private String walletBalance;
    private String bleMac;
    private int currentAction;
    private Disposable mLoadTxListDisposable;
    private AppWalletViewModel mAppWalletViewModel;
    private SharedPreferences preferences;

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_detail_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        mAppWalletViewModel = getApplicationViewModel(AppWalletViewModel.class);
        hdWalletName = getIntent().getStringExtra("hdWalletName");
        bleMac = getIntent().getStringExtra(Constant.BLE_MAC);
        listenerViewModel();
    }

    private void listenerViewModel() {
        mAppWalletViewModel.currentWalletBalance.observe(this, balance -> {
            walletBalance = balance.getBalance();
            textWalletAmount.setText(String.format("%s%s", balance.getBalance(), balance.getUnit()));
        });
        mAppWalletViewModel.currentWalletFiatBalance.observe(this, balance -> {
            textWalletDollar.setText(String.format("≈ %s %s", balance.getSymbol(), Strings.isNullOrEmpty(balance.getBalance()) ? getString(R.string.zero) : balance.getBalance()));
        });
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
        if (mLoadTxListDisposable != null && !mLoadTxListDisposable.isDisposed()) {
            mLoadTxListDisposable.dispose();
        }
        mLoadTxListDisposable = Observable
                .create((ObservableOnSubscribe<PyObject>) emitter -> {
                    try {
                        PyObject getHistoryTx = null;
                        //get transaction json
                        if ("all".equals(status)) {
                            getHistoryTx = Daemon.commands.callAttr("get_all_tx_list");
                        } else {
                            getHistoryTx = Daemon.commands.callAttr("get_all_tx_list", status);
                        }
                        emitter.onNext(getHistoryTx);
                        emitter.onComplete();
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(historyTx -> {
                    if (historyTx.toString().length() > 2) {
                        reclTransactionList.setVisibility(View.VISIBLE);
                        tetNone.setVisibility(View.GONE);
                    } else {
                        reclTransactionList.setVisibility(View.GONE);
                        tetNone.setVisibility(View.VISIBLE);
                    }
                    return historyTx;
                })
                .observeOn(Schedulers.io())
                .map(historyTx -> {
                    try {
                        JSONArray jsonArray = new JSONArray(historyTx.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            MaintrsactionlistEvent maintrsactionlistEvent = new MaintrsactionlistEvent();
                            String type = jsonObject.getString("type");
                            String txHash = jsonObject.getString("tx_hash");
                            String address = jsonObject.getString("address");
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
                                maintrsactionlistEvent.setAddress(address);
                                maintrsactionlistEvent.setConfirmations(confirmations);
                                maintrsactionlistEvent.setType(type);
                                maintrsactionlistEvent.setTxStatus(txStatus);
                                listBeans.add(maintrsactionlistEvent);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return historyTx;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(s -> showProgress())
                .doFinally(this::dismissProgress)
                .subscribe(o -> {
                    if (null != onekeyTxListAdapter) {
                        onekeyTxListAdapter.notifyDataSetChanged();
                        onekeyTxListAdapter.setOnItemClickListener((adapter, view, position) -> {
                            Intent intent = new Intent(TransactionDetailWalletActivity.this, DetailTransactionActivity.class);
                            intent.putExtra("hashDetail", listBeans.get(position).getTxHash());
                            intent.putExtra("txTime", listBeans.get(position).getDate());
                            startActivity(intent);
                        });
                    }
                }, e -> {
                    e.printStackTrace();
                    String message = e.getMessage();
                    if (message != null && !message.contains("SQLite")) {
                        mToast(e.getMessage());
                    }
                    if (null != reclTransactionList) {
                        reclTransactionList.setVisibility(View.GONE);
                    }
                    if (null != tetNone) {
                        tetNone.setVisibility(View.VISIBLE);
                    }
                });
    }

    @SingleClick
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
            case R.id.btn_collect:
                deal(view.getId());
                break;
        }
    }
    /**
     * 统一处理硬件连接
     */
    private void deal(@IdRes int id) {
        if (!Strings.isNullOrEmpty(bleMac)) {
            currentAction = id;
            if (Strings.isNullOrEmpty(bleMac)) {
                Toast.makeText(this, "未发现设备信息", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent2 = new Intent(this, SearchDevicesActivity.class);
                intent2.putExtra(org.haobtc.onekey.constant.Constant.SEARCH_DEVICE_MODE, org.haobtc.onekey.constant.Constant.SearchDeviceMode.MODE_PREPARE);
                startActivity(intent2);
                BleManager.getInstance(this).connDevByMac(bleMac);
            }
            return;
        }
        toNext(id);

    }
    /**
     * 处理具体业务
     */
    private void toNext(int id) {
        switch (id) {
            case R.id.btn_forward:
                Intent intent2 = new Intent(this, SendHdActivity.class);
                intent2.putExtra(WALLET_BALANCE, walletBalance);
                intent2.putExtra("hdWalletName", hdWalletName);
                startActivity(intent2);
                break;
            case R.id.btn_collect:
                Intent intent3 = new Intent(this, ReceiveHDActivity.class);
                if (!Strings.isNullOrEmpty(bleMac)) {
                    intent3.putExtra(org.haobtc.onekey.constant.Constant.WALLET_TYPE, org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE_PERSONAL);
                }
                startActivity(intent3);
                break;
            default:
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnected(BleConnectedEvent event) {
        toNext(currentAction);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mLoadTxListDisposable != null && !mLoadTxListDisposable.isDisposed()) {
            mLoadTxListDisposable.dispose();
        }
    }
}