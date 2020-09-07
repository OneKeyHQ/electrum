package org.haobtc.keymanager.card;

import android.content.Context;
import android.nfc.Tag;


/**
 * The util singleton class help deal with smart card by nfc
 * @author liyan
 * @date 2020/07/03
 *
 */
@Deprecated
public class SmartCard {

    private static volatile SmartCard mSmartCard;

    /**
    the device handler used for jni
*/
    private long mDeviceHandle;

    private SmartCard() {
    }
    public long getDeviceHandle() {
        return mDeviceHandle;
    }

    public static SmartCard getInstance() {
        if (mSmartCard == null) {
           synchronized (SmartCard.class) {
               if (mSmartCard == null) {
                   mSmartCard = new SmartCard();
               }
           }
        }
        return mSmartCard;
    }

    /**
     * init the smart card by jni
     * @param tag the nfc tag
     * @param initParams the init params string from assets/initParams1.json
     * @return -1 means failed
     * */
    public int init(Tag tag, String initParams, Context mContext) {
        if (tag != null) {
            return NativeApi.nativeNFCInit(new InitParameter(mContext, tag),initParams);
        }
        return -1;
    }

    /**
     * check if the card is connected or not
     * @return true if connected else false
     * */
    public boolean isConnect() {
        return NativeApi.nativeNFCIsConnected(mDeviceHandle);
    }

    /**
     * connect to the smart card
     * @return true if success , false if failed
     * */
    public boolean connect() {
        int[] hld = new int[1];
        int ret = NativeApi.nativeNFCConnect(hld);
        if (ret != 0) {
            return false;
        }
        mDeviceHandle = hld[0];
        return true;
    }

    /**
     * release the singleton instance
     * */
    public void onDestroy() {
        mSmartCard = null;
    }
}
