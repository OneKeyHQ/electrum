package org.haobtc.onekey.ui.dialog;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * @author liyan
 * @date 12/11/20
 */

public class PassInputDialog extends BaseDialogFragment {

    @BindView(R.id.cancel_select_wallet)
    ImageView cancelSelectWallet;
    @BindView(R.id.edit_password)
    EditText editPassword;
    @BindView(R.id.btn_enter_wallet)
    Button btnEnterWallet;
    private String password;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.input_wallet_pass;
    }

    @OnClick({R.id.cancel_select_wallet, R.id.btn_enter_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel_select_wallet:
                dismiss();
                break;
            case R.id.btn_enter_wallet:
                EventBus.getDefault().post(new GotPassEvent(password));
                dismiss();
                break;
        }
    }
    @OnTextChanged(value = R.id.edit_password, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChange() {
        password = editPassword.getText().toString();
        if(Strings.isNullOrEmpty(password)) {
            btnEnterWallet.setEnabled(false);
        } else {
            btnEnterWallet.setEnabled(true);
        }
    }

    @Override
    public boolean requireGravityCenter() {
        return true;
    }
}
