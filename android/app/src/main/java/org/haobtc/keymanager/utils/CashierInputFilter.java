package org.haobtc.keymanager.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CashierInputFilter implements InputFilter {
    Pattern mPattern;

    private static final int MAX_VALUE = Integer.MAX_VALUE;

    private static final int POINTER_LENGTH = 2;

    private static final String POINTER = ".";

    private static final String ZERO = "0";

    public CashierInputFilter() {
        mPattern = Pattern.compile("([0-9]|\\.)*");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        String destText = dest.toString();

        //Verify delete etc
        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }

        Matcher matcher = mPattern.matcher(source);
        //Only numbers can be entered when decimal point has been entered
        if(destText.contains(POINTER)) {
            if (!matcher.matches()) {
                return "";
            } else {
                if (POINTER.equals(source.toString())) {  //Only one decimal point can be entered
                    return "";
                }
            }

            //Verify the precision of decimal point to ensure that only two digits can be entered after the decimal point
            int index = destText.indexOf(POINTER);
            int length = dend - index;

            if (length > POINTER_LENGTH) {
                return dest.subSequence(dstart, dend);
            }
        } else {
            /**
             * If no decimal point is entered, only decimal point and number can be entered
             * 1. Decimal point cannot be entered in the first place
             * 2. If you enter 0 in the first place, you can only enter the decimal point next
             */
            if (!matcher.matches()) {
                return "";
            } else {
                if ((POINTER.equals(source.toString())) && TextUtils.isEmpty(destText)) {  //The first decimal point cannot be entered
                    return "";
                } else if (!POINTER.equals(source.toString()) && ZERO.equals(destText)) { //If you enter 0 in the first place, you can only enter the decimal point next
                    return "";
                }
            }
        }

        //Verify the size of the input amount
        double sumText = Double.parseDouble(destText + sourceText);
        if (sumText > MAX_VALUE) {
            return dest.subSequence(dstart, dend);
        }

        return dest.subSequence(dstart, dend) + sourceText;
    }
}