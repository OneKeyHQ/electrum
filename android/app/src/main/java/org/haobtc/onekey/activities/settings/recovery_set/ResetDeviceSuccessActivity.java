package org.haobtc.onekey.activities.settings.recovery_set;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.FinishEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.ble;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.nfc;
import static org.haobtc.onekey.activities.service.CommunicationModeSelector.protocol;

public class ResetDeviceSuccessActivity extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.btn_finish)
    Button btnFinish;

    @Override
    public int getLayoutId() {
        return R.layout.reset_devcie_successful;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.btn_finish:
                ble.put("IS_CANCEL", true);
                nfc.put("IS_CANCEL", true);
                protocol.callAttr("notify");
                EventBus.getDefault().post(new ExitEvent());
                EventBus.getDefault().post(new FinishEvent());
                finish();
                break;
            default:
        }
    }
}
