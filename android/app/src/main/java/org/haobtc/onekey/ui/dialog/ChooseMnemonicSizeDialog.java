package org.haobtc.onekey.ui.dialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.MnemonicSizeSelectedEvent;
import org.haobtc.onekey.mvp.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/19/20
 */
//
public class ChooseMnemonicSizeDialog extends BaseDialogFragment {

    @BindView(R.id.size_12)
    TextView size12;
    @BindView(R.id.size_24)
    TextView size24;
    @BindView(R.id.close)
    ImageView close;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.dialog_mnemonic_size_selector;
    }

    @OnClick({R.id.size_12, R.id.size_24,R.id.close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.size_12:
                EventBus.getDefault().post(new MnemonicSizeSelectedEvent(true));
                dismiss();
                break;
            case R.id.size_24:
                EventBus.getDefault().post(new MnemonicSizeSelectedEvent(false));
                dismiss();
                break;
            case R.id.close:
                dismiss();
        }
    }
}
