package org.haobtc.onekey.onekeys.backup;

import android.view.KeyEvent;
import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.manager.ActivityHQManager;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dr.android.utils.LogUtil;

public class BackupCheckSuccessActivity extends BaseActivity {

    private Unbinder bind;

    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_check_success;
    }

    @Override
    public void initView() {
        bind = ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.img_back, R.id.btn_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.btn_check:
                ActivityHQManager.getInstance().gotoMain();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
    }

    // 监听了物理返回键，也返回首页
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                ActivityHQManager.getInstance().gotoMain();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}