package org.haobtc.onekey.onekeys.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.ImprotSingleActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecoverHdWalletActivity extends BaseActivity {

    @BindView(R.id.edit_one)
    EditText editOne;
    @BindView(R.id.edit_two)
    EditText editTwo;
    @BindView(R.id.edit_three)
    EditText editThree;
    @BindView(R.id.edit_four)
    EditText editFour;
    @BindView(R.id.edit_five)
    EditText editFive;
    @BindView(R.id.edit_six)
    EditText editSix;
    @BindView(R.id.edit_seven)
    EditText editSeven;
    @BindView(R.id.edit_eight)
    EditText editEight;
    @BindView(R.id.edit_nine)
    EditText editNine;
    @BindView(R.id.edit_ten)
    EditText editTen;
    @BindView(R.id.edit_eleven)
    EditText editEleven;
    @BindView(R.id.edit_twelve)
    EditText editTwelve;
    @BindView(R.id.btn_recovery)
    Button btnRecovery;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recover_hd_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.btn_recovery, R.id.lin_hard_recovery, R.id.lin_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_recovery:
                Intent intent = new Intent(RecoverHdWalletActivity.this, SetHDWalletPassActivity.class);
                startActivity(intent);
                break;
            case R.id.lin_hard_recovery:
                break;
            case R.id.lin_import:
                Intent intent2 = new Intent(RecoverHdWalletActivity.this, ImprotSingleActivity.class);
                startActivity(intent2);
                break;
        }
    }
}