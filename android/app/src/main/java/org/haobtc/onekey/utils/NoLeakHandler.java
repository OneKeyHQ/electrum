package org.haobtc.onekey.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @Description: 作为线程间传递消息
 * @Author: peter Qin
 * @CreateDate: 2020/12/16$ 9:58 AM$
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/16$ 9:58 AM$
 * @UpdateRemark: 更新说明：
 */

public class NoLeakHandler extends Handler {
    private WeakReference<HandlerCallback> mCallback;
    private boolean isValid = true;

    public NoLeakHandler(HandlerCallback callback) {
        mCallback = new WeakReference<HandlerCallback>(callback);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mCallback!=null && mCallback.get() != null && isValid) {
            mCallback.get().handleMessage(msg);
        }
    }

    public interface HandlerCallback {
        void handleMessage(Message msg);
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }
}
