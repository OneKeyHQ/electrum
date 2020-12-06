package org.haobtc.onekey.ui.dialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.DeviceSearchEvent;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/24/20
 */

public class ChooseAddXpubWayDialog extends BaseDialogFragment {

    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.add_by_hardware)
    RelativeLayout addByHardware;
    @BindView(R.id.add_by_hand)
    RelativeLayout addByHand;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.add_public_dialog;
    }

    @SingleClick
    @OnClick({R.id.img_cancel, R.id.add_by_hardware, R.id.add_by_hand})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_cancel:
                dismiss();
                break;
            case R.id.add_by_hardware:
                EventBus.getDefault().post(new DeviceSearchEvent());
                dismiss();
                break;
            case R.id.add_by_hand:
                new AddXpubByHandDialog().show(getChildFragmentManager(), "");
                break;
        }
    }
}
