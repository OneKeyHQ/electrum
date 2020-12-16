package org.haobtc.onekey.onekeys.dialog.recovery;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.RecoveryWalletAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.bean.RecoveryWalletBean;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.event.WalletAddressEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.utils.Daemon;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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
    private String name;

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
            RecoveryWalletBean recoveryWalletBean = new Gson().fromJson(recoveryData, RecoveryWalletBean.class);
            List<RecoveryWalletBean.WalletInfoBean> walletInfo = recoveryWalletBean.getWalletInfo();
            name = walletInfo.get(0).getName();
            List<RecoveryWalletBean.DerivedInfoBean> derivedInfo = recoveryWalletBean.getDerivedInfo();
            for (int i = 0; i < derivedInfo.size(); i++) {
                WalletAddressEvent walletAddressEvent = new WalletAddressEvent();
                walletAddressEvent.setAddress(derivedInfo.get(i).getLabel());
                walletAddressEvent.setBalance(derivedInfo.get(i).getBlance());
                walletAddressEvent.setKey(derivedInfo.get(i).getName());
                walletList.add(walletAddressEvent);
            }
            recoveryWalletAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SingleClick
    @OnClick({R.id.btn_recovery})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_recovery:
                listDates.clear();
                Map<Integer, Boolean> map = recoveryWalletAdapter.getMap();
                for (int i = 0; i < walletList.size(); i++) {
                    if (map.get(i)) {
                        listDates.add(walletList.get(i).getKey());
                    }
                }
                String recoveryName = new Gson().toJson(listDates);
                Log.i("recoveryNamejxm", "onViewClicked: " + recoveryName);
                if (listDates != null) {
                    try {
                        Daemon.commands.callAttr("recovery_confirmed", recoveryName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mToast(e.getMessage());
                        return;
                    }
                    EventBus.getDefault().post(new CreateSuccessEvent(name));
                    mIntent(HomeOneKeyActivity.class);
                } else {
                    EventBus.getDefault().post(new CreateSuccessEvent(name));
                    mIntent(HomeOneKeyActivity.class);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}