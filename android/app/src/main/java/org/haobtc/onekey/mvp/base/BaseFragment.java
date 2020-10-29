package org.haobtc.onekey.mvp.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;


public abstract class BaseFragment<L extends IBaseListener> extends Fragment implements IBaseView {

    /**
     * weak reference
     */
    private WeakReference<L> viewRef;

    public L getListener() {
        return viewRef == null ? null : viewRef.get();
    }

    public Handler mHandler;

    static class MyHandler extends Handler {
        public MyHandler() {
            super(Looper.getMainLooper());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewRef = new WeakReference<>((L) context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewId(), container, false);
        setActionBar();
        ButterKnife.bind(this, view);
        init(view);
        return view;
    }

    /**
     * init views
     *
     * @param view
     */
    public abstract void init(View view);


    @Override
    public void onDetach() {
        super.onDetach();
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
        System.gc();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        if (mHandler == null) {
            mHandler = new MyHandler();
        }
        mHandler.post(runnable);
    }

}
