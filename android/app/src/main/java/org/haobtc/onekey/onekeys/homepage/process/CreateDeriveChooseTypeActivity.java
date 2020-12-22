package org.haobtc.onekey.onekeys.homepage.process;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.common.base.Strings;
import com.lxj.xpopup.XPopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.event.NameSettedEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.haobtc.onekey.ui.dialog.custom.SelectWalletTypeDialog;
import org.haobtc.onekey.utils.NavUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dr.android.utils.LogUtil;

public class CreateDeriveChooseTypeActivity extends BaseActivity {
    private boolean isFinish;
    private String name;
    private int selectWalletType;

    public static void gotoCreateDeriveChooseTypeActivity (Context context, boolean finish) {
        Intent intent = new Intent(context, CreateDeriveChooseTypeActivity.class);
        intent.putExtra(Constant.FINISH, finish);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId () {
        return R.layout.activity_create_derive_choose_type;
    }

    @Override
    public void initView () {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {
        isFinish = getIntent().getBooleanExtra(Constant.FINISH, false);
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.rel_type_btc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_type_btc:
                new XPopup.Builder(mContext)
                        .asCustom(new SelectWalletTypeDialog(mContext, new SelectWalletTypeDialog.onClickListener() {
                            @Override
                            public void onClick (int mode) {
                                int purpose = 0;
                                switch (mode) {
                                    case SelectWalletTypeDialog.RecommendType:
                                        purpose = 49;
                                        break;
                                    case SelectWalletTypeDialog.NativeType:
                                        purpose = 84;
                                        break;
                                    case SelectWalletTypeDialog.NormalType:
                                        purpose = 44;
                                        break;
                                    default:
                                        break;
                                }
                                LogUtil.d("purpose  :" + isFinish);
                                PreferencesManager.getSharedPreferences(mContext, Constant.myPreferences).edit().putInt(Constant.Wallet_Purpose, purpose).apply();
                                NavUtils.gotoSoftWalletNameSettingActivity(mContext, purpose);
                                if (isFinish) {
                                    finish();
                                }
                            }
                        })).show();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotName (NameSettedEvent event) {
        name = event.getName();
        selectWalletType = event.type;
        LogUtil.d(" 选择钱包类型--》" + selectWalletType);
        startActivity(new Intent(this, SoftPassActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotPass (GotPassEvent event) {
        PyResponse<Void> response = PyEnv.createDerivedWallet(name, event.getPassword(), "btc", selectWalletType);
        String error = response.getErrors();
        if (Strings.isNullOrEmpty(error)) {
            mIntent(HomeOneKeyActivity.class);
            finish();
        } else {
            mToast(error);
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}