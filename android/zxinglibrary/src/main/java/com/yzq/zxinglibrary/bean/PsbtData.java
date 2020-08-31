package com.yzq.zxinglibrary.bean;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author liyan
 * @date 2020/8/27
 */
//
public class PsbtData {

    /**
     * checkSum : 7fab052bf96970880128aab823d3b2d6
     * compress : true
     * index : 1
     * total : 2
     * value :
     * valueType : protobuf
     */

    @SerializedName("checkSum")
    private String checkSum;
    @SerializedName("compress")
    private boolean compress;
    @SerializedName("index")
    private int index;
    @SerializedName("total")
    private int total;
    @SerializedName("value")
    private String value;
    @SerializedName("valueType")
    private String valueType;

    public static PsbtData objectFromData(String str) {

        return new Gson().fromJson(str, PsbtData.class);
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
