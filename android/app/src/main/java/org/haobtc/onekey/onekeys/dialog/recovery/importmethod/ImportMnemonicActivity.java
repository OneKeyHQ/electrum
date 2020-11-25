package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.chaquo.python.PyObject;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportMnemonicActivity extends BaseActivity {

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
    private SharedPreferences.Editor edit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_mnemonic;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.img_back, R.id.btn_recovery,R.id.img_test_paste})
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
                if ((TextUtils.isEmpty(strone) || TextUtils.isEmpty(strtwo) || TextUtils.isEmpty(strthree) || TextUtils.isEmpty(strfour))
                        || TextUtils.isEmpty(strfive) || TextUtils.isEmpty(strsix) || TextUtils.isEmpty(strseven) || TextUtils.isEmpty(streight)
                        || TextUtils.isEmpty(strnine) || TextUtils.isEmpty(strten) || TextUtils.isEmpty(streleven) || TextUtils.isEmpty(strtwelve)) {
                    mToast(getString(R.string._12_help_word));
                    return;
                }
                String strNewseed = strone + " " + strtwo + " " + strthree + " " + strfour + " " + strfive + " " + strsix + " " + strseven + " " + streight + " " + strnine + " " + strten + " " + streleven + " " + strtwelve;
                isSeed(strNewseed);
                break;
            case R.id.img_test_paste:
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
                break;
        }
    }

    private void isSeed(String strNewseed) {
        PyObject isSeeds = null;
        try {
            isSeeds = Daemon.commands.callAttr("is_seed", strNewseed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("isSeedsisSeedsisSeeds", "isSeed: "+isSeeds.toBoolean());
        Intent intent = new Intent(ImportMnemonicActivity.this, ImportWalletSetNameActivity.class);
        intent.putExtra("importHdword", "importMnemonic");
        intent.putExtra("recoverySeed", strNewseed);
        startActivity(intent);


    }
}
