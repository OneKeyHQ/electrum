package org.haobtc.keymanager.activities;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.aop.SingleClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActiveFailedActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.btn_finish)
    Button btnFinish;

    @Override
    public int getLayoutId() {
        return R.layout.active_failed;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.btn_finish:
                finishAffinity();
                // Ble.getInstance().disconnectAll();
                break;
            default:
        }
    }
}
