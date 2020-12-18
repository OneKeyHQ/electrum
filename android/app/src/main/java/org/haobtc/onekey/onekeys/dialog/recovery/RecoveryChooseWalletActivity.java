package org.haobtc.onekey.onekeys.dialog.recovery;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.RecoveryWalletAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.BalanceInfo;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecoveryChooseWalletActivity extends BaseActivity {

    @BindView(R.id.recl_wallet_list)
    RecyclerView reclWalletList;
    @BindView(R.id.promote)
    TextView promote;
    @BindView(R.id.btn_recovery)
    Button btnRecovery;
    private ArrayList<BalanceInfo> walletList;
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
        walletList = (ArrayList<BalanceInfo>) getIntent().getSerializableExtra("recoveryData");
    }

    @Override
    public void initData() {
        //choose wallet data list
        listDates = new ArrayList<>();
        reclWalletList.setNestedScrollingEnabled(false);
        if (walletList.isEmpty()) {
            promote.setText(R.string.no_use_wallet);
            btnRecovery.setEnabled(true);
            btnRecovery.setText(R.string.back);
        } else {
            recoveryWalletAdapter = new RecoveryWalletAdapter(this, walletList);
            reclWalletList.setAdapter(recoveryWalletAdapter);
            recoveryWalletAdapter.notifyDataSetChanged();
        }
    }

    @SingleClick
    @OnClick({R.id.btn_recovery})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.btn_recovery) {
            if (btnRecovery.getText().equals(getString(R.string.no_use_wallet))) {
                finish();
            } else {
                listDates.clear();
                Map<Integer, Boolean> map = recoveryWalletAdapter.getMap();
                for (int i = 0; i < walletList.size(); i++) {
                    if (map.get(i)) {
                        listDates.add(walletList.get(i).getName());
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
            }
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