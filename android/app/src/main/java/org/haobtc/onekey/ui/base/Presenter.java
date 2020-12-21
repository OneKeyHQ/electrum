package org.haobtc.onekey.ui.base;
import android.view.View;

public interface Presenter<V> {
    void attachView (V view);
    void detachView(V view);
    String getName ();

}
