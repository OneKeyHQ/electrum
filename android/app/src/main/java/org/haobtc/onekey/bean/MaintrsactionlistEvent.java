package org.haobtc.onekey.bean;

import com.google.gson.annotations.SerializedName;

public class MaintrsactionlistEvent {
    @SerializedName("tx_hash")
    private String txHash;
    @SerializedName("date")
    private String date;
    @SerializedName("amount")
    private String amount;
    @SerializedName("is_mine")
    private boolean isMine;
    @SerializedName("confirmations")
    private String confirmations;
    @SerializedName("type")
    private String type;
    @SerializedName("tx_status")
    private String txStatus;
    @SerializedName("invoice_id")
    private String invoiceId;
    @SerializedName("address")
    private String address;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(String txStatus) {
        this.txStatus = txStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(String confirmations) {
        this.confirmations = confirmations;
    }

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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        this.isMine = mine;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
