package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HdRootMnemonicsActivity extends BaseActivity {

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

    @Override
    public int getLayoutId() {
        return R.layout.activity_hd_root_mnemonics;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

    }

    @Override
    public void initData() {
        writeMnemonicWord();
    }

    private void writeMnemonicWord() {
        String exportWord = getIntent().getStringExtra("exportWord");
        if (!TextUtils.isEmpty(exportWord.toString())) {
            String[] wordsList = exportWord.toString().split("\\s+");
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

    @OnClick({R.id.img_back, R.id.btn_copy_it})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_copy_it:
                finish();
                break;
        }
    }
}