package org.haobtc.onekey.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @Description: 键盘管理类
 * @Author: peter Qin
 * @CreateDate: 2020/12/16$ 9:58 AM$
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/16$ 9:58 AM$
 * @UpdateRemark: 更新说明：
 */
public class KeyBoardUtils {

    public static void showKeyBoard(Context context, EditText editText) {
        InputMethodManager inputManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    public static void hideKeyBoard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
