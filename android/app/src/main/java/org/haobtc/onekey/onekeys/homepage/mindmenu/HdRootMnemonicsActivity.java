package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.MainActivity;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.BackupEvent;
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_hd_root_mnemonics;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        String exportType = getIntent().getStringExtra("exportType");
        String importHdword = getIntent().getStringExtra("importHdword");
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
        //check is there a backup
        whetherBackup();
        //get mnemonic
        writeMnemonicWord();
    }

    private void whetherBackup() {
        try {
            PyObject isBackup = Daemon.commands.callAttr("get_backup_info");
            status = isBackup.toString();
        } catch (Exception e) {
            status = "exception";
            e.printStackTrace();
        }

    }

    private void writeMnemonicWord() {
        String exportWord = getIntent().getStringExtra("exportWord");
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
                if ("False".equals(status)) {
                    backupInfo();
                } else {
                    finish();
                }
                break;
        }
    }

    private void backupInfo() {
        try {
            Daemon.commands.callAttr("delete_backup_info");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        EventBus.getDefault().post(new BackupEvent());
        finish();
    }

}