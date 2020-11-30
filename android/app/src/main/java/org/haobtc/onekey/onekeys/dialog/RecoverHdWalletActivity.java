package org.haobtc.onekey.onekeys.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaquo.python.PyObject;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.SearchMnemonicAdapter;
import org.haobtc.onekey.onekeys.dialog.recovery.ImprotSingleActivity;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecoverHdWalletActivity extends BaseActivity implements View.OnFocusChangeListener {

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
    @BindView(R.id.view_show)
    View viewShow;
    @BindView(R.id.recl_search_mnemonic)
    RecyclerView reclSearchMnemonic;
    private ArrayList<String> searchWordList;
    private SearchMnemonicAdapter searchMnemonicAdapter;
    private int focus;
    private List<String> seedList;
    private int screenHeight;
    private boolean mIsSoftKeyboardShowing;
    private ViewTreeObserver.OnGlobalLayoutListener mLayoutChangeListener;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recover_hd_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        inits();
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
        editOne.setOnFocusChangeListener(this);
        editTwo.setOnFocusChangeListener(this);
        editThree.setOnFocusChangeListener(this);
        editFour.setOnFocusChangeListener(this);
        editFive.setOnFocusChangeListener(this);
        editSix.setOnFocusChangeListener(this);
        editSeven.setOnFocusChangeListener(this);
        editEight.setOnFocusChangeListener(this);
        editNine.setOnFocusChangeListener(this);
        editTen.setOnFocusChangeListener(this);
        editEleven.setOnFocusChangeListener(this);
        editTwelve.setOnFocusChangeListener(this);

    }

    private void inits() {
        //监听软键盘
        registerKeyBoard();
        //获取所有助记词
        getAllMnemonic();
        //模糊匹配的助记词集合
        searchWordList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        reclSearchMnemonic.setLayoutManager(manager);
        searchMnemonicAdapter = new SearchMnemonicAdapter(searchWordList);
        reclSearchMnemonic.setAdapter(searchMnemonicAdapter);
        searchMnemonicAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String word = searchWordList.get(position);
                String switchWord = word.replaceAll(" ", "");
                if (focus == 1) {
                    editOne.setText(switchWord);
                } else if (focus == 2) {
                    editTwo.setText(switchWord);
                } else if (focus == 3) {
                    editThree.setText(switchWord);
                } else if (focus == 4) {
                    editFour.setText(switchWord);
                } else if (focus == 5) {
                    editFive.setText(switchWord);
                } else if (focus == 6) {
                    editSix.setText(switchWord);
                } else if (focus == 7) {
                    editSeven.setText(switchWord);
                } else if (focus == 8) {
                    editEight.setText(switchWord);
                } else if (focus == 9) {
                    editNine.setText(switchWord);
                } else if (focus == 10) {
                    editTen.setText(switchWord);
                } else if (focus == 11) {
                    editEleven.setText(switchWord);
                } else if (focus == 12) {
                    editTwelve.setText(switchWord);
                }
            }
        });
    }

    private void getAllMnemonic() {
        PyObject allSeeds = null;
        try {
            allSeeds = Daemon.commands.callAttr("get_all_mnemonic");
            String content = allSeeds.toString();
            String seeds = content.replaceAll("\"", "");
            String[] pathArr = (seeds.substring(1, seeds.length() - 1)).split(",");
            seedList = Arrays.asList(pathArr);
            Log.i("allSeeds", "getAllMnemonic: " + seedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerKeyBoard() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenHeight = metric.heightPixels;
        mIsSoftKeyboardShowing = false;
        mLayoutChangeListener = () -> {
            //Determine the size of window visible area
            Rect r = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            //If the difference between screen height and window visible area height is greater than 1 / 3 of the whole screen height, it means that the soft keyboard is in display, otherwise, the soft keyboard is hidden.
            int heightDifference = screenHeight - (r.bottom - r.top);
            boolean isKeyboardShowing = heightDifference > screenHeight / 3;

            //If the status of the soft keyboard was previously displayed, it is now closed, or it was previously closed, it is now displayed, it means that the status of the soft keyboard has changed
            if ((mIsSoftKeyboardShowing && !isKeyboardShowing) || (!mIsSoftKeyboardShowing && isKeyboardShowing)) {
                mIsSoftKeyboardShowing = isKeyboardShowing;
                if (!mIsSoftKeyboardShowing) {
                    reclSearchMnemonic.setVisibility(View.GONE);
                    viewShow.setVisibility(View.GONE);
                }
            }
        };
        //Register layout change monitoring
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(mLayoutChangeListener);
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
                isSeed(strNewseed);
                break;
            case R.id.lin_hard_recovery:
                Intent recovery = new Intent(RecoverHdWalletActivity.this, SearchDevicesActivity.class);
//                recovery.putExtra(Constant.SEARCH_DEVICE_MODE, Constant.SearchDeviceMode.MODE_RECOVERY_WALLET_BY_COLD);
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

    private void isSeed(String strNewseed) {
        Intent intent = new Intent(RecoverHdWalletActivity.this, SetHDWalletPassActivity.class);
        intent.putExtra("importHdword", "recoveryHdWallet");
        intent.putExtra("recoverySeed", strNewseed);
        startActivity(intent);
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.edit_one:
                    focus = 1;
                    break;
                case R.id.edit_two:
                    focus = 2;
                    break;
                case R.id.edit_three:
                    focus = 3;
                    break;
                case R.id.edit_four:
                    focus = 4;
                    break;
                case R.id.edit_five:
                    focus = 5;
                    break;
                case R.id.edit_six:
                    focus = 6;
                    break;
                case R.id.edit_seven:
                    focus = 7;
                    break;
                case R.id.edit_eight:
                    focus = 8;
                    break;
                case R.id.edit_nine:
                    focus = 9;
                    break;
                case R.id.edit_ten:
                    focus = 10;
                    break;
                case R.id.edit_eleven:
                    focus = 11;
                    break;
                case R.id.edit_twelve:
                    focus = 12;
                    break;
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
            searchWordList.clear();
            if ((editOne.length() > 0 && editTwo.length() > 0 && editThree.length() > 0 && editFour.length() > 0)
                    && editFive.length() > 0 && editSix.length() > 0 && editSeven.length() > 0 && editEight.length() > 0
                    && editNine.length() > 0 && editTen.length() > 0 && editEleven.length() > 0 && editTwelve.length() > 0) {
                btnRecovery.setEnabled(true);
                btnRecovery.setBackground(getDrawable(R.drawable.btn_checked));
            } else {
                btnRecovery.setEnabled(false);
                btnRecovery.setBackground(getDrawable(R.drawable.btn_no_check));
            }
            if (!TextUtils.isEmpty(editable.toString())) {
                for (int i = 0; i < seedList.size(); i++) {
                    if (seedList.get(i).replaceAll(" ", "").startsWith(editable.toString())) {
                        searchWordList.add(seedList.get(i));
                    }
                }
            } else {
                reclSearchMnemonic.setVisibility(View.GONE);
                viewShow.setVisibility(View.GONE);
            }
            if (searchWordList != null && searchWordList.size() != 0) {
                reclSearchMnemonic.setVisibility(View.VISIBLE);
                viewShow.setVisibility(View.VISIBLE);
            }
            searchMnemonicAdapter.notifyDataSetChanged();
        }
    }
}