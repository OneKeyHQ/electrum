package org.haobtc.onekey.onekeys.homepage.process;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateWalletChooseTypeActivity extends BaseActivity {

    @BindView(R.id.rel_derive_hd)
    RelativeLayout relDeriveHd;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_wallet_choose_type;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        int ifHaveHd = getIntent().getIntExtra("ifHaveHd", 0);
        if (ifHaveHd == 0) {
            relDeriveHd.setVisibility(View.GONE);
        }
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.rel_derive_hd, R.id.rel_single_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_derive_hd:
                Intent intent = new Intent(CreateWalletChooseTypeActivity.this, CreateDeriveChooseTypeActivity.class);
                intent.putExtra("walletType", "derive");
                startActivity(intent);
                break;
            case R.id.rel_single_wallet:
                Intent intent1 = new Intent(CreateWalletChooseTypeActivity.this, CreateDeriveChooseTypeActivity.class);
                intent1.putExtra("walletType", "single");
                startActivity(intent1);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}