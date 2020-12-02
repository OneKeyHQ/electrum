package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletManageActivity extends BaseActivity {

    @BindView(R.id.text_safe)
    TextView textSafe;
    @BindView(R.id.rel_export_word)
    RelativeLayout relExportWord;
    private String deleteHdWalletName;
    private SharedPreferences preferences;
    private Intent intent1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_manage;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        deleteHdWalletName = getIntent().getStringExtra("deleteHdWalletName");
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.rel_export_word, R.id.rel_delete_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_export_word:
                if ("short".equals(preferences.getString("shortOrLongPass", "short"))) {
                    intent1 = new Intent(this, SetHDWalletPassActivity.class);
                } else {
                    intent1 = new Intent(this, SetLongPassActivity.class);
                }
                intent1.putExtra("importHdword", "exportHdword");
                startActivity(intent1);
                break;
            case R.id.rel_delete_wallet:
                Intent intent = new Intent(WalletManageActivity.this, DeleteWalletActivity.class);
                intent.putExtra("deleteHdWalletName", deleteHdWalletName);
                startActivity(intent);
                break;
        }
    }

    @Subscribe
    public void onFinish(FinishEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}