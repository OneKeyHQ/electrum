package org.haobtc.onekey.manager;

import android.nfc.Tag;

/**
 * nfc
 * @author liyan
 */
public final class NfcManager {

    private static NfcManager sInstance;
    public static Tag mTag;

    private NfcManager() {
    }

    public static NfcManager getInstance() {
        if (sInstance == null) {
            synchronized (BleManager.class) {
                if (sInstance == null) {
                    sInstance = new NfcManager();
                }
            }
        }
        return sInstance;
    }

    public void initNfc(Tag tag) {
        PyEnv.nfcEnable();
        PyEnv.sNfcHandler.put("device", tag);
    }

}
