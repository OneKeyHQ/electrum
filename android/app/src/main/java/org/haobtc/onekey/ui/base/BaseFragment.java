package org.haobtc.onekey.ui.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.orhanobut.logger.Logger;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.utils.EventBusUtils;

/** @author liyan */
public abstract class BaseFragment extends Fragment implements IBaseView {

    protected Unbinder unbinder;

    private Handler mHandler;

    static class MyHandler extends Handler {

        public MyHandler() {
            super(Looper.getMainLooper());
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Logger.d(" Current Fragment-->" + getClass().getName());
        View view;
        if (enableViewBinding()) {
            view = getLayoutView(inflater, container, savedInstanceState);
            assert view != null;
        } else {
            view = inflater.inflate(getContentViewId(), container, false);
        }
        unbinder = ButterKnife.bind(this, view);
        if (needEvents()) {
            EventBusUtils.register(this);
        }
        init(view);
        return view;
    }

    /**
     * init views it's needless
     *
     * @param view
     */
    public abstract void init(View view);

    public boolean enableViewBinding() {
        return false;
    }

    @Nullable
    public View getLayoutView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (needEvents()) {
            EventBusUtils.unRegister(this);
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

    public <T extends ViewModel> T getApplicationViewModel(Class<T> clazz) {
        return new ViewModelProvider(((MyApplication) requireContext().getApplicationContext()))
                .get(clazz);
    }

    public <T extends ViewModel> T getActivityViewModel(Class<T> clazz) {
        return new ViewModelProvider(requireActivity()).get(clazz);
    }

    public <T extends ViewModel> T getFragmentViewModel(Class<T> clazz) {
        return new ViewModelProvider(this).get(clazz);
    }
}
