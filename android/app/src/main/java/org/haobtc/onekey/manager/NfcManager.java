package org.haobtc.onekey.manager;

import android.nfc.Tag;

/**
 * nfc
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
        PyEnv.sUsbTransport.put("ENABLED", false);
        PyEnv.sBleTransport.put("ENABLED", false);
        PyEnv.sNfcTransport.put("ENABLED", true);
        PyEnv.sNfcHandler.put("device", tag);
    }

}
