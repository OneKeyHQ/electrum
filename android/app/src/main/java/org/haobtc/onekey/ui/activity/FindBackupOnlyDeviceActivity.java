package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 *
 *BackupOnlyDevice: is a hardware which is inited only as an backup
 */
public class FindBackupOnlyDeviceActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.recovery_hd_wallet)
    TextView recoveryHdWallet;
    @BindView(R.id.recovery_hd_wallet_hide)
    TextView recoveryHdWalletHide;

    @Override
    public void init() {
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_find_device_and_backed_up;
    }


    @OnClick({R.id.img_back, R.id.recovery_hd_wallet, R.id.recovery_hd_wallet_hide})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                break;
            case R.id.recovery_hd_wallet:
                break;
            case R.id.recovery_hd_wallet_hide:
                break;
        }
    }
}
