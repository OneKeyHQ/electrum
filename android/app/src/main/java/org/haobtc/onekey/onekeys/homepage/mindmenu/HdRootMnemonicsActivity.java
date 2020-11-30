package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.chaquo.python.PyObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.onekeys.backup.CheckMnemonicActivity;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HdRootMnemonicsActivity extends BaseActivity {

    @BindView(R.id.edit_one)
    TextView editOne;
    @BindView(R.id.edit_two)
    TextView editTwo;
    @BindView(R.id.edit_three)
    TextView editThree;
    @BindView(R.id.edit_four)
    TextView editFour;
    @BindView(R.id.edit_five)
    TextView editFive;
    @BindView(R.id.edit_six)
    TextView editSix;
    @BindView(R.id.edit_seven)
    TextView editSeven;
    @BindView(R.id.edit_eight)
    TextView editEight;
    @BindView(R.id.edit_nine)
    TextView editNine;
    @BindView(R.id.edit_ten)
    TextView editTen;
    @BindView(R.id.edit_eleven)
    TextView editEleven;
    @BindView(R.id.edit_twelve)
    TextView editTwelve;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.tet_title_name)
    TextView tetTitleName;
    private String status = "";
    private String exportWord;
    private String exportType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_hd_root_mnemonics;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//禁止截屏
        exportType = getIntent().getStringExtra("exportType");
        String importHdword = getIntent().getStringExtra("importHdword");
        exportWord = getIntent().getStringExtra("exportWord");
        if ("importHdword".equals(importHdword)) {
            textTitle.setVisibility(View.GONE);
            tetTitleName.setText(getString(R.string.export_mnemonic));
        }
        if ("backup".equals(exportType)) {
            textTitle.setText(getString(R.string.this_mnemonic));
        }
    }

    @Override
    public void initData() {
        //get mnemonic
        writeMnemonicWord();
    }

    private void writeMnemonicWord() {
        if (!TextUtils.isEmpty(exportWord)) {
            String[] wordsList = exportWord.split("\\s+");
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
                if ("backup".equals(exportType)) {
                    backupInfo();
                } else {
                    finish();
                }
                break;
        }
    }

    private void backupInfo() {
        Intent intent = new Intent(HdRootMnemonicsActivity.this, CheckMnemonicActivity.class);
        intent.putExtra("mnemonic", exportWord);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(FinishEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}