package org.haobtc.onekey.card.entries;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * @author liyan
 * @date 2020/7/18
 */
//
public class SecureChanelParam {

    /**
     * scpID : 1107
     * keyUsage : 3C
     * keyType : 88
     * keyLength : 16
     * hostID : 8080808080808080
     * crt : 7F2181E89310434552545F4150505F45434B41303031420D6A75626974657277616C6C65745F200D6A75626974657277616C6C6574950200805F2504202005255F2404202505245300BF200EEF0C8D0A820182028203820482057F4946B041048FD3FAB3907C5CC8CD193EB2B653EA179115B7F305C9E21DE6D29C0736A3B82025B219F24BDA86D80F5AE262521E124F4C6691A0C47B1FB72D95895E9312CB0DF001005F3746304402204D75EAA2F09604A9597DA905D680EB619B8ADCF080E5AD6950E1DBF26195C9E2022067649AFB4A8BC380B382520499C6F2BB350A8519B0ECDBE0B7374AA898826D0E
     * sk : B66BE8CB7512A6DFF741839EE8C5092D6987A5D7790E93B52EBB16FCD4EAD7AA
     */

    @SerializedName("scpID")
    private String scpID;
    @SerializedName("keyUsage")
    private String keyUsage;
    @SerializedName("keyType")
    private String keyType;
    @SerializedName("keyLength")
    private int keyLength;
    @SerializedName("hostID")
    private String hostID;
    @SerializedName("crt")
    private String crt;
    @SerializedName("sk")
    private String sk;

    public static SecureChanelParam objectFromData(String str) {

        return new Gson().fromJson(str, SecureChanelParam.class);
    }

    public String getScpID() {
        return scpID;
    }

    public void setScpID(String scpID) {
        this.scpID = scpID;
    }

    public String getKeyUsage() {
        return keyUsage;
    }

    public void setKeyUsage(String keyUsage) {
        this.keyUsage = keyUsage;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public String getCrt() {
        return crt;
    }

    public void setCrt(String crt) {
        this.crt = crt;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }
}
