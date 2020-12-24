package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.backup.BackupGuideActivity;
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.haobtc.onekey.ui.dialog.BackupRequireDialog;
import org.haobtc.onekey.utils.Daemon;

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

    @SingleClick
    @OnClick({R.id.img_back, R.id.rel_export_word, R.id.rel_delete_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_export_word:
                startActivity(new Intent(this, SoftPassActivity.class));
                break;
            case R.id.rel_delete_wallet:
                hdWalletIsBackup();
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotPass(GotPassEvent event) {
        PyResponse<String> response = PyEnv.exportMnemonics(event.getPassword());
        String errors = response.getErrors();
        if (Strings.isNullOrEmpty(errors)) {
            Intent intent = new Intent(this, BackupGuideActivity.class);
            intent.putExtra("exportWord", response.getResult());
            intent.putExtra("importHdword", "exportHdword");
            startActivity(intent);
            finish();
        } else {
            mlToast(errors);
        }
    }
    private void hdWalletIsBackup() {
        Log.i("deleteHdWalletNamejxm", "hdWalletIsBackup: "+deleteHdWalletName);
        try {
            PyObject data = Daemon.commands.callAttr("get_backup_info", new Kwarg("name", deleteHdWalletName));
            boolean isBackup = data.toBoolean();
            if (isBackup) {
                Intent intent = new Intent(WalletManageActivity.this, DeleteWalletActivity.class);
                intent.putExtra("deleteHdWalletName", deleteHdWalletName);
                startActivity(intent);
                finish();
            } else {
                //没备份提示备份
                new BackupRequireDialog(mContext).show(getSupportFragmentManager(), "");
            }
        } catch (Exception e) {
            mToast(e.getMessage());
            e.printStackTrace();
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