package org.haobtc.onekey.manager;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.event.ButtonRequestEvent;

import static org.haobtc.onekey.constant.PyConstant.BUTTON_REQUEST_6;
import static org.haobtc.onekey.constant.PyConstant.BUTTON_REQUEST_7;
import static org.haobtc.onekey.constant.PyConstant.BUTTON_REQUEST_8;
import static org.haobtc.onekey.constant.PyConstant.BUTTON_REQUEST_9;
import static org.haobtc.onekey.constant.PyConstant.PASS_PASSPHRASS;
import static org.haobtc.onekey.constant.PyConstant.PIN_CURRENT;
import static org.haobtc.onekey.constant.PyConstant.PIN_NEW_FIRST;

/**
 * @author liyan
 * @date 11/20/20
 *
 * * DCL singleton used for hardware callback
 *  * works in UI thread
 */

public class HardwareCallbackHandler extends Handler {

    /**
     * handler used in python
     */
    private static volatile HardwareCallbackHandler callbackHandle;
    /**
     * the handler to start new activity
     */
    private FragmentActivity fragmentActivity;

    private HardwareCallbackHandler(FragmentActivity activity) {
        this.fragmentActivity = activity;
    }

    public static HardwareCallbackHandler getInstance(FragmentActivity fragmentActivity) {
        if (callbackHandle == null) {
            synchronized (CommunicationModeSelector.MyHandler.class) {
                if (callbackHandle == null) {
                    callbackHandle = new HardwareCallbackHandler(fragmentActivity);
                }
            }
        }
        return callbackHandle;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case PIN_CURRENT:
                EventBus.getDefault().post(new ButtonRequestEvent(PIN_CURRENT));
                break;
            case PIN_NEW_FIRST:
                EventBus.getDefault().post(new ButtonRequestEvent(PIN_NEW_FIRST));
                break;
            case BUTTON_REQUEST_9:
                EventBus.getDefault().post(new ButtonRequestEvent(BUTTON_REQUEST_9));
                break;
            case BUTTON_REQUEST_7:
                EventBus.getDefault().post(new ButtonRequestEvent(BUTTON_REQUEST_7));
                break;
            case BUTTON_REQUEST_6:
                EventBus.getDefault().post(new ButtonRequestEvent(BUTTON_REQUEST_6));
            case BUTTON_REQUEST_8:
                EventBus.getDefault().post(new ButtonRequestEvent(BUTTON_REQUEST_8));
//            case PASS_NEW_PASSPHRASS:
            case PASS_PASSPHRASS:
                break;
            default:
        }
    }
}
