package org.haobtc.onekey.activities.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.event.HandlerEvent;
import org.haobtc.onekey.event.SetShutdownTimeEvent;
import org.haobtc.onekey.event.ShutdownTimeEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;

public class SetShutdownTimeActivity extends BaseActivity implements TextWatcher {

    public static final String TAG = SetShutdownTimeActivity.class.getSimpleName();
    @BindView(R.id.time_edit)
    EditText timeEdit;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private SharedPreferences preferences;
    private String device_id;
    private String bleName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_shutdown_time;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        EventBus.getDefault().register(this);
        timeEdit.addTextChangedListener(this);
        device_id = getIntent().getStringExtra("device_id");
    }

    @Override
    public void initData() {
        bleName = getIntent().getStringExtra("ble_name");
    }

    @OnClick({R.id.img_back, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_confirm:
                String time = timeEdit.getText().toString();
                if (TextUtils.isEmpty(time)) {
                    mToast(getString(R.string.please_input_time));
                    return;
                }
                int shutdownTime = Integer.parseInt(time);
                if (shutdownTime < 10) {
                    mToast(getString(R.string.little_time));
                    return;
                }
                if (Ble.getInstance().getConnetedDevices().size() != 0) {
                    if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                        EventBus.getDefault().postSticky(new HandlerEvent());
                    }
                }
                CommunicationModeSelector.runnables.clear();
                Intent intent = new Intent(this, CommunicationModeSelector.class);
                intent.putExtra("tag", TAG);
                intent.putExtra("shutdown_time", time);
                startActivity(intent);
                break;
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showReading(ShutdownTimeEvent event) {
        String result = event.getResult();
        if ("1".equals(result)) {
            EventBus.getDefault().post(new SetShutdownTimeEvent(timeEdit.getText().toString()));
            preferences.edit().putString(device_id, timeEdit.getText().toString()).apply();
            timeEdit.setText("");
            mToast(getString(R.string.set_success));
        } else {
            mToast(getString(R.string.set_fail));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            btnConfirm.setEnabled(true);
            btnConfirm.setBackground(getDrawable(R.drawable.button_bk));
        } else {
            btnConfirm.setEnabled(false);
            btnConfirm.setBackground(getDrawable(R.drawable.button_bk_grey));
        }
    }
}
