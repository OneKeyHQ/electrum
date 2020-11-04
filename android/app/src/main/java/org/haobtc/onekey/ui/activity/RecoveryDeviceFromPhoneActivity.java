package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseMvpActivity;
import org.haobtc.onekey.mvp.presenter.RecoveryDeviceFromPhonePresenter;
import org.haobtc.onekey.mvp.view.IRecoveryDeviceFromPhoneView;
import org.haobtc.onekey.ui.fragment.FindBackupFromPhoneFragment;
import org.haobtc.onekey.ui.listener.IFindBackupFromPhoneListener;

import butterknife.BindView;

public class RecoveryDeviceFromPhoneActivity extends BaseMvpActivity<RecoveryDeviceFromPhonePresenter>
        implements IRecoveryDeviceFromPhoneView, IFindBackupFromPhoneListener, View.OnClickListener {

    @BindView(R.id.title)
    protected TextView mTitle;

    @Override
    protected RecoveryDeviceFromPhonePresenter initPresenter() {
        return new RecoveryDeviceFromPhonePresenter(this);
    }

    @Override
    public void init() {
        mTitle.setText(R.string.recovery_device_title);
        findViewById(R.id.img_back).setOnClickListener(this);
        startFragment(new FindBackupFromPhoneFragment());

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_recovery_device_from_phone;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }
}
