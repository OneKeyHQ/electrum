package org.haobtc.onekey.onekeys.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.onekeys.dialog.recovery.ImprotSingleActivity;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecoverHdWalletActivity extends BaseActivity {

    @BindView(R.id.edit_one)
    EditText editOne;
    @BindView(R.id.edit_two)
    EditText editTwo;
    @BindView(R.id.edit_three)
    EditText editThree;
    @BindView(R.id.edit_four)
    EditText editFour;
    @BindView(R.id.edit_five)
    EditText editFive;
    @BindView(R.id.edit_six)
    EditText editSix;
    @BindView(R.id.edit_seven)
    EditText editSeven;
    @BindView(R.id.edit_eight)
    EditText editEight;
    @BindView(R.id.edit_nine)
    EditText editNine;
    @BindView(R.id.edit_ten)
    EditText editTen;
    @BindView(R.id.edit_eleven)
    EditText editEleven;
    @BindView(R.id.edit_twelve)
    EditText editTwelve;
    @BindView(R.id.btn_recovery)
    Button btnRecovery;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recover_hd_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        TextWatcher1 textWatcher1 = new TextWatcher1();
        editOne.addTextChangedListener(textWatcher1);
        editTwo.addTextChangedListener(textWatcher1);
        editThree.addTextChangedListener(textWatcher1);
        editFour.addTextChangedListener(textWatcher1);
        editFive.addTextChangedListener(textWatcher1);
        editSix.addTextChangedListener(textWatcher1);
        editSeven.addTextChangedListener(textWatcher1);
        editEight.addTextChangedListener(textWatcher1);
        editNine.addTextChangedListener(textWatcher1);
        editTen.addTextChangedListener(textWatcher1);
        editEleven.addTextChangedListener(textWatcher1);
        editTwelve.addTextChangedListener(textWatcher1);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.btn_recovery, R.id.lin_hard_recovery, R.id.lin_import, R.id.img_copy_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_recovery:
                String strone = editOne.getText().toString();
                String strtwo = editTwo.getText().toString();
                String strthree = editThree.getText().toString();
                String strfour = editFour.getText().toString();
                String strfive = editFive.getText().toString();
                String strsix = editSix.getText().toString();
                String strseven = editSeven.getText().toString();
                String streight = editEight.getText().toString();
                String strnine = editNine.getText().toString();
                String strten = editTen.getText().toString();
                String streleven = editEleven.getText().toString();
                String strtwelve = editTwelve.getText().toString();
                String strNewseed = strone + " " + strtwo + " " + strthree + " " + strfour + " " + strfive + " " + strsix + " " + strseven + " " + streight + " " + strnine + " " + strten + " " + streleven + " " + strtwelve;
                Intent intent = new Intent(RecoverHdWalletActivity.this, SetHDWalletPassActivity.class);
                intent.putExtra("importHdword", "recoveryHdWallet");
                intent.putExtra("recoverySeed", strNewseed);
                startActivity(intent);
                break;
            case R.id.lin_hard_recovery:
                Intent recovery = new Intent(RecoverHdWalletActivity.this, SearchDevicesActivity.class);
                recovery.putExtra(Constant.SEARCH_DEVICE_MODE, Constant.SearchDeviceMode.MODE_RECOVERY_WALLET_BY_COLD);
                startActivity(recovery);
                break;
            case R.id.lin_import:
                Intent intent2 = new Intent(RecoverHdWalletActivity.this, ImprotSingleActivity.class);
                startActivity(intent2);
                break;
            case R.id.img_copy_test:
                pasteSeed();
                break;
        }
    }

    private void pasteSeed() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData data = clipboard.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                CharSequence text = data.getItemAt(0).getText();
                if (!TextUtils.isEmpty(text.toString())) {
                    String[] wordsList = text.toString().split("\\s+");
                    ArrayList<String> wordList = new ArrayList<>(Arrays.asList(wordsList));
                    switch (wordList.size()) {
                        case 12:
                            editTwelve.setText(wordList.get(11));
                        case 11:
                            editEleven.setText(wordList.get(10));
                        case 10:
                            editTen.setText(wordList.get(9));
                        case 9:
                            editNine.setText(wordList.get(8));
                        case 8:
                            editEight.setText(wordList.get(7));
                        case 7:
                            editSeven.setText(wordList.get(6));
                        case 6:
                            editSix.setText(wordList.get(5));
                        case 5:
                            editFive.setText(wordList.get(4));
                        case 4:
                            editFour.setText(wordList.get(3));
                        case 3:
                            editThree.setText(wordList.get(2));
                        case 2:
                            editTwo.setText(wordList.get(1));
                        case 1:
                            editOne.setText(wordList.get(0));
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + wordList.size());
                    }
                }
            }
        }
    }

    class TextWatcher1 implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if ((editOne.length() > 0 && editTwo.length() > 0 && editThree.length() > 0 && editFour.length() > 0)
                    && editFive.length() > 0 && editSix.length() > 0 && editSeven.length() > 0 && editEight.length() > 0
                    && editNine.length() > 0 && editTen.length() > 0 && editEleven.length() > 0 && editTwelve.length() > 0) {
                btnRecovery.setEnabled(true);
                btnRecovery.setBackground(getDrawable(R.drawable.btn_checked));
            } else {
                btnRecovery.setEnabled(false);
                btnRecovery.setBackground(getDrawable(R.drawable.btn_no_check));
            }
        }
    }
}