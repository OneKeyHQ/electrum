package org.haobtc.onekey.data.net;

public final class HandleApiAction {

    private HandleApiAction() {
    }

    public interface CallBack {
        void onResponse(String response);
    }
}
