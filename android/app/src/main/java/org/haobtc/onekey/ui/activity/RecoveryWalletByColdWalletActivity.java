package org.haobtc.onekey.ui.activity;

import android.widget.ImageView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.ui.fragment.DevicePINFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 */
public class RecoveryWalletByColdWalletActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;

    @Override
    public void init() {
        mTitle.setText(R.string.recovery_hd_wallet);
//        startFragment(new DevicePINFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }
    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked() {
        finish();
    }
}
