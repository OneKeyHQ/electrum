package org.haobtc.onekey.activities.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.service.CommunicationModeSelector;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.event.FixAllLabelnameEvent;
import org.haobtc.onekey.event.FixBixinkeyNameEvent;

import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FixBixinkeyNameActivity extends BaseActivity {

    public static final String TAG = FixBixinkeyNameActivity.class.getSimpleName();
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.name_edit)
    EditText nameEdit;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.btn_next)
    Button btnNext;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fix_bixinkey_name;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {
        nameEditStyle();
    }

    private void nameEditStyle() {
        nameEdit.addTextChangedListener(new TextWatcher() {
            CharSequence input;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                input = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                number.setText(String.format(Locale.CHINA, "%d/16", input.length()));
                if (input.length() > 15) {
                    mToast(getString(R.string.moreinput_text_fixbixinkey));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    btnNext.setEnabled(true);
                    btnNext.setBackground(getDrawable(R.drawable.button_bk));
                } else {
                    btnNext.setEnabled(false);
                    btnNext.setBackground(getDrawable(R.drawable.button_bk_grey));
                }
            }
        });
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_next:
                CommunicationModeSelector.runnables.clear();
                CommunicationModeSelector.runnables.add(null);
                Intent intent = new Intent(this, CommunicationModeSelector.class);
                intent.putExtra("tag", TAG);
                intent.putExtra("fixName", nameEdit.getText().toString());
                startActivity(intent);
                break;
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showReading(FixAllLabelnameEvent event) {
        String oldKey_device_id = event.getKeyName();
        String code = event.getCode();
        if ("1".equals(code)) {
            SharedPreferences devices = getSharedPreferences("devices", Context.MODE_PRIVATE);
            Map<String, ?> devicesAll = devices.getAll();
            //key
            for (Map.Entry<String, ?> entry : devicesAll.entrySet()) {
                String mapValue = (String) entry.getValue();
                HardwareFeatures hardwareFeaturesfix = new Gson().fromJson(mapValue, HardwareFeatures.class);
                if (oldKey_device_id.equals(hardwareFeaturesfix.getDeviceId())) {
                    hardwareFeaturesfix.setLabel(nameEdit.getText().toString());
                    devices.edit().putString(oldKey_device_id, hardwareFeaturesfix.toString()).apply();
                }
            }
            EventBus.getDefault().post(new FixBixinkeyNameEvent(nameEdit.getText().toString()));
            Toast.makeText(this, getString(R.string.fix_success), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
