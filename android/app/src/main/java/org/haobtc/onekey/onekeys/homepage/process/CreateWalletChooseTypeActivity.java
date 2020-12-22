package org.haobtc.onekey.onekeys.homepage.process;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.event.NameSettedEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.haobtc.onekey.utils.NavUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dr.android.utils.LogUtil;

public class CreateWalletChooseTypeActivity extends BaseActivity {

    @BindView(R.id.rel_derive_hd)
    RelativeLayout relDeriveHd;
    private String name;
    private int type;
    private int selectWalletType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_wallet_choose_type;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        boolean ifHaveHd = getIntent().getBooleanExtra("ifHaveHd", false);
        if (!ifHaveHd) {
            relDeriveHd.setVisibility(View.GONE);
        }
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.rel_derive_hd, R.id.rel_single_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_derive_hd:
            case R.id.rel_single_wallet:
                dealCreate(view.getId());
                break;
        }
    }

    private void dealCreate(int id) {
        switch (id) {
            case R.id.rel_derive_hd:
                type = R.id.rel_derive_hd;
                break;
            case R.id.rel_single_wallet:
                type = R.id.rel_single_wallet;
                break;
        }
//        Intent intent = new Intent(CreateWalletChooseTypeActivity.this, CreateDeriveChooseTypeActivity.class);
//        startActivity(intent);
        NavUtils.gotoCreateDeriveChooseTypeActivity(mContext, true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotPass(GotPassEvent event) {
       switch (type) {
           case R.id.rel_single_wallet:
               PyEnv.createWallet(this, name, event.getPassword(), null, null, selectWalletType);
               finish();
               break;
           case R.id.rel_derive_hd:
               PyResponse<Void> response = PyEnv.createDerivedWallet(name, event.getPassword(), "btc", selectWalletType);
               String error = response.getErrors();
               if (Strings.isNullOrEmpty(error)) {
                   mIntent(HomeOneKeyActivity.class);
                   finish();
               } else {
                   mToast(error);
               }
               break;
       }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotName(NameSettedEvent event) {
        name = event.getName();
        selectWalletType = event.type;
        LogUtil.d(" 选择钱包类型--》" + selectWalletType);
        startActivity(new Intent(this, SoftPassActivity.class));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}