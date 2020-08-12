package org.haobtc.wallet.event;
@Deprecated
public class BatchInputWalletEvent {
    String walletName;
    String m;
    String n;
    String xpubs;

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getXpubs() {
        return xpubs;
    }

    public void setXpubs(String xpubs) {
        this.xpubs = xpubs;
    }
}
