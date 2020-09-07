package org.haobtc.keymanager.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * @author liyan
 */
public class JsonParseUtils {

    public static String getJsonStr(Context context, String name) {
        AssetManager assets = context.getAssets();
        String jsonStr = "";
        try {
            InputStream open;
            open = assets.open(name);

            Reader reader = new InputStreamReader(open, StandardCharsets.UTF_8);
            int ch = 0;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            open.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}