package org.haobtc.onekey.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.security.InvalidParameterException;

/**
 * 监听 View 的高度变化，达到一定阈值触发显示隐藏 View 的监听。
 *
 * @author Onekey@QuincySx
 * @create 2020-12-29 12:15 PM
 */
public class ViewHeightStatusDetector {
    private int mViewMinHeight;
    private boolean visible = false;
    private VisibilityListener mVisibilityListener;
    private View mView;

    private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = this::calculateHeight;

    public ViewHeightStatusDetector(int minHeight) {
        mViewMinHeight = minHeight;
    }

    public ViewHeightStatusDetector register(@NonNull Fragment fragment) {
        return register(fragment.getView());
    }

    public ViewHeightStatusDetector register(@NonNull Activity activity) {
        return register(activity.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    public ViewHeightStatusDetector register(@Nullable final View view) {
        if (view == null) {
            throw new InvalidParameterException("The View cannot be null.");
        }

        mView = view;
        view.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);

        return this;
    }

    public void unregister(@NonNull Activity activity) {
        unregister(activity.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    public void unregister(@NonNull Fragment fragment) {
        if (fragment.getView() != null) {
            unregister(fragment.getView());
        }
    }

    public void unregister(@NonNull View view) {
        view.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    /**
     * 设置隐藏显示的监听。
     *
     * @param listener 监听器
     */
    public void setVisibilityListener(VisibilityListener listener) {
        mVisibilityListener = listener;
    }

    /**
     * 实时修改触发阈值
     *
     * @param minHeight 阈值
     */
    public void notification(int minHeight) {
        mViewMinHeight = minHeight;
        calculateHeight();
    }

    private void calculateHeight() {
        Rect r = new Rect();
        mView.getWindowVisibleDisplayFrame(r);

        int heightDiff = (r.bottom - r.top);
        if (heightDiff > mViewMinHeight) {
            if (!visible) {
                visible = true;
                if (mVisibilityListener != null) {
                    mVisibilityListener.onVisibilityChanged(true);
                }
            }
        } else {
            if (visible) {
                visible = false;
                if (mVisibilityListener != null) {
                    mVisibilityListener.onVisibilityChanged(false);
                }
            }
        }
    }

    public interface VisibilityListener {
        void onVisibilityChanged(boolean keyboardVisible);
    }
}
