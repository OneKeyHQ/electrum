package org.haobtc.keymanager.activities.settings;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditAnyskServerActivity extends BaseActivity {

    @BindView(R.id.text_delete)
    TextView textDelete;
    @BindView(R.id.testNodeType)
    TextView testNodeType;
    @BindView(R.id.relNodeType)
    RelativeLayout relNodeType;
    @BindView(R.id.editAgentIP)
    EditText editAgentIP;
    @BindView(R.id.editPort)
    EditText editPort;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_anysk_server;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String edit_server = intent.getStringExtra("edit_server");
        if ("editServer".equals(edit_server)) {
            textDelete.setVisibility(View.VISIBLE);
        } else {
            textDelete.setVisibility(View.GONE);
        }
        TextChange textChange = new TextChange();
        editAgentIP.addTextChangedListener(textChange);
        editPort.addTextChangedListener(textChange);

    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.text_delete, R.id.btnConfirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_delete:
                break;
            case R.id.btnConfirm:
                break;
            default:
        }
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
        String strAgentIP = editAgentIP.getText().toString();
        String strPort = editPort.getText().toString();

        if (TextUtils.isEmpty(strAgentIP) || TextUtils.isEmpty(strPort)) {
            btnConfirm.setEnabled(false);
            btnConfirm.setBackground(getDrawable(R.drawable.little_radio_qian));

        } else {
            btnConfirm.setEnabled(true);
            btnConfirm.setBackground(getDrawable(R.drawable.little_radio_blue));

        }

    }


}
