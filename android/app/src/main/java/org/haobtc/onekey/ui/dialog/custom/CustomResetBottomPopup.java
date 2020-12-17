package org.haobtc.onekey.ui.dialog.custom;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.BottomPopupView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.ui.widget.SuperTextView;

/**
 * @Description: java类作用描述
 * @Author: peter Qin
 * @CreateDate: 2020/12/16$ 5:55 PM$
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/16$ 5:55 PM$
 * @UpdateRemark: 更新说明：
 */
public class CustomResetBottomPopup extends BottomPopupView {

    private onClick onClick;
    private SuperTextView confirmBtn, cancelBtn;

    public CustomResetBottomPopup(@NonNull Context context, onClick onClick) {
        super(context);
        this.onClick = onClick;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        confirmBtn = findViewById(R.id.confirm_button);
        cancelBtn = findViewById(R.id.cancel_button);
        confirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onConfirm();
                dismiss();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.reset_app_confirm_dialog;
    }

    public interface onClick {
        void onConfirm();
    }
}
