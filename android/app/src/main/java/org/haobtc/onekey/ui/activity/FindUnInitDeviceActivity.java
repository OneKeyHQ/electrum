package org.haobtc.onekey.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.mvp.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 */
public class FindUnInitDeviceActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.init_as_new_wallet)
    TextView initAsNewWallet;
    @BindView(R.id.init_as_new_wallet_hide)
    TextView initAsNewWalletHide;
    @BindView(R.id.import_seed)
    TextView importSeed;
    @BindView(R.id.import_seed_hide)
    TextView importSeedHide;
    @BindView(R.id.recovery_device)
    TextView recoveryDevice;
    @BindView(R.id.recovery_device_hide)
    TextView recoveryDeviceHide;

    @Override
    public void init() {
        updateTitle(R.string.pair);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_find_new_device;
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.init_as_new_wallet, R.id.init_as_new_wallet_hide, R.id.import_seed, R.id.import_seed_hide, R.id.recovery_device, R.id.recovery_device_hide})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.init_as_new_wallet:
                toNext(Constant.ACTIVE_MODE_NEW);
                finish();
                break;
            case R.id.init_as_new_wallet_hide:
                break;
            case R.id.import_seed:
                toNext(Constant.ACTIVE_MODE_IMPORT);
                finish();
                break;
            case R.id.import_seed_hide:
                break;
            case R.id.recovery_device:
                break;
            case R.id.recovery_device_hide:
                break;
        }
    }
    /**
     * @param mode 激活方式
     * */
    private void toNext(int mode) {
        Intent intent = new Intent(this, ActivateColdWalletActivity.class);
        intent.putExtra(Constant.ACTIVE_MODE, mode);
        startActivity(intent);
    }
}
