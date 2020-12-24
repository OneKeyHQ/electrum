package org.haobtc.onekey.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.haobtc.onekey.R;

/**
 * Created by 小米粒 on 2018/11/5.
 */

public class MyDialog extends Dialog {
    private Context context;
    private static MyDialog dialog;
    private ImageView ivProgress;

    public MyDialog(Context context) {
        super(context);
        this.context = context;
    }

    private MyDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    //check dialog
    public static MyDialog showDialog(Context context) {
        dialog = new MyDialog(context, R.style.MyDialog);//dialog style
        dialog.setContentView(R.layout.dialog_layout);//dialog view file
        return dialog;
    }

    //onclick out don't close dialog
    public void onTouchOutside(boolean flag) {
        dialog.setCanceledOnTouchOutside(flag);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && dialog != null) {
            ivProgress = (ImageView) dialog.findViewById(R.id.ivProgress);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.dialog_progress_anim);
            ivProgress.startAnimation(animation);
        }
    }
}
