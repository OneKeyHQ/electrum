package org.haobtc.onekey.onekeys.homepage.process;

import android.content.Intent;
import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE;

public class CreateDeriveChooseTypeActivity extends BaseActivity {
    private String walletType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_derive_choose_type;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        walletType = getIntent().getStringExtra(CURRENT_SELECTED_WALLET_TYPE);


    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.rel_type_btc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_type_btc:
                Intent intent = new Intent(CreateDeriveChooseTypeActivity.this, SetDeriveWalletNameActivity.class);
                intent.putExtra(CURRENT_SELECTED_WALLET_TYPE,walletType);
                intent.putExtra("currencyType","btc");
                startActivity(intent);

                break;
        }
    }
}