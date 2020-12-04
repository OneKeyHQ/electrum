package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.chaquo.python.PyObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.event.FinishEvent;
import org.haobtc.onekey.onekeys.backup.CheckMnemonicActivity;
import org.haobtc.onekey.onekeys.dialog.recovery.ImprotSingleActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateWalletChooseTypeActivity;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.ScreenShotListenManager;

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
    private String importHdword;
    private ScreenShotListenManager screenShotListenManager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_hd_root_mnemonics;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//禁止截屏
        EventBus.getDefault().register(this);
        screenShotListenManager = new ScreenShotListenManager(this);
//        startScreenShotListen();
        importHdword = getIntent().getStringExtra("importHdword");
        exportWord = getIntent().getStringExtra("exportWord");
        if ("exportHdword".equals(importHdword)) {
            textTitle.setVisibility(View.GONE);
            tetTitleName.setText(getString(R.string.export_mnemonic));
        } else {
            textTitle.setText(getString(R.string.this_mnemonic));
        }
    }

    @Override
    public void initData() {
        //get mnemonic
        writeMnemonicWord();
    }

    //禁止截屏监听
    private void startScreenShotListen() {
        if (screenShotListenManager != null) {
            screenShotListenManager.setListener(new ScreenShotListenManager.OnScreenShotListener() {
                @Override
                public void onShot(String imagePath) {
                    screenTipDialog(HdRootMnemonicsActivity.this, R.layout.screened);
                    startScreenShotListen();
                }
            });
            screenShotListenManager.startListen();
        }
    }

    private void screenTipDialog(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.img_cancel).setOnClickListener(v -> {
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
                if ("exportHdword".equals(importHdword)) {
                    finish();
                } else {
                    backupInfo();
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