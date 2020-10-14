package org.haobtc.onekey.card.entries;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author  liyan
 * @date 2020/7/15
 */
//
public class CardResponse {

    /**
     * response : bf2181dc7f2181d8931042584e46433230303532353030303031420d6a75626974657277616c6c65745f200d6a75626974657277616c6c65749501825f2504202005255f24042025052453007f4946b0410479704bdb2d3da2e547eb6de66e0073f6e61ae32076af007973b5fa1dbe07e0ef38bd84d85f1fe1e1410ff743e659691b36361c76bee2fac44fd88825759268cef001005f37483046022100b076674c9f0ea1ddee84517e2a53cb392ac2c8b25ca3a7d56558570a051737020221008a982e267ffcef5309a272ea492be489a233381c477e8803034a8f6789f2bbd9
     * wRet : 36864
     */

    @SerializedName(value = "response", alternate = "value")
    private String response;
    @SerializedName(value = "wRet", alternate = "tag")
    private int wRet;

    public static CardResponse objectFromData(String str) {

        return new Gson().fromJson(str, CardResponse.class);
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getWRet() {
        return wRet;
    }

    public void setWRet(int wRet) {
        this.wRet = wRet;
    }
}
