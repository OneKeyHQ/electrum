package org.haobtc.onekey.ui.activity;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.ui.fragment.VerifyConnFragment;
import org.haobtc.onekey.ui.fragment.VerifyResultFragment;
import org.haobtc.onekey.ui.listener.IVerifyConnListener;
import org.haobtc.onekey.ui.listener.IVerifyResultListener;

public class AuthVerifyActivity extends BaseActivity implements IVerifyConnListener, IVerifyResultListener {


    private boolean mVerifyRet;

    @Override
    public void init() {

        startFragment(new VerifyConnFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @Override
    public void onVerifyComplete(boolean ret) {
        this.mVerifyRet = ret;
        startFragment(new VerifyResultFragment());
    }


    public boolean getVerifyRet() {
        return this.mVerifyRet;
    }


    @Override
    public void onBackDevice() {
        // todo back device

    }
}
