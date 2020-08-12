package org.haobtc.wallet.activities.settings.fixpin;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.activities.service.NfcNotifyHelper;
import org.haobtc.wallet.aop.SingleClick;
import org.haobtc.wallet.card.BackupHelper;
import org.haobtc.wallet.event.ExitEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.wallet.activities.service.CommunicationModeSelector.isNFC;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.nfc;
import static org.haobtc.wallet.activities.service.CommunicationModeSelector.protocol;

public class ConfirmActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    @BindView(R.id.promote_message)
    TextView promoteMessage;
    private String tag;

    @Override
    public int getLayoutId() {
        return R.layout.active_successful;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        tag = getIntent().getStringExtra("tag");
        if (ChangePinProcessingActivity.TAG.equals(tag)) {
            promoteMessage.setText(R.string.pin_change_confirm);
        } else if ("set_pin".equals(tag)|| BackupHelper.TAG.equals(tag)) {
            promoteMessage.setText(R.string.set_pin_confirm);
        }
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                if (isNFC) {
                    protocol.callAttr("notify");
                    new Handler().postDelayed(() -> nfc.put("IS_CANCEL", true), 2);
                }
                EventBus.getDefault().post(new ExitEvent());
                finish();
                break;
            case R.id.btn_finish:
                if (isNFC) {
                    protocol.callAttr("notify");
                    if (!BackupHelper.TAG.equals(tag)) {
                        new Handler().postDelayed(() -> nfc.put("IS_CANCEL", true), 2);
                        EventBus.getDefault().post(new ExitEvent());
                    } else {
                        startActivity(new Intent(this, NfcNotifyHelper.class));
                    }
                }
                finish();
                break;
            default:
        }
    }
}
