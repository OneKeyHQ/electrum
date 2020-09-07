package org.haobtc.keymanager.utils;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.haobtc.keymanager.R;

import java.lang.reflect.Field;

public class CommonUtils {

    public static void enableToolBar(AppCompatActivity appCompatActivity, int resid) {
        TextView textView = appCompatActivity.findViewById(R.id.title_template);
        if (resid == 0) {
            textView.setText("");
        } else {
            textView.setText(resid);
        }
        // my_child_toolbar is defined in the layout file
        appCompatActivity.setSupportActionBar(appCompatActivity.findViewById(R.id.tb));

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = appCompatActivity.getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static void setNumberPickerDividerColor(AppCompatActivity appCompatActivity, NumberPicker numberPicker) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    pf.set(numberPicker, new ColorDrawable(appCompatActivity.getColor(R.color.separation_line)));
                } catch (IllegalArgumentException | Resources.NotFoundException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        for (Field pf2 : pickerFields) {
            if (pf2.getName().equals("mSelectionDividerHeight")) {
                pf2.setAccessible(true);
                try {
                    int result = 3;
                    pf2.set(numberPicker, result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }
    public static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }
        return sb.toString();

    }
}
