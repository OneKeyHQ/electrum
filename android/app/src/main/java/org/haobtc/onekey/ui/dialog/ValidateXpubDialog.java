package org.haobtc.onekey.ui.dialog;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.AddXpubEvent;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * @author liyan
 * @date 11/24/20
 */

public class ValidateXpubDialog extends BaseDialogFragment {
    @BindView(R.id.name)
    EditText nameEdit;
    @BindView(R.id.xpub_info)
    TextView xpubInfo;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    private String xpub;
    private String name;

    public ValidateXpubDialog(@NonNull String str) {
        this.xpub = str;
    }

    @Override
    public void init() {
        xpubInfo.setText(xpub);
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.bixinkey_confirm;
    }

    @SingleClick
    @OnClick({R.id.btn_confirm, R.id.img_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                if (Strings.isNullOrEmpty(name)) {
                    Toast.makeText(MyApplication.getInstance(), "名字不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    EventBus.getDefault().post(new AddXpubEvent(name, xpub));
                    dismiss();
                }
                break;
            case R.id.img_cancel:
                dismiss();
        }

    }

    @OnTextChanged(value = R.id.name)
    public void onTextChanged() {
        name = nameEdit.getText().toString();
    }
}
