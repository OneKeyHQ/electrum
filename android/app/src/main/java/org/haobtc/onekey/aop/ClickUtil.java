package org.haobtc.onekey.aop;

import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.manager.PyEnv;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.ble;

/**
 * @author liyan
 */
public class ClickUtil {
    /**
     * the last click time
     */
    private static long mLastClickTime;
    /**
     * the view of id last clicked
     */
    private static int mLastClickViewId;

    /**
     * is fast click
     *
     * @param v  the view clicked
     * @param intervalMillis  interval（ms）
     * @return true  for fast click
     */
    public static boolean isFastDoubleClick(View v, long intervalMillis) {
        int viewId = v.getId();
        if (viewId == R.id.img_back || viewId == R.id.img_cancel) {
            EventBus.getDefault().post(new ExitEvent());
            if (ble != null) {
                PyEnv.bleCancel();
                PyEnv.nfcCancel();
                PyEnv.sNotify();
            }
            return false;
        }
        long time = System.currentTimeMillis();
        long timeInterval = Math.abs(time - mLastClickTime);
        if (timeInterval < intervalMillis && viewId == mLastClickViewId) {
            return true;
        } else {
            mLastClickTime = time;
            mLastClickViewId = viewId;
            return false;
        }
    }
}
