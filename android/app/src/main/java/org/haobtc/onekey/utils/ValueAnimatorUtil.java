package org.haobtc.onekey.utils;

import android.animation.ValueAnimator;
import android.view.View;

public final class ValueAnimatorUtil {

    private ValueAnimatorUtil(){}

    /***
     * change height by animator
     * @param view
     * @param begin
     * @param end
     */
    public static void animatorHeightLayout(View view, int begin, int end) {
        ValueAnimator va = ValueAnimator.ofInt(begin, end);
        va.addUpdateListener(valueAnimator -> {
            int h = (Integer) valueAnimator.getAnimatedValue();
            view.getLayoutParams().height = h;
            view.requestLayout();
        });
        va.setDuration(500);
        va.start();
    }
}
