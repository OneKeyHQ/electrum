package org.haobtc.onekey.ui.dialog.custom;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.BottomPopupView;

import org.haobtc.onekey.R;

public class SelectWalletTypeDialog extends BottomPopupView {
    private LinearLayout recommendLayout, nativeLayout, normalLayout;
    private onClickListener onClickListener;
    public static final int RecommendType=0;
    public static final int NativeType=1;
    public static final int NormalType=2;
    private Context context;

    public SelectWalletTypeDialog (@NonNull Context context, onClickListener onClickListener) {
        super(context);
        this.onClickListener = onClickListener;
        this.context=context;
    }

    @Override
    protected void onCreate () {
        super.onCreate();
        recommendLayout = findViewById(R.id.recommend_layout);
        nativeLayout = findViewById(R.id.native_layout);
        normalLayout = findViewById(R.id.normal_layout);
        recommendLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                recommendLayout.setBackgroundColor(context.getColor(R.color.color_line));
                onClickListener.onClick(RecommendType);
                dismiss();
            }
        });
        nativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                nativeLayout.setBackgroundColor(context.getColor(R.color.color_line));
                onClickListener.onClick(NativeType);
                dismiss();
            }
        });
        normalLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                normalLayout.setBackgroundColor(context.getColor(R.color.color_line));
                onClickListener.onClick(NormalType);
                dismiss();
            }
        });
    }

    @Override
    protected int getImplLayoutId () {
        return R.layout.dialog_select_wallet_type;
    }

    public interface onClickListener {
        void onClick (int mode);

    }

}
