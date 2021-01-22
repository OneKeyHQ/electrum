package org.haobtc.onekey.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

/**
 * @Description: 可以监听粘贴的输入框
 * @Author: peter Qin
 */
public class PasteEditText extends AppCompatEditText {
    private OnPasteCallback mOnPasteCallback;
    public PasteEditText(@NonNull Context context) {
        super(context);
    }

    public PasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        switch (id) {
            case android.R.id.paste:
                if (mOnPasteCallback != null) {
                    mOnPasteCallback.onPaste();
                }
                break;
        }
        return super.onTextContextMenuItem(id);
    }

    public void setOnPasteCallback(OnPasteCallback onPasteCallback) {
        this.mOnPasteCallback = onPasteCallback;
    }

    public interface OnPasteCallback {
        void onPaste();
    }
}
