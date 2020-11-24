package org.haobtc.onekey.ui.activity;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.ui.fragment.VerifyConnFragment;

/**
 * @author liyan
 */
public class AuthVerifyActivity extends BaseActivity {


    private boolean mVerifyRet;

    @Override
    public void init() {

        startFragment(new VerifyConnFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }
}
