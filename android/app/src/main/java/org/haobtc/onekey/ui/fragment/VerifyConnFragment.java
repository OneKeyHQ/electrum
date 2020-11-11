package org.haobtc.onekey.ui.fragment;

import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.listener.IVerifyConnListener;

public class VerifyConnFragment extends BaseFragment<IVerifyConnListener> {


    @Override
    public void init(View view) {
        getListener().onUpdateTitle(R.string.auth_verify);

        //todo verify

        getListener().onVerifyComplete(true);

    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_verify_conn;
    }


}
