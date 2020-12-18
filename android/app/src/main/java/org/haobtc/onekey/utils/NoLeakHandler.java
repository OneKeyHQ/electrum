package org.haobtc.onekey.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;


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
