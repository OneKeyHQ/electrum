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

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment implements IBaseView{
    protected Unbinder unbinder;

    public Handler mHandler;

    static class MyHandler extends Handler {
        public MyHandler() {
            super(Looper.getMainLooper());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewId(), container, false);
//        setActionBar();
        unbinder = ButterKnife.bind(this, view);
        if (needEvents()) {
            EventBus.getDefault().register(this);
        }
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        } else if (needEvents()) {
            EventBus.getDefault().unregister(this);
        }

    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        if (mHandler == null) {
            mHandler = new MyHandler();
        }
        mHandler.post(runnable);
    }
    public boolean needEvents() {
        return false;
    }
}
