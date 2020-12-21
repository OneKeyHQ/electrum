package org.haobtc.onekey.ui.base;

public interface IMvpView extends IBasePresenterView {
    void onError (String error);
    void showLoading ();
    void hideLoading ();

}
