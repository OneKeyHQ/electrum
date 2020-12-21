package org.haobtc.onekey.ui.base;
import java.lang.ref.WeakReference;

public abstract class BasePresenter<V extends IBasePresenterView> implements Presenter<V> {
    protected V mvpView;
    private WeakReference<V> mViewRef;

    @Override
    public void attachView (V view) {
        mViewRef = new WeakReference<V>(view);
        mvpView = view;
    }

    @Override
    public void detachView (V view) {
        mViewRef.clear();
        mvpView = null;
    }

    @Override
    public String getName () {
        if (mvpView != null) {
            return mvpView.getClass().getSimpleName();
        }
        return null;
    }

}
