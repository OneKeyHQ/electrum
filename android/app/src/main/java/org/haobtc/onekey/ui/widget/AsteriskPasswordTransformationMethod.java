package org.haobtc.onekey.ui.widget;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/** @Description: 设置EditText 展示为 * @Author: peter Qin */
public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }

        public char charAt(int index) {
            return '•'; // This is the important part
        }

        public int length() {
            return mSource.length(); // Return default
        }

        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }
}
