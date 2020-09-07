package org.haobtc.keymanager.activities.personalwallet.mnemonic_word;

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

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.aop.SingleClick;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateHelpWordWalletActivity extends BaseActivity {

    @BindView(R.id.img_backCreat)
    ImageView imgBackCreat;
    @BindView(R.id.edit_walletName_setting)
    EditText editWalletNameSetting;
    @BindView(R.id.tet_textNum)
    TextView tetTextNum;
    @BindView(R.id.bn_multi_next)
    Button bnMultiNext;
    private String newWalletType;
    private String newSeed;
    private int defaultName;
    private int walletNameNum;
    private String mnemonicWalletType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_help_word_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        defaultName = preferences.getInt("defaultName", 0);//default wallet name
        Intent intent = getIntent();
        newWalletType = intent.getStringExtra("newWallet_type");
        newSeed = intent.getStringExtra("newSeed");
        mnemonicWalletType = intent.getStringExtra("mnemonic_wallet_derivation");

    }

    @Override
    public void initData() {
        setEditTextComments();
    }

    private void setEditTextComments() {
        walletNameNum = defaultName + 1;
        editWalletNameSetting.setText(String.format("钱包%s", String.valueOf(walletNameNum)));
        tetTextNum.setText(String.format(Locale.CHINA, "%d/16", editWalletNameSetting.length()));
        editWalletNameSetting.addTextChangedListener(new TextWatcher() {
            CharSequence input;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                input = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tetTextNum.setText(String.format(Locale.CHINA, "%d/20", input.length()));
                if (input.length() > 19) {
                    Toast.makeText(CreateHelpWordWalletActivity.this, R.string.moreinput_text, Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(s)) {
                    bnMultiNext.setEnabled(false);
                    bnMultiNext.setBackground(getDrawable(R.drawable.button_bk_grey));
                } else {
                    bnMultiNext.setEnabled(true);
                    bnMultiNext.setBackground(getDrawable(R.drawable.button_bk));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @SingleClick
    @OnClick({R.id.img_backCreat, R.id.bn_multi_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_backCreat:
                finish();
                break;
            case R.id.bn_multi_next:
                String strWalletname = editWalletNameSetting.getText().toString();
                if (TextUtils.isEmpty(strWalletname)) {
                    mToast(getString(R.string.please_input_walletname));
                    return;
                }
                Intent intent = new Intent(CreateHelpWordWalletActivity.this, CreatePersonalHelpPassActivity.class);
                intent.putExtra("newWallet_type", newWalletType);
                intent.putExtra("strNewseed", newSeed);
                intent.putExtra("walletNameNum", walletNameNum);
                intent.putExtra("newWalletName", strWalletname);
                intent.putExtra("mnemonicWalletType", mnemonicWalletType);
                startActivity(intent);
                finish();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}
