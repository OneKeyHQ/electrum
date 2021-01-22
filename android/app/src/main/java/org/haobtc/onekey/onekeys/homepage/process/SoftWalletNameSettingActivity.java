package org.haobtc.onekey.onekeys.homepage.process;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.NameSettedEvent;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.onekeys.walletprocess.SoftWalletNameSettingFragment;

public class SoftWalletNameSettingActivity extends BaseActivity implements OnFinishViewCallBack,
        SoftWalletNameSettingFragment.OnSetWalletNameCallback {
    private static final String EXT_WALLET_PURPOSE = Constant.WALLET_PURPOSE;
    private static final String EXT_WALLET_TYPE = Constant.WALLET_TYPE;

    private static final String EXT_RESULT_WALLET_PURPOSE = Constant.WALLET_PURPOSE;
    private static final String EXT_RESULT_WALLET_TYPE = Constant.WALLET_TYPE;
    private static final String EXT_RESULT_WALLET_NAME = "wallet_name";

    private static Intent obtainIntent(Context context, int purpose, String type) {
        Intent intent = new Intent(context, SoftWalletNameSettingActivity.class);
        intent.putExtra(EXT_WALLET_PURPOSE, purpose);
        intent.putExtra(EXT_WALLET_TYPE, type);
        return intent;
    }

    public static void start(Context context, int purpose, String type) {
        context.startActivity(obtainIntent(context, purpose, type));
    }

    public static void startForResult(Activity activity, int requestCode, int purpose, String type) {
        activity.startActivityForResult(obtainIntent(activity.getBaseContext(), purpose, type), requestCode);
    }

    public static ResultDataBean decodeResultData(Intent intent) {
        int purpose = intent.getIntExtra(EXT_RESULT_WALLET_PURPOSE, 44);
        String type = intent.getStringExtra(EXT_RESULT_WALLET_TYPE);
        String name = intent.getStringExtra(EXT_RESULT_WALLET_NAME);
        return new ResultDataBean(purpose, type, name);
    }

    public static class ResultDataBean {
        public final int purpose;
        public final String walletType;
        public final String name;

        public ResultDataBean(int purpose, String walletType, String name) {
            this.purpose = purpose;
            this.walletType = walletType;
            this.name = name;
        }
    }

    private int mPurpose;
    private String mWalletType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_derive_wallet_name;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        mPurpose = getIntent().getIntExtra(EXT_WALLET_PURPOSE, 0);
        mWalletType = getIntent().getStringExtra(EXT_WALLET_TYPE);
    }

    @Override
    public void onSetWalletName(String name) {
        NameSettedEvent nameSettedEvent = new NameSettedEvent(name);
        EventBus.getDefault().post(nameSettedEvent);

        nameSettedEvent.addressPurpose = mPurpose;
        nameSettedEvent.walletType = mWalletType;
        Intent intent = new Intent();
        intent.putExtra(EXT_RESULT_WALLET_PURPOSE, mPurpose);
        intent.putExtra(EXT_RESULT_WALLET_TYPE, mWalletType);
        intent.putExtra(EXT_RESULT_WALLET_NAME, name);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    @Override
    public void onFinishView() {
        finish();
    }
}
