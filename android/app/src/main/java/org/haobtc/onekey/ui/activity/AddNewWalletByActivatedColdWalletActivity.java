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

/**
 * @author liyan
 */
public class AddNewWalletByActivatedColdWalletActivity extends BaseActivity {

    @Override
    public void init() {


    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }
}
