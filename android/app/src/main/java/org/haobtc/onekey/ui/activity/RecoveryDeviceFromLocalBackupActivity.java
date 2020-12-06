package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.ImageView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.fragment.FindBackupFromPhoneFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 */
public class RecoveryDeviceFromLocalBackupActivity extends BaseActivity {


    @BindView(R.id.img_back)
    ImageView imgBack;

    @Override
    public void init() {
        startFragment(new FindBackupFromPhoneFragment());

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked(View view) {
        finish();
    }
}
