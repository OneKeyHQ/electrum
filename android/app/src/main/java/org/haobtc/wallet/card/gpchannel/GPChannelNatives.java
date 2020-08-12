package org.haobtc.wallet.card.gpchannel;

/**
 * @Date 2020-07-09  16:29
 * @Author li
 * @Version 1.0
 */
public class GPChannelNatives {

    static {
        System.loadLibrary("gpchannelNDK");
    }

    /**
     * 当native接口的返回值不是int时，需要调用该接口获取错误码
     *
     * @return
     */
    public static native int nativeGetErrorCode();

    public static native int nativeGPCInitialize(String json);

    public static native int nativeGPCFinalize();

    public static native String nativeGPCBuildMutualAuthData();

    public static native int nativeGPCOpenSecureChannel(String response);

    public static native String nativeGPCBuildAPDU(long cla, long ins, long p1, long p2, String data);

    public static native String nativeGPCBuildSafeAPDU(long cla, long ins, long p1, long p2, String data);

    public static native String nativeGPCParseSafeAPDUResponse(String response);

    public static native String nativeGPCParseAPDUResponse(String response);

    public static native String nativeGPCTLVDecode(String apdu);

}
