package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeleteWalletActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.checkbox_ok)
    CheckBox checkboxOk;
    @BindView(R.id.btn_forward)
    Button btnForward;

    @Override
    public int getLayoutId() {
        return R.layout.activity_delete_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        checkboxOk.setOnCheckedChangeListener(this);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.btn_forward})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_forward:
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            btnForward.setEnabled(true);
            btnForward.setBackground(getDrawable(R.drawable.delete_wallet_yes));
        }
    }
}