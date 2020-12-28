package org.haobtc.onekey.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.CenterPopupView;

import org.haobtc.onekey.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressLint("ViewConstructor")
public class UnBackupTipDialog extends CenterPopupView {
    @BindView(R.id.text_tip)
    TextView textTip;
    @BindView(R.id.text_back)
    TextView textBack;
    @BindView(R.id.text_i_know)
    TextView textIKnow;
    private onClick onClick;
    private Unbinder bind;
    private String content;

    public UnBackupTipDialog(@NonNull Context context, String text, onClick onClick) {
        super(context);
        this.onClick = onClick;
        content = text;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        bind = ButterKnife.bind(this);
        textTip.setText(content);
        textIKnow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        textBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onClick.onBack();
            }
        });
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.unbackup_tip;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    public interface onClick {
        void onBack();
    }
}
