package org.haobtc.wallet.mvp.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

public abstract class BaseMvpFragment<P extends BasePresenter, L extends IBaseListener> extends BaseFragment<L> {

    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = initPresenter();
        super.onCreate(savedInstanceState);
    }


    /**
     * init presenter
     *
     * @return
     */
    protected abstract P initPresenter();

    @Override
    public void onDetach() {
        super.onDetach();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }
}
