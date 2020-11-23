package org.haobtc.onekey.onekeys.backup;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BackupCheckSuccessActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_check_success;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.img_back, R.id.btn_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_check:
                finish();
                break;
        }
    }
}