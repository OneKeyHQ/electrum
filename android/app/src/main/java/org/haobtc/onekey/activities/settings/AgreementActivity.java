package org.haobtc.onekey.activities.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgreementActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;

    @Override
    public int getLayoutId() {
        return R.layout.introduce;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }
}
