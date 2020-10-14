package org.haobtc.onekey.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;
@Deprecated
public class ImportHistryWalletBean {

    /**
     * xpubId : Vpub5jJrnF3Z5cLUA6Kj6NaV5yPkp1VqAKMj1N88GXvMy5qnyqYRLNTjmEivif7pDfHTRroaQSrDe3qubmT88a4SZRGzBUr71zq6ZkfWvdFbysK
     * walletId : 4961b853a48b35a5d6c761676961ec2ae12bf9b3f83b744057e680dc1a837d31
     * xpubs : ["[\"Vpub5jJrnF3Z5cLUA6Kj6NaV5yPkp1VqAKMj1N88GXvMy5qnyqYRLNTjmEivif7pDfHTRroaQSrDe3qubmT88a4SZRGzBUr71zq6ZkfWvdFbysK\""," \"Vpub5gyCX33B53xAyfEaH1Jfnp5grizbHfxVz6bWLPD92nLcbKMsQzSbM2eyGiK4qiRziuoRhoeVMoPLvEdfbQxGp88PN9cU6zupSSuiPi3RjEg\"]"]
     * walletType : 2-2
     */

    @SerializedName("xpubId")
    private String xpubId;
    @SerializedName("walletId")
    private String walletId;
    @SerializedName("walletType")
    private String walletType;
    @SerializedName("xpubs")
    private List<String> xpubs;

    public String getXpubId() {
        return xpubId;
    }

    public void setXpubId(String xpubId) {
        this.xpubId = xpubId;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public List<String> getXpubs() {
        return xpubs;
    }

    public void setXpubs(List<String> xpubs) {
        this.xpubs = xpubs;
    }
}
