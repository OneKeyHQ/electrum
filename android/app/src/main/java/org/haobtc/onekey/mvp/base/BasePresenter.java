package org.haobtc.onekey.mvp.base;


import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.lang.ref.WeakReference;

public class BasePresenter<V> {

    /**
     * weak reference
     */
    private WeakReference<V> viewRef;

    public BasePresenter(V view) {
        viewRef = new WeakReference<>(view);
    }

    public V getView() {
        return viewRef == null ? null : viewRef.get();
    }

    public void onDestroy() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
        System.gc();
    }

    public FragmentActivity getActivity() {
        if (getView() instanceof Fragment) {
            return ((Fragment) getView()).getActivity();
        } else {
            return (FragmentActivity) getView();
        }
    }
}
