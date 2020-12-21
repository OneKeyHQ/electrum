package org.haobtc.onekey.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * <p>
 * NormalDevice is a hardware which is inited by normal way
 */
public class FindNormalDeviceActivity extends BaseActivity {


    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.add_new_wallet)
    TextView addNewWallet;
    @BindView(R.id.recovery_used_wallet)
    TextView recoveryUsedWallet;
    @BindView(R.id.multi_sig_wallet)
    TextView multiSigWallet;
    public static String deviceId;
    @Override
    public void init() {
        updateTitle(R.string.pair);
        deviceId = getIntent().getStringExtra(Constant.DEVICE_ID);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_find_device_no_backup;
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.add_new_wallet, R.id.recovery_used_wallet, R.id.multi_sig_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                PyEnv.cancelPinInput();
                finish();
                break;
            case R.id.add_new_wallet:
                startActivity(new Intent(this, CreatePersonalWalletActivity.class));
                break;
            case R.id.recovery_used_wallet:
                startActivity(new Intent(this, RecoveryHardwareOnceWallet.class));
                break;
            case R.id.multi_sig_wallet:
                showToast(R.string.support_less_promote);
              // startActivity(new Intent(this, CreateMultiSigWalletActivity.class));
                break;
            default:
        }
    }

}
