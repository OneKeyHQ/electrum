package org.haobtc.onekey.onekeys.backup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HdRootMnemonicsActivity;

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
    private Intent intent;
    private String exportWord;
    private String importHdword;

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
            backupTip.setVisibility(View.GONE);
            textDontCopy.setVisibility(View.GONE);
            linBackupHardware.setVisibility(View.GONE);
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

    @OnClick({R.id.img_back, R.id.btn_ready_go, R.id.lin_backup_hardware})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_ready_go:
                if ("exportHdword".equals(importHdword)) {
                    dontScreen(this, R.layout.dont_screenshot);
                } else {
                    if ("short".equals(preferences.getString("shortOrLongPass", "short"))) {
                        intent = new Intent(BackupGuideActivity.this, SetHDWalletPassActivity.class);
                    } else {
                        intent = new Intent(BackupGuideActivity.this, SetLongPassActivity.class);
                    }
                    intent.putExtra("importHdword", "backupMnemonic");
                    startActivity(intent);
                }
                break;
            case R.id.lin_backup_hardware:
                Intent intent1;
                if ("short".equals(preferences.getString("shortOrLongPass", "short"))) {
                    intent1 = new Intent(BackupGuideActivity.this, SetHDWalletPassActivity.class);
                } else {
                    intent1 = new Intent(BackupGuideActivity.this, SetLongPassActivity.class);
                }
                intent1.putExtra("importHdword", "backupMnemonic");
                intent1.putExtra(Constant.OPERATE_TYPE, Constant.EXPORT_DESTINATIONS);
                startActivity(intent1);
                break;
        }
    }

    private void dontScreen(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.btn_next).setOnClickListener(v -> {
            Intent intent = new Intent(context, HdRootMnemonicsActivity.class);
            intent.putExtra("exportWord", exportWord);
            intent.putExtra("importHdword", importHdword);
            startActivity(intent);
            finish();
            dialogBtoms.dismiss();
        });
        view.findViewById(R.id.img_cancel).setOnClickListener(v -> {
            dialogBtoms.dismiss();
        });
        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.setCanceledOnTouchOutside(true);
        dialogBtoms.show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(FinishEvent event) {
        finish();
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
}