package org.haobtc.onekey.ui.dialog;

import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.AddXpubEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * @author liyan
 * @date 11/24/20
 */

public class AddXpubByHandDialog extends BaseDialogFragment {

    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.name_edit)
    EditText nameEdit;
    @BindView(R.id.xpub_edit)
    EditText xpubEdit;
    @BindView(R.id.add)
    Button add;
    private String name;
    private String xpub;
    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.bixinkey_input;
    }
    @SingleClick
    @OnClick({R.id.img_cancel, R.id.name_edit, R.id.xpub_edit, R.id.add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_cancel:
                dismiss();
                break;
            case R.id.add:

                if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(xpub)) {
                    Toast.makeText(MyApplication.getInstance(), "名称或扩展公钥为空", Toast.LENGTH_SHORT).show();
                } else {
                   if (PyEnv.validateXpub(xpub)) {
                        EventBus.getDefault().post(new AddXpubEvent(name, xpub));
                        dismiss();
                    } else {
                       Toast.makeText(MyApplication.getInstance(), "扩展公钥格式有误，请仔细确认", Toast.LENGTH_SHORT).show();
                   }
                }
                break;
        }
    }
    @OnTextChanged(value = {R.id.name_edit}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged1(Editable editable) {
                name = editable.toString();
    }
    @OnTextChanged(value = {R.id.xpub_edit}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(Editable editable) {
                xpub = editable.toString();
    }
}
