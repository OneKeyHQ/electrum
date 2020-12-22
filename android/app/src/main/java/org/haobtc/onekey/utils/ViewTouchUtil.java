package org.haobtc.onekey.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

import org.jetbrains.annotations.NotNull;

/**
 * 扩大 View 的触摸区域。
 * ps：一个父布局下只有最后设置的子 View 有效。
 *
 * @author Onekey@QuincySx
 * @create 2020-12-22 5:39 PM
 */
public class ViewTouchUtil {

    /**
     * 向四周扩大 View 的触摸范围
     *
     * @param view 要扩大的 View
     * @param dp   四周扩大的触控范围，单位:dp
     */
    public static void expandViewTouchDelegate(final @NotNull View view, final float dp) {
        final int dpValue = dp2px(view.getContext(), dp);
        expandViewTouchDelegate(view, dpValue, dpValue, dpValue, dpValue);
    }

    /**
     * 扩大 View 的触摸范围
     *
     * @param view   要扩大的 View
     * @param top    向上扩大的触控范围，单位:px
     * @param bottom 向下扩大的触控范围，单位:px
     * @param left   向左扩大的触控范围，单位:px
     * @param right  向右扩大的触控范围，单位:px
     */
    public static void expandViewTouchDelegate(final @NotNull View view, final int top, final int bottom, final int left, final int right) {
        ((View) view.getParent()).post(() -> {
            Rect bounds = new Rect();
            view.getHitRect(bounds);

            bounds.top -= top;
            bounds.bottom += bottom;
            bounds.left -= left;
            bounds.right += right;

            TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

            if (view.getParent() instanceof View) {
                ((View) view.getParent()).setTouchDelegate(touchDelegate);
            }
        });
    }

    /**
     * 取消扩大 View 的触控范围
     *
     * @param view 需要取消扩大范围的 View
     */
    public static void restoreViewTouchDelegate(final @NotNull View view) {
        ((View) view.getParent()).post(() -> {
            Rect bounds = new Rect();
            bounds.setEmpty();
            TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

            if (view.getParent() instanceof View) {
                ((View) view.getParent()).setTouchDelegate(touchDelegate);
            }
        });
    }

    private static int dp2px(final @NotNull Context context, final Float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
