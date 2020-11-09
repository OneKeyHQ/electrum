package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.BackupWalletBean;
import org.haobtc.onekey.mvp.base.BaseMvpActivity;
import org.haobtc.onekey.mvp.presenter.RecoveryDeviceFromPhonePresenter;
import org.haobtc.onekey.mvp.view.IRecoveryDeviceFromPhoneView;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.ui.fragment.AddAssetFragment;
import org.haobtc.onekey.ui.fragment.ColdDeviceConfirmFragment;
import org.haobtc.onekey.ui.fragment.FindBackupFromPhoneFragment;
import org.haobtc.onekey.ui.fragment.GiveNameFragment;
import org.haobtc.onekey.ui.fragment.SetDevicePINFragment;
import org.haobtc.onekey.ui.listener.IAddAssetListener;
import org.haobtc.onekey.ui.listener.IColdDeviceConfirmListener;
import org.haobtc.onekey.ui.listener.IFindBackupFromPhoneListener;
import org.haobtc.onekey.ui.listener.IGiveNameListener;
import org.haobtc.onekey.ui.listener.ISetDevicePassListener;

import butterknife.BindView;

public class RecoveryDeviceFromPhoneBackupActivity extends BaseMvpActivity<RecoveryDeviceFromPhonePresenter>
        implements IRecoveryDeviceFromPhoneView, IFindBackupFromPhoneListener, View.OnClickListener
        , ISetDevicePassListener, IColdDeviceConfirmListener, IGiveNameListener, IAddAssetListener {

    @Override
    protected RecoveryDeviceFromPhonePresenter initPresenter() {
        return new RecoveryDeviceFromPhonePresenter(this);
    }

    @Override
    public void init() {
        findViewById(R.id.img_back).setOnClickListener(this);
        startFragment(new FindBackupFromPhoneFragment());

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    public void onBackupToRecovery(BackupWalletBean bean) {
        //todo  backup info to recovery

        startFragment(new SetDevicePINFragment());
    }

    @Override
    public void onSetDevicePassSuccess() {
        startFragment(new ColdDeviceConfirmFragment());
    }

    @Override
    public void toNext() {
        startFragment(new GiveNameFragment());
    }

    @Override
    public void onWalletInitSuccess() {
        startFragment(new AddAssetFragment());
    }

    @Override
    public void onAddAssetsComplete() {
        toActivity(HomeOnekeyActivity.class);
    }
}
