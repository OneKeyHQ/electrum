package org.haobtc.onekey.onekeys.dappbrowser.bean;

import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.haobtc.onekey.onekeys.dappbrowser.utils.StyledStringBuilder;
import org.web3j.crypto.Hash;

public class CryptoFunctions implements CryptoFunctionsInterface {

    @Override
    public byte[] Base64Decode(String message) {
        return Base64.decode(message, Base64.URL_SAFE);
    }

    @Override
    public byte[] Base64Encode(byte[] data) {
        return Base64.encode(data, Base64.URL_SAFE | Base64.NO_WRAP);
    }

    @Override
    public byte[] keccak256(byte[] message) {
        return Hash.sha3(message);
    }

    @Override
    public CharSequence formatTypedMessage(ProviderTypedData[] rawData) {
        return StyledStringBuilder.formatTypedMessage(rawData);
    }

    @Override
    public CharSequence formatEIP721Message(String messageData) {
        CharSequence msgData = "";
        try {
            StructuredDataEncoder eip721Object = new StructuredDataEncoder(messageData);
            msgData = formatEIP721Message(eip721Object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return msgData;
    }

    @Override
    public byte[] getStructuredData(String messageData) {
        try {
            StructuredDataEncoder eip721Object = new StructuredDataEncoder(messageData);
            return eip721Object.getStructuredData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    private static CharSequence formatEIP721Message(StructuredDataEncoder messageData) {
        HashMap<String, Object> messageMap =
                (HashMap<String, Object>) messageData.jsonMessageObject.getMessage();
        StyledStringBuilder sb = new StyledStringBuilder();
        for (String entry : messageMap.keySet()) {
            sb.startStyleGroup().append(entry).append(":").append("\n");
            sb.setStyle(new StyleSpan(Typeface.BOLD));
            Object v = messageMap.get(entry);
            if (v instanceof LinkedHashMap) {
                HashMap<String, Object> valueMap = (HashMap<String, Object>) messageMap.get(entry);
                for (String paramName : valueMap.keySet()) {
                    String value = valueMap.get(paramName).toString();
                    sb.startStyleGroup().append(" ").append(paramName).append(": ");
                    sb.setStyle(new StyleSpan(Typeface.BOLD));
                    sb.append(value).append("\n");
                }
            } else {
                sb.append(" ").append(v.toString()).append("\n");
            }
        }

        sb.applyStyles();

        return sb;
    }
}
