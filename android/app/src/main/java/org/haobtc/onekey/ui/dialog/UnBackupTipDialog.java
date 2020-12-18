package org.haobtc.onekey.ui.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class UnBackupTipDialog extends BaseDialogFragment {
    @BindView(R.id.text_tip)
    TextView textTip;

    @Override
    public int getContentViewId() {
        return R.layout.unbackup_tip;
    }

    @Override
    public void init() {
        super.init();
        Bundle bundle = getArguments();
        assert bundle != null;
        String unBackupTip = bundle.getString(Constant.UN_BACKUP_TIP);
        textTip.setText(unBackupTip);
    }

    @OnClick({R.id.text_back, R.id.text_i_know})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_back:
                dismiss();
                EventBus.getDefault().post(new SecondEvent("finish"));
                break;
            case R.id.text_i_know:
                dismiss();
                break;
        }
    }

    @Override
    public boolean requireGravityCenter() {
        return true;
    }
}
