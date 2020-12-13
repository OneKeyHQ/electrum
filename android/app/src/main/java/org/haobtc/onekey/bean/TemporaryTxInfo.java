package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;

/**
 * @author liyan
 */
public class TemporaryTxInfo {

    /**
     * amount : 0
     * size : 258
     * fee : 0.00165
     * time : 35
     * tx : cHNidP8BAHcCAAAAAcny3Df2+QeVX1q6Z6Od26cvF7qT2PS2++HcC2co26OAAQAAAAD9////AgAAAAAAAAAAGXapFIsnKYltSXUB21OiEBOHQ5/43GqSiKwY4ZcAAAAAABl2qRSLJymJbUl1AdtTohATh0Of+NxqkoisAAAAAAABAOECAAAAAAEBspXL1JU8cZewirTQJMWRhmlyOlsJO2unVrwjL2MXweMBAAAAAP7///8CVDyuRgAAAAAWABR45N26W2qOhL4s/7Xsn/t0cqWeWYCWmAAAAAAAGXapFIsnKYltSXUB21OiEBOHQ5/43GqSiKwCRzBEAiBudIFX3JM09tyMbPwKDzbPqbI/K7+T3sFnfVQN3gqtXAIgXJ4ldMdvTMp12TlxaKBOyKeNJ9G2m8bpPbuGRGPk9DABIQI/c3Qs7wI9WOCsNTU7PPb+et7TsuNoV1JN5w7B7aRbyq4OAABCBgTuF/H0tleUwFpnYS6tRxc4hNIgYsgVDhhjulcJsSqe1QdRSHeTJRwLocbA1Pbrz7hNiVylTz96IY7keh1L2OlzDF3WrSQAAAAAAAAAAABCAgTuF/H0tleUwFpnYS6tRxc4hNIgYsgVDhhjulcJsSqe1QdRSHeTJRwLocbA1Pbrz7hNiVylTz96IY7keh1L2OlzDF3WrSQAAAAAAAAAAABCAgTuF/H0tleUwFpnYS6tRxc4hNIgYsgVDhhjulcJsSqe1QdRSHeTJRwLocbA1Pbrz7hNiVylTz96IY7keh1L2OlzDF3WrSQAAAAAAAAAAAA=
     */

    @SerializedName("amount")
    private double amount;
    @SerializedName("size")
    private int size;
    @SerializedName("fee")
    private double fee;
    @SerializedName("time")
    private int time;
    @SerializedName("tx")
    private String tx;

    public static TemporaryTxInfo objectFromData(String str) {
        return new Gson().fromJson(str, TemporaryTxInfo.class);
    }
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTx() {
        return tx;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }
}
