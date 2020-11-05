package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FixHdPassActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_fix_hd_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.rel_fix_pass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_fix_pass:
                Intent intent = new Intent(FixHdPassActivity.this, SetHDWalletPassActivity.class);
                intent.putExtra("importHdword","fixHdPass");
                startActivity(intent);
                break;
        }
    }
}