package org.haobtc.onekey.bean;

import com.google.gson.annotations.SerializedName;

public class DefaultNodeBean {

    /**
     * host : 39.97.224.50
     * port : 51002
     */

    @SerializedName("host")
    private String host;
    @SerializedName("port")
    private String port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
