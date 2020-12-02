package org.haobtc.onekey.activities.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnyskServerSetActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @BindView(R.id.switch_cynchronez)
    Switch switchCynchronez;
    @BindView(R.id.btn_add_server)
    Button btnAddServer;
    @BindView(R.id.editAgentIP)
    EditText editAgentIP;
    @BindView(R.id.editPort)
    EditText editPort;
    private SharedPreferences preferences;
    private SharedPreferences.Editor edit;
    private boolean open = false;
    private String strAgentIP;
    private String strPort;

    @Override
    public int getLayoutId() {
        return R.layout.activity_anysk_server_set;
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        inits();

    }

    @SuppressLint("SetTextI18n")
    private void inits() {
        Intent intent = getIntent();
        //get ip and port
        String ipPort = intent.getStringExtra("ip_port");
        assert ipPort != null;
        String strIp = ipPort.substring(0, ipPort.indexOf(":"));
        int length = ipPort.length();
        String strPort = ipPort.substring(strIp.length() + 1, length);
        editAgentIP.setText(strIp);
        editPort.setText(strPort);

        //editText Listener
        TextChange textChange = new TextChange();
        editAgentIP.addTextChangedListener(textChange);
        editPort.addTextChangedListener(textChange);

        //synchronize server
        boolean setSynServer = preferences.getBoolean("set_syn_server", false);
        if (setSynServer) {
            open = true;
            switchCynchronez.setChecked(true);
        } else {
            open = false;
            switchCynchronez.setChecked(false);
        }
        //judge button status
        buttonColorStatus();

    }

    @Override
    public void initData() {
        switchCyn();

    }

    private void switchCyn() {
        switchCynchronez.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i("sdjfklsndfssd", "--------------33: ");
                    open = true;
                    try {
                        Daemon.commands.callAttr("set_syn_server", true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    edit.putBoolean("set_syn_server", true);
                    edit.apply();
                    mToast(getString(R.string.set_success));
                } else {
                    open = false;
                    try {
                        Daemon.commands.callAttr("set_syn_server", false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    edit.putBoolean("set_syn_server", false);
                    edit.apply();
                }
                //judge button status
                buttonColorStatus();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @OnClick({R.id.img_back, R.id.btn_add_server, R.id.text_recovery_default})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_add_server:
                strAgentIP = editAgentIP.getText().toString();
                strPort = editPort.getText().toString();
                addAnyskServer(strAgentIP, strPort);

                break;
            case R.id.text_recovery_default:
                switchCynchronez.setChecked(true);
                editAgentIP.setText("39.105.86.163");
                editPort.setText("8080");
                addAnyskServer("39.105.86.163", "8080");
                break;
            default:
        }
    }

    private void addAnyskServer(String ip, String port) {
        try {
            Daemon.commands.callAttr("set_sync_server_host", ip, port);
        } catch (Exception e) {
            if (e.getMessage().contains("No address associated with hostname")){
                mToast(getString(R.string.invalid_address));
            }
            e.printStackTrace();
            return;
        }
        EventBus.getDefault().post(new FirstEvent("add_anysk_server"));
    }

    class TextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //judge button status
            buttonColorStatus();
        }
    }

    //judge button status
    private void buttonColorStatus() {
        strAgentIP = editAgentIP.getText().toString();
        strPort = editPort.getText().toString();

        if (TextUtils.isEmpty(strAgentIP) || TextUtils.isEmpty(strPort)) {
            btnAddServer.setEnabled(false);
            btnAddServer.setBackground(getDrawable(R.drawable.btn_no_check));

        } else {
            if (open) {
                btnAddServer.setEnabled(true);
                btnAddServer.setBackground(getDrawable(R.drawable.btn_checked));
            } else {
                btnAddServer.setEnabled(false);
                btnAddServer.setBackground(getDrawable(R.drawable.btn_no_check));
            }

        }

    }
}
