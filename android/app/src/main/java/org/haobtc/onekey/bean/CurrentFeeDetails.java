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
    private SlowBean slow;
    @SerializedName("normal")
    private NormalBean normal;
    @SerializedName("slowest")
    private SlowestBean slowest;
    @SerializedName("fast")
    private FastBean fast;
    public static CurrentFeeDetails objectFromDate(String str) {
        System.out.println("============" + str);
        return new Gson().fromJson(str, CurrentFeeDetails.class);
    }
    public SlowBean getSlow() {
        return slow;
    }

    public void setSlow(SlowBean slow) {
        this.slow = slow;
    }

    public NormalBean getNormal() {
        return normal;
    }

    public void setNormal(NormalBean normal) {
        this.normal = normal;
    }

    public SlowestBean getSlowest() {
        return slowest;
    }

    public void setSlowest(SlowestBean slowest) {
        this.slowest = slowest;
    }

    public FastBean getFast() {
        return fast;
    }

    public void setFast(FastBean fast) {
        this.fast = fast;
    }

    public static class SlowBean {
        /**
         * size : 65
         * feerate : 20.01
         * fiat : 1.52 CNY
         * time : 100
         * fee : 0.00001301
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

    public static class NormalBean {
        /**
         * size : 65
         * feerate : 20.01
         * fiat : 1.52 CNY
         * time : 50
         * fee : 0.00001301
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

    public static class SlowestBean {
        /**
         * size : 65
         * feerate : 20.01
         * fiat : 1.52 CNY
         * time : 250
         * fee : 0.00001301
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

    public static class FastBean {
        /**
         * size : 65
         * feerate : 20.01
         * fiat : 1.52 CNY
         * time : 20
         * fee : 0.00001301
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
