package org.haobtc.onekey.mvp.base;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author liyan
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseView {


    public Fragment mCurrentFragment;

    @BindView(R.id.title)
    protected TextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setCustomDensity();
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
//        setActionBar();
        init();
        if (needEvents()) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * init
     */
    public abstract void init();


    @Override
    public Activity getActivity() {
        return this;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideKeyboard();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void startFragment(Fragment fragment) {
        hideKeyboard();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        if (mCurrentFragment == null) {
            fragmentTransaction.add(R.id.container, fragment).commitAllowingStateLoss();
            mCurrentFragment = fragment;
        }
        if (mCurrentFragment != fragment) {
            if (!fragment.isAdded()) {
                fragmentTransaction.hide(mCurrentFragment)
                        .add(R.id.container, fragment).commitAllowingStateLoss();
            } else {
                fragmentTransaction.hide(mCurrentFragment).show(fragment)
                        .commitAllowingStateLoss();
            }
            mCurrentFragment = fragment;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (needEvents()) {
            EventBus.getDefault().unregister(this);
        }
    }
    /**
     * 修改标题
     * */
    public void updateTitle(int title) {
        runOnUiThread(() -> {
            if(mTitle != null){
                mTitle.setText(title);
            }
        });
    }
    /**
     * 是否注册eventBus的钩子函数
     * */
    public boolean needEvents() {
        return false;
    }
}
