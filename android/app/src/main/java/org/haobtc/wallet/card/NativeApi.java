package org.haobtc.wallet.card;



/**
 * @author liyan
 * @date 2020/07/02
 */
@Deprecated
public class NativeApi {

    static {
        System.loadLibrary("coreNDK");
    }

    /**
     * 当native接口的返回值不是int时，需要调用该接口获取错误码
     *
     * @return
     */
    public static native int nativeGetErrorCode();


    //*********************************** NFC接口 *****************************************

    public static native int nativeNFCInit(InitParameter parameter, String paramJson);

    public static native int nativeNFCConnect(int[] handle);

    public static native int nativeNFCDisconnect(long deviceHandle);

    public static native boolean nativeNFCIsConnected(long deviceHandle);

    //****************************************************************************

    public static native String nativeSendApdu(long deviceHandle, String apduStr);

    public static native int nativeReset(long deviceHandle);

    public static native int nativeGenerateSeed(long deviceHandle, String pin);

    public static native int nativeImportMnemonic(long deviceHandle, String pin, String mnemonic);

    public static native String nativeExportMnemonic(long deviceHandle, String pin);

    public static native int nativeVerifyPIN(long contextID, byte[] pin);

    public static native int nativeChangePIN(long deviceHandle, byte[] pin, byte[] newPin);

    public static native String nativeGetDeviceInfo(long deviceHandle);

    public static native String nativeGetDeviceCert(long deviceHandle);

    public static native String nativeEnumApplets(long deviceHandle);

    //*********************************** BTC接口 *****************************************

    public static native int nativeBTCCreateContext(int[] contextIDs, String json, long deviceHandle);

    public static native String nativeBTCTransaction(long contextID, String json);

    public static native String[] nativeBTCGetAddress(long contextID, String json);

}