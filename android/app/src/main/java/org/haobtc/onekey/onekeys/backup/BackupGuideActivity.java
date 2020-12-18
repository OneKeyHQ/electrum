package org.haobtc.onekey.onekeys.backup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.event.ShowMnemonicEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HdRootMnemonicsActivity;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.haobtc.onekey.ui.dialog.ScreenshotWarningDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE;

/**
 * @author xiaoming
 */
public class BackupGuideActivity extends BaseActivity {

    @BindView(R.id.backup_tip)
    TextView backupTip;
    @BindView(R.id.text_dont_copy)
    TextView textDontCopy;
    @BindView(R.id.lin_backup_hardware)
    LinearLayout linBackupHardware;
    @BindView(R.id.text_title)
    TextView textTitle;
    private SharedPreferences preferences;
    private String exportWord;
    private String importHdword;
    private int destination;

    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_guide;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        String walletType = getIntent().getStringExtra(CURRENT_SELECTED_WALLET_TYPE);
        exportWord = getIntent().getStringExtra("exportWord");
        importHdword = getIntent().getStringExtra("importHdword");
        if ("exportHdword".equals(importHdword)) {
            textTitle.setText(getString(R.string.export_word_));
            backupTip.setVisibility(View.INVISIBLE);
            textDontCopy.setVisibility(View.INVISIBLE);
            linBackupHardware.setVisibility(View.INVISIBLE);
        } else {
            if ("btc-standard".equals(walletType)) {
                backupTip.setVisibility(View.GONE);
                textDontCopy.setText(getString(R.string.support_backup));
                linBackupHardware.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void initData() {

    }

    @SingleClick(value = 1000)
    @OnClick({R.id.img_back, R.id.btn_ready_go, R.id.lin_backup_hardware})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_ready_go:
                if ("exportHdword".equals(importHdword)) {
                    new ScreenshotWarningDialog().show(getSupportFragmentManager(), "mnemonic");
                } else {
                    destination = 0;
                    startActivity(new Intent(this, SoftPassActivity.class));
                }
                break;
            case R.id.lin_backup_hardware:
                destination = 1;
                startActivity(new Intent(this, SoftPassActivity.class));
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNext(ShowMnemonicEvent event) {
        Intent intent = new Intent(this, HdRootMnemonicsActivity.class);
        intent.putExtra("exportWord", exportWord);
        intent.putExtra("importHdword", importHdword);
        startActivity(intent);
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotPass(GotPassEvent event) {
        PyResponse<String> response = PyEnv.exportMnemonics(event.getPassword());
       String errors = response.getErrors();
       if (Strings.isNullOrEmpty(errors)) {
            switch (destination) {
                case 0:
                    Intent intent0 = new Intent(this, HdRootMnemonicsActivity.class);
                    intent0.putExtra("exportWord", response.getResult());
                    intent0.putExtra("importHdword", importHdword);
                    startActivity(intent0);
                    finish();
                    break;
                case 1:
                    Intent intent = new Intent(this, SearchDevicesActivity.class);
                    intent.putExtra(Constant.SEARCH_DEVICE_MODE, Constant.SearchDeviceMode.MODE_BACKUP_HD_WALLET_TO_DEVICE);
                    intent.putExtra(Constant.MNEMONICS, response.getResult());
                    startActivity(intent);
                    finish();
                    break;
            }
       } else {
           mToast(errors);
       }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(ExitEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean isRestricted() {
        return super.isRestricted();
    }
}