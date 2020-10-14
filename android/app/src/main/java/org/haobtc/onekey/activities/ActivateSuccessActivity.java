package org.haobtc.onekey.activities;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivateSuccessActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.bn_back)
    Button bnBack;

    @Override
    public int getLayoutId() {
        return R.layout.activated_successful;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.bn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.bn_back:
                finishAffinity();
              //  Ble.getInstance().disconnectAll();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}
