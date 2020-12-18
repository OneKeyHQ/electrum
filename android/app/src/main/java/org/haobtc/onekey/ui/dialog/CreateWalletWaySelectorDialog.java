package org.haobtc.onekey.ui.dialog;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.onekeys.dialog.recovery.ImprotSingleActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateWalletChooseTypeActivity;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/18/20
 */

public class CreateWalletWaySelectorDialog extends BaseDialogFragment {
    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.btn_create)
    Button btnNext;
    @BindView(R.id.btn_import)
    Button btnImport;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.add_wallet;
    }

    @OnClick({R.id.img_cancel, R.id.btn_create, R.id.btn_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_cancel:
                dismiss();
                break;
            case R.id.btn_create:
                Intent intent = new Intent(getContext(), CreateWalletChooseTypeActivity.class);
                intent.putExtra("ifHaveHd", (boolean)PreferencesManager.get(getContext(), "Preferences", Constant.HAS_LOCAL_HD, false));
                startActivity(intent);
                dismiss();
                break;
            case R.id.btn_import:
                Intent intent1 = new Intent(getContext(), ImprotSingleActivity.class);
                startActivity(intent1);
                dismiss();
                break;
        }
    }
}
