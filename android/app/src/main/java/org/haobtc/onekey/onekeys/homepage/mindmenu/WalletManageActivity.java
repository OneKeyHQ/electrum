package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE_SHORT;

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
                if (SOFT_HD_PASS_TYPE_SHORT.equals(preferences.getString(SOFT_HD_PASS_TYPE, SOFT_HD_PASS_TYPE_SHORT))) {
                    intent1 = new Intent(this, SetHDWalletPassActivity.class);
                } else {
                    intent1 = new Intent(this, SetLongPassActivity.class);
                }
                intent1.putExtra("importHdword", "exportHdword");
                startActivity(intent1);
                break;
            case R.id.rel_delete_wallet:
                hdWalletIsBackup();
                break;
        }
    }

    private void hdWalletIsBackup() {
        try {
            PyObject data = Daemon.commands.callAttr("get_backup_info", new Kwarg("name", deleteHdWalletName));
            boolean isBackup = data.toBoolean();
            if (isBackup) {
                Intent intent = new Intent(WalletManageActivity.this, DeleteWalletActivity.class);
                intent.putExtra("deleteHdWalletName", deleteHdWalletName);
                startActivity(intent);
            } else {
                //没备份提示备份
                dontBackup(this, R.layout.confrim_delete_hdwallet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dontBackup(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish();
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