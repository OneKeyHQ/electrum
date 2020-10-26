package org.haobtc.onekey.passageway;

import android.nfc.Tag;

/**
 * nfc
 */
public final class NfcPassageway{

    private static NfcPassageway sInstance;
    public static Tag mTag;

    private NfcPassageway() {
    }

    public static NfcPassageway getInstance() {
        if (sInstance == null) {
            synchronized (BlePassageway.class) {
                if (sInstance == null) {
                    sInstance = new NfcPassageway();
                }
            }
        }
        return sInstance;
    }

    public void initNfc(Tag tag) {
        HandleCommands.sUsbTransport.put("ENABLED", false);
        HandleCommands.sBleTransport.put("ENABLED", false);
        HandleCommands.sNfcTransport.put("ENABLED", true);
        HandleCommands.sNfcHandler.put("device", tag);
    }

}
