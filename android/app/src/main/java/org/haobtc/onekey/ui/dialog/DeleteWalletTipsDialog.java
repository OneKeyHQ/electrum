package org.haobtc.onekey.ui.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.Kwarg;
import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.LoadOtherWalletEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseDialogFragment;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/20/20
 */

public class DeleteWalletTipsDialog extends BaseDialogFragment {
    @BindView(R.id.content)
    TextView content;
    private int type;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.delete_wallet_dialog;
    }

    @Override
    public void init() {
        Bundle args = getArguments();
        if (args != null) {
            type =  args.getInt(Constant.WALLET_TYPE, 0);
        }
        if (type == 1) {
            content.setText(R.string.delete_watch_wallet_warning);
        }
    }

    @OnClick({R.id.confirm_button, R.id.cancel_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm_button:
                deleteWatchWallet();
                break;
            case R.id.cancel_button:
                dismiss();
                break;
        }
    }
    private void deleteWatchWallet() {
        String keyName = PreferencesManager.get(requireActivity(), "Preferences", Constant.CURRENT_SELECTED_WALLET_NAME, "").toString();
        PyResponse<Void> response = PyEnv.deleteWallet("111111", keyName);
        String error = response.getErrors();
        if (Strings.isNullOrEmpty(error)) {
            PreferencesManager.remove(getContext(), Constant.WALLETS, keyName);
            EventBus.getDefault().post(new LoadOtherWalletEvent());
            dismiss();
            requireActivity().finish();
            Toast.makeText(getContext(), R.string.delete_succse, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        }
    }
}
