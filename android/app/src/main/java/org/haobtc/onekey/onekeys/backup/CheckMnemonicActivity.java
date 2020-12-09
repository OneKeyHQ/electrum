package org.haobtc.onekey.onekeys.backup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.chaquo.python.Kwarg;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.BackupEvent;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.onekeys.dialog.recovery.ImprotSingleActivity;
import org.haobtc.onekey.onekeys.homepage.WalletListActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateWalletChooseTypeActivity;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME;

public class CheckMnemonicActivity extends BaseActivity {

    @BindView(R.id.text_word_one)
    TextView textWordOne;
    @BindView(R.id.red_group_one)
    RadioGroup redGroupOne;
    @BindView(R.id.text_word_two)
    TextView textWordTwo;
    @BindView(R.id.red_group_two)
    RadioGroup redGroupTwo;
    @BindView(R.id.text_word_three)
    TextView textWordThree;
    @BindView(R.id.red_group_three)
    RadioGroup redGroupThree;
    @BindView(R.id.btn_check)
    Button btnCheck;
    @BindView(R.id.one_line_1)
    RadioButton oneLine1;
    @BindView(R.id.one_line_2)
    RadioButton oneLine2;
    @BindView(R.id.one_line_3)
    RadioButton oneLine3;
    @BindView(R.id.two_line_1)
    RadioButton twoLine1;
    @BindView(R.id.two_line_2)
    RadioButton twoLine2;
    @BindView(R.id.two_line_3)
    RadioButton twoLine3;
    @BindView(R.id.three_line_1)
    RadioButton threeLine1;
    @BindView(R.id.three_line_2)
    RadioButton threeLine2;
    @BindView(R.id.three_line_3)
    RadioButton threeLine3;
    private String[] array;
    private String word1 = "";
    private String word2 = "";
    private String word3 = "";
    private List listPos;
    private ArrayList<String> chooseWord;
    private String loadWalletName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_mnemonic;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("", MODE_PRIVATE);
        //Get current wallet name
        loadWalletName = preferences.getString(CURRENT_SELECTED_WALLET_NAME, "");
        String mnemonic = getIntent().getStringExtra("mnemonic");
        array = mnemonic.split("\\s+");

    }

    @Override
    public void initData() {
        chooseWord = new ArrayList<>();
        titleList();
        radioOnclick();
    }

    private void radioOnclick() {
        redGroupOne.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.one_line_1:
                        word1 = oneLine1.getText().toString();
                        buttonStatus();
                        break;
                    case R.id.one_line_2:
                        word1 = oneLine2.getText().toString();
                        buttonStatus();
                        break;
                    case R.id.one_line_3:
                        word1 = oneLine3.getText().toString();
                        buttonStatus();
                        break;

                }
            }
        });
        redGroupTwo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.two_line_1:
                        word2 = twoLine1.getText().toString();
                        buttonStatus();
                        break;
                    case R.id.two_line_2:
                        word2 = twoLine2.getText().toString();
                        buttonStatus();
                        break;
                    case R.id.two_line_3:
                        word2 = twoLine3.getText().toString();
                        buttonStatus();
                        break;

                }
            }
        });
        redGroupThree.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.three_line_1:
                        word3 = threeLine1.getText().toString();
                        buttonStatus();
                        break;
                    case R.id.three_line_2:
                        word3 = threeLine2.getText().toString();
                        buttonStatus();
                        break;
                    case R.id.three_line_3:
                        word3 = threeLine3.getText().toString();
                        buttonStatus();
                        break;

                }
            }
        });
    }

    private void buttonStatus() {
        if (!TextUtils.isEmpty(word1) && !TextUtils.isEmpty(word2) && !TextUtils.isEmpty(word3)) {
            btnCheck.setEnabled(true);
            btnCheck.setBackground(getDrawable(R.drawable.btn_checked));
        } else {
            btnCheck.setEnabled(false);
            btnCheck.setBackground(getDrawable(R.drawable.btn_no_check));
        }

    }

    private void titleList() {
        Random rand = new Random();
        listPos = new ArrayList();
        for (int i = 0; i < 3; i++) {
            int num = rand.nextInt(11) + 1;
            while (listPos.contains(num)) {
                num = rand.nextInt(11) + 1;
            }
            listPos.add(num);
            chooseWord.add(array[num - 1]);//choose 3 words
        }
        textWordOne.setText(String.format("%s%s%s", getString(R.string.chooseword_left), listPos.get(0), getString(R.string.chooseword_right)));
        textWordTwo.setText(String.format("%s%s%s", getString(R.string.chooseword_left), listPos.get(1), getString(R.string.chooseword_right)));
        textWordThree.setText(String.format("%s%s%s", getString(R.string.chooseword_left), listPos.get(2), getString(R.string.chooseword_right)));

        List list1 = new ArrayList();
        for (int i = 0; i < 3; i++) {
            int num = rand.nextInt(12);
            String word = array[num];
            while (list1.contains(word)) {
                num = rand.nextInt(12);
                word = array[num];
            }
            if (i == 2) {
                String aaa = array[Integer.valueOf(listPos.get(0).toString()) - 1];
                if (!list1.contains(aaa)) {
                    list1.add(aaa);
                } else {
                    list1.add(word);
                }
            } else {
                list1.add(word);
            }
        }
        Collections.shuffle(list1);

        oneLine1.setText(list1.get(0).toString());
        oneLine2.setText(list1.get(1).toString());
        oneLine3.setText(list1.get(2).toString());

        List list2 = new ArrayList();
        for (int i = 0; i < 3; i++) {
            int num = rand.nextInt(12);
            String word = array[num];
            while (list2.contains(word)) {
                num = rand.nextInt(12);
                word = array[num];
            }
            if (i == 2) {
                String aaa = array[Integer.valueOf(listPos.get(1).toString()) - 1];
                if (!list2.contains(aaa)) {
                    list2.add(aaa);
                } else {
                    list2.add(word);
                }
            } else {
                list2.add(word);
            }
        }
        Collections.shuffle(list2);

        twoLine1.setText(list2.get(0).toString());
        twoLine2.setText(list2.get(1).toString());
        twoLine3.setText(list2.get(2).toString());
        List list3 = new ArrayList();
        for (int i = 0; i < 3; i++) {
            int num = rand.nextInt(12);
            String word = array[num];
            while (list3.contains(word)) {
                num = rand.nextInt(12);
                word = array[num];
            }
            if (i == 2) {
                String aaa = array[Integer.valueOf(listPos.get(2).toString()) - 1];
                if (!list3.contains(aaa)) {
                    list3.add(aaa);
                } else {
                    list3.add(word);
                }
            } else {
                list3.add(word);
            }
        }
        Collections.shuffle(list3);

        threeLine1.setText(list3.get(0).toString());
        threeLine2.setText(list3.get(1).toString());
        threeLine3.setText(list3.get(2).toString());

    }

    @OnClick({R.id.img_back, R.id.btn_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_check:
                String keyName = PreferencesManager.get(this, "Preferences", CURRENT_SELECTED_WALLET_NAME, "").toString();
                if (chooseWord.toString().contains(word1) && chooseWord.toString().contains(word2) && chooseWord.toString().contains(word3)) {
                    try {
                        Daemon.commands.callAttr("delete_backup_info", keyName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    EventBus.getDefault().post(new BackupEvent());
                    EventBus.getDefault().post(new FinishEvent());
                    mIntent(BackupCheckSuccessActivity.class);
                    finish();
                } else {
                    checkMnemonicFail(CheckMnemonicActivity.this, R.layout.check_mnemonic_fail);
                }
                break;
        }
    }

    //check Mnemonic Fail
    private void checkMnemonicFail(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.img_cancel).setOnClickListener(v -> {
            dialogBtoms.dismiss();
        });
        view.findViewById(R.id.btn_check).setOnClickListener(v -> {
            dialogBtoms.dismiss();
        });
        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.setCanceledOnTouchOutside(true);
        dialogBtoms.show();

    }

}