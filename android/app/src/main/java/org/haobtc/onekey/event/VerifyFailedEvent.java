package org.haobtc.onekey.event;

/**
 * @author liyan
 * @date 11/27/20
 */

public class VerifyFailedEvent {
    private FailedReason failedReason;
    public enum FailedReason {
        /**
         * 认证失败
         * */
        VERIFY_FAILED,
        /**
         * 连接认证服务器失败
         * */
        NETWORK_ERROR,
        /**
         * 从硬件上获取证书失败
         * */
        GOT_CERT_FAILED
    }
    public VerifyFailedEvent(FailedReason reason) {
        this.failedReason = reason;
    }

    public FailedReason getFailedReason() {
        return failedReason;
    }
}
