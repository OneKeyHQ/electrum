package org.haobtc.onekey.ui.dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;

import org.haobtc.onekey.R;

/**
 * @Description: java类作用描述
 * @Author: peter Qin
 * @CreateDate: 2020/12/21$ 9:25 PM$
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/12/21$ 9:25 PM$
 * @UpdateRemark: 更新说明：
 */
public class ReStartDialog extends CenterPopupView {
    TextView confirmTV;
    private onConfirmClick onConfirmClick;

    public ReStartDialog (@NonNull Context context, onConfirmClick onConfirmClick) {
        super(context);
        this.onConfirmClick = onConfirmClick;
    }

    @Override
    protected void onCreate () {
        super.onCreate();
        confirmTV = findViewById(R.id.confirm_tv);
        confirmTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                onConfirmClick.onConfirm();
            }
        });
    }

    @Override
    protected int getImplLayoutId () {
        return R.layout.dialog_restart;
    }



    public interface onConfirmClick {
        void onConfirm ();

    }

}
