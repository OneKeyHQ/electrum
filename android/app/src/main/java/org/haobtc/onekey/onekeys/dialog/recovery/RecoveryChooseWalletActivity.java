package org.haobtc.onekey.onekeys.dialog.recovery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.RecoveryWalletAdapter;
import org.haobtc.onekey.event.WalletAddressEvent;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecoveryChooseWalletActivity extends BaseActivity {

    @BindView(R.id.recl_wallet_list)
    RecyclerView reclWalletList;
    private ArrayList<WalletAddressEvent> walletList;
    private RecoveryWalletAdapter recoveryWalletAdapter;
    private ArrayList<String> listDates;
    private String recoveryData;
    private SharedPreferences.Editor edit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recovery_choose_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        recoveryData = getIntent().getStringExtra("recoveryData");
        Log.i("pyObjectjxmjmm", "getRecoveryWallet: " + recoveryData);
    }

    @Override
    public void initData() {
        //choose wallet data list
        listDates = new ArrayList<>();
        reclWalletList.setNestedScrollingEnabled(false);
        walletList = new ArrayList<>();
        recoveryWalletAdapter = new RecoveryWalletAdapter(RecoveryChooseWalletActivity.this, walletList);
        reclWalletList.setAdapter(recoveryWalletAdapter);
        //get recovery wallet
        getRecoveryWallet();

    }

    private void getRecoveryWallet() {
        try {
            JSONArray jsonArray = new JSONArray(recoveryData);
            for (int i = 0; i < jsonArray.length(); i++) {
                WalletAddressEvent walletAddressEvent = new WalletAddressEvent();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String balance = jsonObject.getString("balance");
                walletAddressEvent.setAddress(name);
                walletAddressEvent.setBalance(balance);
                walletList.add(walletAddressEvent);
            }
            recoveryWalletAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.img_back, R.id.btn_recovery})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_recovery:
                listDates.clear();
                Map<Integer, Boolean> map = recoveryWalletAdapter.getMap();
                for (int i = 0; i < walletList.size(); i++) {
                    if (map.get(i)) {
                        listDates.add(walletList.get(i).getAddress());
                    }
                }
                String recoveryName = new Gson().toJson(listDates);
                Log.i("recoveryNamejxm", "onViewClicked: " + recoveryName);
                try {
                    Daemon.commands.callAttr("recovery_confirmed", recoveryName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                edit.putBoolean("isHaveWallet", true);
                edit.putString("loadWalletName", "BTC-1");
                edit.apply();
                mIntent(HomeOnekeyActivity.class);
                break;
        }
    }
}