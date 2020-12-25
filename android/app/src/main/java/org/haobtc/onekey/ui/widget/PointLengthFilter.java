package org.haobtc.onekey.ui.widget;
import android.text.InputFilter;
import android.text.Spanned;

public class PointLengthFilter implements InputFilter {
    /**
     * 输入框小数的位数  法币 2位，BTC：8位，ETH：6位，ERC-20Token:4 位
     */
    private int decimalDigits = 8;
    private onMaxListener onMaxListener;

    public PointLengthFilter (int decimalDigits, onMaxListener onMaxListener) {
        this.decimalDigits = decimalDigits;
        this.onMaxListener = onMaxListener;
    }

    @Override
    public CharSequence filter (CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // 删除等特殊字符，直接返回
        if ("".equals(source.toString())) {
            return null;
        }
        String dValue = dest.toString();
        String[] splitArray = dValue.split("\\.");
        if (splitArray.length > 1) {
            String dotValue = splitArray[1];
            int diff = dotValue.length() + 1 - decimalDigits;
            if (diff > 0) {
                onMaxListener.onMax(decimalDigits);
                return source.subSequence(start, end - diff);
            }
        }
        return null;
    }

    public interface onMaxListener {
        void onMax (int maxNum);

    }

}

