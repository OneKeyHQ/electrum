package org.haobtc.wallet.mvp.base;

public class BaseMvpPresenter<V, M> extends BasePresenter<V> {


    protected M mModel;

    public BaseMvpPresenter(V view, M model) {
        super(view);
        this.mModel = model;
    }

    @Override
    public void onDestroy() {
        if (mModel != null) {
            mModel = null;
        }
        super.onDestroy();
    }
}
