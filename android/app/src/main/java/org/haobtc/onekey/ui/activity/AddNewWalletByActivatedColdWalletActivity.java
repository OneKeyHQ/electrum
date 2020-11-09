package org.haobtc.onekey.ui.activity;

import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CoinBean;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.onekeys.HomeOnekeyActivity;
import org.haobtc.onekey.ui.fragment.SetWalletNameFragment;
import org.haobtc.onekey.ui.listener.IGiveNameListener;
import org.haobtc.onekey.ui.listener.ISelectCoinListener;

import butterknife.BindView;

public class AddNewWalletByActivatedColdWalletActivity extends BaseActivity implements
        ISelectCoinListener, IGiveNameListener {

    @Override
    public void init() {


    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @Override
    public void onCoinChoose(CoinBean bean) {
        //todo create wallet

        startFragment(new SetWalletNameFragment());
    }

    @Override
    public void onWalletInitSuccess() {
        toActivity(HomeOnekeyActivity.class);
    }
}
