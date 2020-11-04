package org.haobtc.onekey.mvp.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.haobtc.onekey.R;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {


    public Fragment mCurrentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setCustomDensity();
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        setActionBar();
        init();
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
}
