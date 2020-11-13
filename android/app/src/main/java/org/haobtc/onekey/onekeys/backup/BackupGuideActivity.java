package org.haobtc.onekey.onekeys.backup;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.chaquo.python.PyObject;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.importmethod.ImportPrivateKeyActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HdRootMnemonicsActivity;
import org.haobtc.onekey.utils.Daemon;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BackupGuideActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_guide;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
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
                intent.putExtra("exportType","backup");
                startActivity(intent);
                break;
            case R.id.lin_backup_hardware:

                break;
        }
    }
}