package org.haobtc.onekey.onekeys.backup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BackupGuideActivity extends BaseActivity {

    @BindView(R.id.backup_tip)
    TextView backupTip;
    @BindView(R.id.text_dont_copy)
    TextView textDontCopy;
    @BindView(R.id.lin_backup_hardware)
    LinearLayout linBackupHardware;

    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_guide;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        String walletType = getIntent().getStringExtra("walletType");
        if ("btc-standard".equals(walletType)) {
            backupTip.setVisibility(View.GONE);
            textDontCopy.setText(getString(R.string.support_backup));
            linBackupHardware.setVisibility(View.GONE);
        }

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.btn_ready_go, R.id.lin_backup_hardware})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_ready_go:
                Intent intent = new Intent(BackupGuideActivity.this, SetHDWalletPassActivity.class);
                intent.putExtra("importHdword", "importHdword");
                intent.putExtra("exportType", "backup");
                startActivity(intent);
                break;
            case R.id.lin_backup_hardware:

                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(FinishEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}