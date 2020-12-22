package org.haobtc.onekey.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CustomFeeInfo {

    /**
     * customer : {"fee":"0.00000325","feerate":5,"time":250,"fiat":"0.49 CNY","size":65}
     */


    public static CustomFeeInfo objectFromDate(String str) {
        System.out.println("============" + str);
        return new Gson().fromJson(str, CustomFeeInfo.class);
    }
    @SerializedName("customer")
    private CustomerBean customer;

    public CustomerBean getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerBean customer) {
        this.customer = customer;
    }

    public static class CustomerBean {
        /**
         * fee : 0.00000325
         * feerate : 5.0
         * time : 250
         * fiat : 0.49 CNY
         * size : 65
         */

        @SerializedName("fee")
        private String fee;
        @SerializedName("feerate")
        private double feerate;
        @SerializedName("time")
        private int time;
        @SerializedName("fiat")
        private String fiat;
        @SerializedName("size")
        private int size;

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public double getFeerate() {
            return feerate;
        }

        public void setFeerate(double feerate) {
            this.feerate = feerate;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getFiat() {
            return fiat;
        }

        public void setFiat(String fiat) {
            this.fiat = fiat;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
