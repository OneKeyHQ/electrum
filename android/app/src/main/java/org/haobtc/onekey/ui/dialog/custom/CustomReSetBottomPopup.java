package org.haobtc.onekey.ui.dialog.custom;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.BottomPopupView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.ui.widget.SuperTextView;

public class CustomReSetBottomPopup extends BottomPopupView {
    private onClick onClick;
    private SuperTextView confirmBtn, cancelBtn;
    private TextView title, content;
    private int mode;
    public static final int resetApp = 0;
    public static final int deleteHdChildren = 1;
    public static final int deleteHdRoot =2;

    public CustomReSetBottomPopup (@NonNull Context context, onClick onClick, int mode) {
        super(context);
        this.onClick = onClick;
        this.mode = mode;
    }

    @Override
    protected void onCreate () {
        super.onCreate();
        confirmBtn = findViewById(R.id.confirm_button);
        cancelBtn = findViewById(R.id.cancel_button);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        switch (mode) {
            case resetApp:
                title.setText(R.string.confirm_do_this);
                content.setText(R.string.reset_tip);
                confirmBtn.setText(R.string.confirm_reset);
                break;
            case deleteHdChildren:
                title.setText(R.string.delete_wallet_single);
                content.setText(R.string.delete_wallet_single_tip);
                confirmBtn.setText(R.string.delete_this_wallet);
                break;
            case deleteHdRoot:
                title.setText(R.string.delete_hd_derived);
                content.setText(R.string.delete_dh_derived_tip);
                confirmBtn.setText(R.string.delete_hd_derived_confirm);
                break;
            default:
                break;
        }
        confirmBtn.setOnClickListener(v -> {
            onClick.onConfirm();
            dismiss();
        });
        cancelBtn.setOnClickListener(v -> dismiss());
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.reset_app_confirm_dialog;
    }

    public interface onClick {
        void onConfirm();
    }

}
