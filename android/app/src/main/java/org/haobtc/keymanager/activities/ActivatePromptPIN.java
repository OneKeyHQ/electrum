package org.haobtc.keymanager.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.aop.SingleClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
//
// Created by liyan on 2020/6/17.
//
public class ActivatePromptPIN extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.backup)
    Button backup;

    @Override
    public int getLayoutId() {
        return R.layout.pin_setting_after_init;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @SingleClick(value = 5000)
    @OnClick({R.id.img_back, R.id.backup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.backup:
                startActivity(new Intent(this, BackupWaySelector.class));
                finish();
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
