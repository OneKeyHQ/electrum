package org.haobtc.keymanager.activities.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.event.EditWhiteListEvent;
import org.haobtc.keymanager.event.FastPayEvent;
import org.haobtc.keymanager.event.HandlerEvent;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;

public class ConfidentialPaymentSettings extends BaseActivity {

    public static final String TAG = ConfidentialPaymentSettings.class.getSimpleName();
    public static final String TAG_EDIT_WHITE_LIST = "EditWhiteList";
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.switch_noPin)
    Switch switchNoPin;
    @BindView(R.id.switch_noHard)
    Switch switchNoHard;
    @BindView(R.id.unclassified_pay)
    EditText unclassifiedPay;
    @BindView(R.id.pay_unit)
    TextView payUnit;
    @BindView(R.id.edit_times)
    EditText editTimes;
    @BindView(R.id.btn_set_key)
    Button btnSetKey;
    private String noPIN = "false";
    private String noHard = "false";
    private String base_unit;
    private BigDecimal limit;
    private SharedPreferences preferences;
    private SharedPreferences.Editor edit;
    private String bleName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_no_secret_payment;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        boolean boPIN_set = preferences.getBoolean("boPIN_set", false);
        boolean noHard_set = preferences.getBoolean("noHard_set", false);
        base_unit = preferences.getString("base_unit", "mBTC");
        bleName = getIntent().getStringExtra("ble_name");
        payUnit.setText(base_unit);
        if (boPIN_set) {
            switchNoPin.setChecked(true);
            noPIN = "true";
        } else {
            switchNoPin.setChecked(false);
            noPIN = "false";
        }
        if (noHard_set) {
            switchNoHard.setChecked(true);
            noHard = "true";
        } else {
            switchNoHard.setChecked(false);
            noHard = "false";
        }
        TextWatcher1 textWatcher1 = new TextWatcher1();
        unclassifiedPay.addTextChangedListener(textWatcher1);
        editTimes.addTextChangedListener(textWatcher1);
    }

    @Override
    public void initData() {
        switchNoPinStatus();
    }

    private void switchNoPinStatus() {
        switchNoPin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    noPIN = "true";
                } else {
                    noPIN = "false";
                }
            }
        });
        switchNoHard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    noHard = "true";
                } else {
                    noHard = "false";
                }
            }
        });
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_set_key, R.id.test_edit_whitelist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_set_key:
                String strClassifiedPay = unclassifiedPay.getText().toString();
                if ("BTC".equals(base_unit)) {
                    limit = BigDecimal.valueOf(Double.parseDouble(strClassifiedPay)).multiply(BigDecimal.valueOf(100000000L)).setScale(0);
                } else if ("mBTC".equals(base_unit)) {
                    limit = BigDecimal.valueOf(Double.parseDouble(strClassifiedPay)).multiply(BigDecimal.valueOf(100000L)).setScale(0);
                } else if ("bits".equals(base_unit)) {
                    limit = BigDecimal.valueOf(Double.parseDouble(strClassifiedPay)).multiply(BigDecimal.valueOf(100L)).setScale(0);
                } else if ("sat".equals(base_unit)) {
                    limit = BigDecimal.valueOf(Double.parseDouble(strClassifiedPay));
                }
                BigDecimal one = new BigDecimal(1);
                int mathMax = limit.compareTo(one);
                if (mathMax < 0) {
                    mToast(getString(R.string.limit_input_wrong));
                    return;
                }
                if (Ble.getInstance().getConnetedDevices().size() != 0) {
                    if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                        EventBus.getDefault().postSticky(new HandlerEvent());
                    }
                }
                Intent intent = new Intent(this, CommunicationModeSelector.class);
                intent.putExtra("tag", TAG);
                intent.putExtra("limit", String.valueOf(limit));
                intent.putExtra("times", editTimes.getText().toString());
                intent.putExtra("noPIN", noPIN);
                intent.putExtra("noHard", noHard);
                startActivity(intent);
                break;
            case R.id.test_edit_whitelist:
                Intent intentA = new Intent(this, CommunicationModeSelector.class);
                intentA.putExtra("tag", TAG_EDIT_WHITE_LIST);
                startActivity(intentA);
                break;
            default:
        }
    }

    class TextWatcher1 implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            if (s.toString().contains(".")) {
                if (s.length() - 1 - s.toString().indexOf(".") > 7) {
                    s = s.toString().subSequence(0,
                            s.toString().indexOf(".") + 8);
                    unclassifiedPay.setText(s);
                    unclassifiedPay.setSelection(s.length());
                }
            }
            if (s.toString().trim().substring(0).equals(".")) {
                s = "0" + s;
                unclassifiedPay.setText(s);
                unclassifiedPay.setSelection(2);
            }
            if (s.toString().startsWith("0")
                    && s.toString().trim().length() > 1) {
                if (!s.toString().substring(1, 2).equals(".")) {
                    unclassifiedPay.setText(s.subSequence(0, 1));
                    unclassifiedPay.setSelection(1);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if ((unclassifiedPay.length() > 0 && editTimes.length() > 0)) {
                btnSetKey.setEnabled(true);
                btnSetKey.setBackground(getDrawable(R.drawable.button_bk));
            } else {
                btnSetKey.setEnabled(false);
                btnSetKey.setBackground(getDrawable(R.drawable.button_bk_grey));
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doBusiness(FastPayEvent event) {
        if (!TextUtils.isEmpty(event.getCode())) {
            if (event.getCode().equals("1")) {
                if (noPIN.equals("true")) {
                    edit.putBoolean("boPIN_set", true).apply();
                } else if (noPIN.equals("false")) {
                    edit.putBoolean("boPIN_set", false).apply();
                }
                if (noHard.equals("true")) {
                    edit.putBoolean("noHard_set", true).apply();
                } else if (noHard.equals("false")) {
                    edit.putBoolean("noHard_set", false).apply();
                }
                unclassifiedPay.setText("");
                editTimes.setText("");
                mToast(getString(R.string.set_success));
            } else {
                mToast(getString(R.string.set_fail));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEvent(EditWhiteListEvent event) {
        if ("checkWhiteList".equals(event.getType())) {
            Intent intent = new Intent(ConfidentialPaymentSettings.this, EditWhiteListActivity.class);
            intent.putExtra("whiteListData", event.getContent());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
