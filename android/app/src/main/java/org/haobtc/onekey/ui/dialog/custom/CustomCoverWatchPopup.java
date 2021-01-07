package org.haobtc.onekey.ui.dialog.custom;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.common.base.Strings;
import com.lxj.xpopup.core.BottomPopupView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.ui.widget.SuperTextView;

/**
 * @Description: 覆盖观察钱包的弹窗，后期相同弹框可以再扩展复用
 * @Author: peter Qin
 * @CreateDate: 2021/1/11 8:08 PM
 */
public class CustomCoverWatchPopup extends BottomPopupView {
    private onClick onClick;
    private SuperTextView confirmBtn, cancelBtn;
    private TextView title, content;
    private int mode;
    public static final int deleteWatch = 0;
    private String walletName;
    private Context context;

    public CustomCoverWatchPopup (@NonNull Context context, onClick onClick, int mode) {
        super(context);
        this.onClick = onClick;
        this.mode = mode;
        this.context = context;
    }

    public void setWalletName (String walletName) {
        this.walletName = walletName;
    }

    @Override
    protected void onCreate () {
        super.onCreate();
        confirmBtn = findViewById(R.id.confirm_button);
        cancelBtn = findViewById(R.id.cancel_button);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        switch (mode) {
            case deleteWatch:
                title.setText(R.string.delete_watch);
                if (!Strings.isNullOrEmpty(walletName))
                    content.setText(context.getString(R.string.delete_watch_tip, walletName));
                confirmBtn.setText(R.string.cover_watch);
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
    protected int getImplLayoutId () {
        return R.layout.app_confirm_dialog_green;
    }

    public interface onClick {
        void onConfirm ();

    }

}
