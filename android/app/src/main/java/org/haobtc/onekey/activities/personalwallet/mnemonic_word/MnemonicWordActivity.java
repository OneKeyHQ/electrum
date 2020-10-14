package org.haobtc.onekey.activities.personalwallet.mnemonic_word;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.ChoosePayAddressAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.AddressEvent;
import org.haobtc.onekey.event.FirstEvent;
import org.haobtc.onekey.event.FromSeedEvent;
import org.haobtc.onekey.fragment.mainwheel.WheelViewpagerFragment;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MnemonicWordActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
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
    @BindView(R.id.btn_setPin)
    Button btnSetPin;
    @BindView(R.id.text_paste)
    TextView textPaste;
    private PyObject getFromSeed;

    @Override
    public int getLayoutId() {
        return R.layout.activity_mnemonic_word;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        inits();
    }

    private void inits() {
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

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_setPin, R.id.text_paste})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_setPin:
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
                judgeSeedorrectC(strNewseed);
                break;
            case R.id.text_paste:
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
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void judgeSeedorrectC(String newSeed) {
        PyObject isSeeds = null;
        try {
            isSeeds = Daemon.commands.callAttr("is_seed", newSeed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isSeeds != null) {
            boolean isSeed = isSeeds.toBoolean();
            if (isSeed) {
                try {
                    Daemon.commands.callAttr("is_exist_seed", newSeed);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("The same seed have create wallet")) {
                        String haveWalletName = e.getMessage().substring(e.getMessage().indexOf("name=") + 5);
                        mToast(getString(R.string.same_seed_have) + haveWalletName);
                    }
                    return;
                }
                Intent intent = new Intent(MnemonicWordActivity.this, ImportMnemonicWalletActivity.class);
                intent.putExtra("strNewseed", newSeed);
                startActivity(intent);
                finish();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //get import mnemonic address
                        getAddrsFromSeed(newSeed);
                    }
                }, 350);

            } else {
                mToast(getString(R.string.helpword_wrong));
            }
        }
    }

    //get import mnemonic address
    private void getAddrsFromSeed(String newSeed) {
        try {
            getFromSeed = Daemon.commands.callAttr("get_addrs_from_seed", newSeed);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        EventBus.getDefault().postSticky(new FromSeedEvent(getFromSeed.toString()));
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
                btnSetPin.setEnabled(true);
                btnSetPin.setBackground(getDrawable(R.drawable.button_bk));
            } else {
                btnSetPin.setEnabled(false);
                btnSetPin.setBackground(getDrawable(R.drawable.button_bk_grey));
            }
        }
    }
}
