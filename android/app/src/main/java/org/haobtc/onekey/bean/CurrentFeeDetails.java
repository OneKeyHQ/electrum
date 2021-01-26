package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author jinxiaomin
 */
public class CurrentFeeDetails {

    /**
     * slow : {"size":65,"feerate":20.01,"fiat":"1.52 CNY","time":100,"fee":"0.00001301"}
     * normal : {"size":65,"feerate":20.01,"fiat":"1.52 CNY","time":50,"fee":"0.00001301"}
     * slowest : {"size":65,"feerate":20.01,"fiat":"1.52 CNY","time":250,"fee":"0.00001301"}
     * fast : {"size":65,"feerate":20.01,"fiat":"1.52 CNY","time":20,"fee":"0.00001301"}
     */

    @SerializedName("slow")
    private DetailBean slow;
    @SerializedName("normal")
    private DetailBean normal;
    @SerializedName("slowest")
    private DetailBean slowest;
    @SerializedName("fast")
    private DetailBean fast;

    public static CurrentFeeDetails objectFromDate(String str) {
        return new Gson().fromJson(str, CurrentFeeDetails.class);
    }

    public DetailBean getSlow() {
        return slow;
    }

    public void setSlow(DetailBean slow) {
        this.slow = slow;
    }

    public DetailBean getNormal() {
        return normal;
    }

    public void setNormal(DetailBean normal) {
        this.normal = normal;
    }

    public DetailBean getSlowest() {
        return slowest;
    }

    public void setSlowest(DetailBean slowest) {
        this.slowest = slowest;
    }

    public DetailBean getFast() {
        return fast;
    }

    public void setFast(DetailBean fast) {
        this.fast = fast;
    }

    public static class DetailBean {
        /**
         * size : 65
         * feerate : 20.01
         * fiat : 1.52 CNY
         * time : 100
         * fee : 0.00001301
         * gas_price : 42,
         * gas_limit : 40000,
         */

        @SerializedName("size")
        private int size;
        @SerializedName("feerate")
        private double feerate;
        @SerializedName("fiat")
        private String fiat;
        @SerializedName("time")
        private int time;
        @SerializedName("fee")
        private String fee;
        @SerializedName("gas_price")
        private double gasPrice;
        @SerializedName("gas_limit")
        private int gasLimit;

        public double getGasPrice() {
            return gasPrice;
        }

        public void setGasPrice(double gasPrice) {
            this.gasPrice = gasPrice;
        }

        public int getGasLimit() {
            return gasLimit;
        }

        public void setGasLimit(int gasLimit) {
            this.gasLimit = gasLimit;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public double getFeerate() {
            return feerate;
        }

        public void setFeerate(double feerate) {
            this.feerate = feerate;
        }

        public String getFiat() {
            return fiat;
        }

        public void setFiat(String fiat) {
            this.fiat = fiat;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }
    }
}
