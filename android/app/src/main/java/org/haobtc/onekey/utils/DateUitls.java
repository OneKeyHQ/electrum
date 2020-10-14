package org.haobtc.onekey.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/23.
 */
@SuppressLint("SimpleDateFormat")
public class DateUitls {
    private static SimpleDateFormat sss;
    private static SimpleDateFormat yyyyMMdd;

    //    pu SimpleDateFormat sss = null;
    /*获取系统时间 格式为："yyyy/MM/dd "*/
    public static String getCurrentDate() {
        Date d = new Date();
        sss = new SimpleDateFormat("yyyy年MM月dd日");
        return sss.format(d);
    }


    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time);
        sss = new SimpleDateFormat("yyyy年MM月dd日");
        return sss.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToStringX(long time) {
        Date d = new Date(time);
        sss = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sss.format(d);
    }

    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        sss = new SimpleDateFormat("yyyy/M/d HH:mm");
        Date date = new Date();
        try {
            date = sss.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }

}
