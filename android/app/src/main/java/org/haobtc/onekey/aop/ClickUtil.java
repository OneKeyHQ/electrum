package org.haobtc.onekey.aop;

import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.ExitEvent;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.ble;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.nfc;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.protocol;

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
                ble.put("IS_CANCEL", true);
                nfc.put("IS_CANCEL", true);
                protocol.callAttr("notify");
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
