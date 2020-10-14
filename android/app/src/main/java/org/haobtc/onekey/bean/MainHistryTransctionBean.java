package org.haobtc.onekey.bean;
@Deprecated
public class MainHistryTransctionBean {

    /**
     * tx_hash : 029a5002de1703279f256bb09c09c6d8fdf8f784b762c26fa6d5f7f9b5de7d6a
     * date : 2019-11-29 11:57
     * message :
     * confirmations : 1663
     * is_mine : false
     * amount : 10. mBTC
     */

    private String txHash;
    private String date;
    private String message;
    private int confirmations;
    private boolean isMine;
    private String amount;

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        this.isMine = mine;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
