package org.haobtc.onekey.ui.dialog;

import android.view.View;
import android.widget.Toast;

import com.chaquo.python.Kwarg;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.LoadOtherWalletEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.ui.base.BaseDialogFragment;
import org.haobtc.onekey.utils.Daemon;

import butterknife.OnClick;

public class DeleteWatchWalletDialog extends BaseDialogFragment {
    @Override
    public int getContentViewId() {
        return R.layout.delete_wallet;
    }

    @OnClick({R.id.tet_ConfirmDelete, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tet_ConfirmDelete:
                // delete  watch wallet
                deleteWatchWallet();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    private void deleteWatchWallet() {
        String keyName = PreferencesManager.get(getActivity(), "Preferences", Constant.CURRENT_SELECTED_WALLET_NAME, "").toString();
        try {
            Daemon.commands.callAttr("delete_wallet", "111111", new Kwarg("name", keyName));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getActivity(), getString(R.string.delete_succse), Toast.LENGTH_SHORT).show();
        PreferencesManager.remove(getActivity(), Constant.WALLETS, keyName);
        EventBus.getDefault().post(new LoadOtherWalletEvent());
        EventBus.getDefault().post(new SecondEvent("finish"));
    }
}
