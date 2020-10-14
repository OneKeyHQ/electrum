package org.haobtc.onekey.onekeys;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuidanceActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.checkbox_ok)
    CheckBox checkboxOk;
    private SharedPreferences.Editor edit;
    private boolean isAgree = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_guidance;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        edit = preferences.edit();
        inits();

    }

    private void inits() {
        checkboxOk.setOnCheckedChangeListener(this);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.text_user1, R.id.btn_begin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_user1:
                break;
//            case R.id.text_user2:
//                break;
            case R.id.btn_begin:
                if (isAgree) {
                    edit.putBoolean("is_first_run", true);
                    edit.apply();
                    mIntent(HomeOnekeyActivity.class);
                } else {
                    mToast(getString(R.string.agree_user));
                }
                break;
            default:
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            isAgree = true;
        } else {
            isAgree = false;
        }
    }
}