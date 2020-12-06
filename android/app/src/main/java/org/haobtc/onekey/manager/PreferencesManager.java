package org.haobtc.onekey.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * @author tiger
 */
public class PreferencesManager {

    /**
     * commit
     * @param editor
     */
    private static void commit(SharedPreferences.Editor editor){
            editor.commit();
    }


    private static SharedPreferences getSharedPreferences(Context context,String name){
        return context.getSharedPreferences(name,
                Context.MODE_PRIVATE);
    }

    /**
     * save
     * @param context
     * @param name
     * @param key
     * @param object
     */
    public static void put(Context context,String name, String key, Object object) {
        if (context == null || object == null) {
            return;
        }
        SharedPreferences.Editor editor = getSharedPreferences(context,name).edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        commit(editor);
    }

    /**
     * get
     * @param context
     * @param name
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String name, String key, Object defaultObject) {
        if (context == null) {
            return null;
        }
        SharedPreferences sp = getSharedPreferences(context,name);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * remove key
     * @param context
     * @param name
     * @param key
     */
    public static void remove(Context context,String name, String key) {
        SharedPreferences.Editor editor = getSharedPreferences(context,name).edit();
        editor.remove(key);
        commit(editor);
    }

    /**
     * clear
     * @param context
     * @param name
     */
    public static void clear(Context context,String name) {
        SharedPreferences.Editor editor = getSharedPreferences(context,name).edit();
        editor.clear();
        commit(editor);
    }

    /**
     * contains
     * @param context
     * @param name
     * @param key
     * @return
     */
    public static boolean contains(Context context, String name,String key) {
        SharedPreferences sp = getSharedPreferences(context,name);
        return sp.contains(key);
    }

    /**
     * remove all
     * @param context
     * @param name
     * @return
     */
    public static Map<String, ?> getAll(Context context,String name) {
        SharedPreferences sp = getSharedPreferences(context,name);
        return sp.getAll();
    }

}