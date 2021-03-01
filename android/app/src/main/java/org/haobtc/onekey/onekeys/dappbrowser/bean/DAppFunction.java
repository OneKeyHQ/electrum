package org.haobtc.onekey.onekeys.dappbrowser.bean;

public interface DAppFunction {

    void DAppError(Throwable error, Signable message);

    void DAppReturn(byte[] data, Signable message);
}
