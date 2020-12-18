package org.haobtc.onekey.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.LunchActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author liyan
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseView {

    public Fragment mCurrentFragment;
    public Context mContext;

    @BindView(R.id.title)
    @Nullable
    protected TextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setCustomDensity();
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            Intent intent = new Intent(this, LunchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        mContext = this;
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        if (requireSecure()) {
            requestSecure();
        }
        diyWindow();
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

    /**
     * Set transparent immersion bar : white backgrand black text
     */
    public void diyWindow() {
        //other one write
        Window window = getWindow();
        if (keepScreenOn()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
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
     */
    public void updateTitle(int title) {
        runOnUiThread(() -> {
            if (mTitle != null) {
                mTitle.setText(title);
            }
        });
    }

    /**
     * 禁止录屏和截图
     */
    private void requestSecure() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    /**
     * 获取页面隐私属性的钩子
     */
    public boolean requireSecure() {
        return false;
    }

    /**
     * 是否注册eventBus的钩子函数
     */
    public boolean needEvents() {
        return false;
    }

    /**
     * 是否保持屏幕常亮的钩子
     */
    public boolean keepScreenOn() {
        return false;
    }
}
