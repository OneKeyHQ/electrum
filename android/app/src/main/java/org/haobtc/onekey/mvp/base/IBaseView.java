package org.haobtc.onekey.mvp.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public interface IBaseView {


    /***
     * init layout
     * @return
     */
    public abstract int getContentViewId();


    /**
     * run on ui thread
     *
     * @param action
     */
    void runOnUiThread(Runnable action);

    /**
     * get activity
     *
     * @return
     */
    Activity getActivity();


    /**
     * show input
     *
     * @param v
     * @param event
     * @return
     */
    default boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Screen adaptation   640？
     */
    default void setCustomDensity() {
        DisplayMetrics activityDisplayMetrics = getActivity().getResources().getDisplayMetrics();
        final float targetDensity = (float) activityDisplayMetrics.widthPixels / (float) 640;
        int targetDensityDpi = (int) (160 * targetDensity);
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

    /**
     * activity jump
     *
     * @param to
     */
    default void toActivity(Class<?> to) {
        Activity activity = getActivity();
        Intent intent = new Intent(activity, to);
        activity.startActivity(intent);
    }

    /**
     * set actionBar
     */
    default void setActionBar() {
        Activity activity = getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }


    /**
     * hide keyboard
     */
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    default void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * activity jump for result
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    default void startActivityForResult(Class<?> cls, Bundle bundle,
                                        int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        getActivity().startActivityForResult(intent, requestCode);
    }

    /**
     * set actionBar color
     */
    default void setActionBarColor() {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    /**
     * toast
     *
     * @param id
     */
    default void showToast(final int id) {
        if (id == 0) {
            return;
        }
        runOnUiThread(() -> Toast.makeText(getActivity(), getActivity().getString(id), Toast.LENGTH_SHORT).show());

    }
    /**
     * toast
     *
     * @param info
     */
    default void showToast(String info) {
        if (TextUtils.isEmpty(info)) {
            return;
        }
        runOnUiThread(() -> Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show());

    }


}
