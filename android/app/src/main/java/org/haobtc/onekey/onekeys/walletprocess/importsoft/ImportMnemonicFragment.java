package org.haobtc.onekey.onekeys.walletprocess.importsoft;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chaquo.python.PyObject;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.R;
import org.haobtc.onekey.adapter.SearchMnemonicAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.databinding.FragmentImportMnemonicBinding;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.ui.base.BaseFragment;
import org.haobtc.onekey.utils.Daemon;

/**
 * 使用助记词导入账户
 *
 * @author Onekey@QuincySx
 * @create 2021-01-17 12:44 PM
 */
@Keep
public class ImportMnemonicFragment extends BaseFragment
        implements View.OnFocusChangeListener, View.OnClickListener {

    private FragmentImportMnemonicBinding mBinding;

    private ImportSoftWalletProvider mImportSoftWalletProvider;
    private OnFinishViewCallBack mOnFinishViewCallBack;
    private OnImportMnemonicCallback mOnImportMnemonicCallback;

    private List<String> seedList;
    private int screenHeight;
    private boolean mIsSoftKeyboardShowing;
    private ViewTreeObserver.OnGlobalLayoutListener mLayoutChangeListener;
    private ArrayList<String> searchWordList;
    private SearchMnemonicAdapter searchMnemonicAdapter;
    private int focus;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFinishViewCallBack) {
            mOnFinishViewCallBack = (OnFinishViewCallBack) context;
        }
        if (context instanceof OnImportMnemonicCallback) {
            mOnImportMnemonicCallback = (OnImportMnemonicCallback) context;
        }
        if (context instanceof ImportSoftWalletProvider) {
            mImportSoftWalletProvider = (ImportSoftWalletProvider) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mBinding = FragmentImportMnemonicBinding.inflate(inflater, container, false);
        init(mBinding.getRoot());
        return mBinding.getRoot();
    }

    @Override
    public void init(View view) {
        inits();
        mBinding.imgBack.setOnClickListener(this);
        mBinding.imgCoinType.setOnClickListener(this);
        mBinding.btnRecovery.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        if (mImportSoftWalletProvider != null) {
            switch (mImportSoftWalletProvider.currentCoinType()) {
                case BTC:
                    mBinding.imgCoinType.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                    getResources(), R.drawable.token_btc, null));
                    break;
                case ETH:
                    mBinding.imgCoinType.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                    getResources(), R.drawable.token_eth, null));
                    break;
            }
        }
    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    private void inits() {
        // 模糊匹配的助记词集合
        searchWordList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        mBinding.reclSearchMnemonic.setLayoutManager(manager);
        searchMnemonicAdapter = new SearchMnemonicAdapter(searchWordList);
        mBinding.reclSearchMnemonic.setAdapter(searchMnemonicAdapter);
        searchMnemonicAdapter.setOnItemClickListener(
                new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        String word = searchWordList.get(position);
                        String switchWord = word.replaceAll(" ", "");
                        if (focus == 1) {
                            mBinding.editOne.setText(switchWord);
                        } else if (focus == 2) {
                            mBinding.editTwo.setText(switchWord);
                        } else if (focus == 3) {
                            mBinding.editThree.setText(switchWord);
                        } else if (focus == 4) {
                            mBinding.editFour.setText(switchWord);
                        } else if (focus == 5) {
                            mBinding.editFive.setText(switchWord);
                        } else if (focus == 6) {
                            mBinding.editSix.setText(switchWord);
                        } else if (focus == 7) {
                            mBinding.editSeven.setText(switchWord);
                        } else if (focus == 8) {
                            mBinding.editEight.setText(switchWord);
                        } else if (focus == 9) {
                            mBinding.editNine.setText(switchWord);
                        } else if (focus == 10) {
                            mBinding.editTen.setText(switchWord);
                        } else if (focus == 11) {
                            mBinding.editEleven.setText(switchWord);
                        } else if (focus == 12) {
                            mBinding.editTwelve.setText(switchWord);
                        }
                    }
                });
    }

    public void initData() {
        TextWatcher1 textWatcher1 = new TextWatcher1();
        mBinding.editOne.addTextChangedListener(textWatcher1);
        mBinding.editTwo.addTextChangedListener(textWatcher1);
        mBinding.editThree.addTextChangedListener(textWatcher1);
        mBinding.editFour.addTextChangedListener(textWatcher1);
        mBinding.editFive.addTextChangedListener(textWatcher1);
        mBinding.editSix.addTextChangedListener(textWatcher1);
        mBinding.editSeven.addTextChangedListener(textWatcher1);
        mBinding.editEight.addTextChangedListener(textWatcher1);
        mBinding.editNine.addTextChangedListener(textWatcher1);
        mBinding.editTen.addTextChangedListener(textWatcher1);
        mBinding.editEleven.addTextChangedListener(textWatcher1);
        mBinding.editTwelve.addTextChangedListener(textWatcher1);
        mBinding.editOne.setOnFocusChangeListener(this);
        mBinding.editTwo.setOnFocusChangeListener(this);
        mBinding.editThree.setOnFocusChangeListener(this);
        mBinding.editFour.setOnFocusChangeListener(this);
        mBinding.editFive.setOnFocusChangeListener(this);
        mBinding.editSix.setOnFocusChangeListener(this);
        mBinding.editSeven.setOnFocusChangeListener(this);
        mBinding.editEight.setOnFocusChangeListener(this);
        mBinding.editNine.setOnFocusChangeListener(this);
        mBinding.editTen.setOnFocusChangeListener(this);
        mBinding.editEleven.setOnFocusChangeListener(this);
        mBinding.editTwelve.setOnFocusChangeListener(this);
        // 监听软键盘
        registerKeyBoard();
        // 获取所有助记词
        getAllMnemonic();
    }

    private void registerKeyBoard() {
        if (getActivity() == null) {
            return;
        }
        DisplayMetrics metric = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenHeight = metric.heightPixels;
        mIsSoftKeyboardShowing = false;
        mLayoutChangeListener =
                () -> {
                    if (getActivity() == null || getActivity().getWindow() == null) {
                        return;
                    }
                    // Determine the size of window visible area
                    Rect r = new Rect();
                    getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                    // If the difference between screen height and window visible area height is
                    // greater than 1 / 3 of the whole screen height, it means that the soft
                    // keyboard is in display, otherwise, the soft keyboard is hidden.
                    int heightDifference = screenHeight - (r.bottom - r.top);
                    boolean isKeyboardShowing = heightDifference > screenHeight / 3;

                    // If the status of the soft keyboard was previously displayed, it is now
                    // closed, or it was previously closed, it is now displayed, it means that the
                    // status of the soft keyboard has changed
                    if ((mIsSoftKeyboardShowing && !isKeyboardShowing)
                            || (!mIsSoftKeyboardShowing && isKeyboardShowing)) {
                        mIsSoftKeyboardShowing = isKeyboardShowing;
                        if (!mIsSoftKeyboardShowing) {
                            mBinding.reclSearchMnemonic.setVisibility(View.GONE);
                            mBinding.viewShow.setVisibility(View.GONE);
                        }
                    }
                };
        // Register layout change monitoring
        getActivity()
                .getWindow()
                .getDecorView()
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(mLayoutChangeListener);
    }

    private void getAllMnemonic() {
        PyObject allSeeds = null;
        try {
            allSeeds = Daemon.commands.callAttr("get_all_mnemonic");
            String content = allSeeds.toString();
            String seeds = content.replaceAll("\"", "");
            String[] pathArr = (seeds.substring(1, seeds.length() - 1)).split(",");
            seedList = Arrays.asList(pathArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void isSeed(String strNewseed) {
        PyObject isSeeds = null;
        try {
            isSeeds = Daemon.commands.callAttr("is_seed", strNewseed);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(HardWareExceptions.getExceptionString(e));
            return;
        }
        if (isSeeds.toBoolean()) {
            if (mOnImportMnemonicCallback != null) {
                mOnImportMnemonicCallback.onImportMnemonic(strNewseed);
            }
        } else {
            showToast(getString(R.string.helpword_wrong));
        }
    }

    private void past() {
        ClipboardManager clipboard =
                (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData data = clipboard.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                CharSequence text = data.getItemAt(0).getText();
                if (!Strings.isNullOrEmpty(text.toString())) {
                    String[] wordsList = text.toString().split("\\s+");
                    ArrayList<String> wordList = new ArrayList<>(Arrays.asList(wordsList));
                    if (wordList.size() > 0) {
                        switch (wordList.size()) {
                            case 12:
                                mBinding.editTwelve.setText(wordList.get(11));
                            case 11:
                                mBinding.editEleven.setText(wordList.get(10));
                            case 10:
                                mBinding.editTen.setText(wordList.get(9));
                            case 9:
                                mBinding.editNine.setText(wordList.get(8));
                            case 8:
                                mBinding.editEight.setText(wordList.get(7));
                            case 7:
                                mBinding.editSeven.setText(wordList.get(6));
                            case 6:
                                mBinding.editSix.setText(wordList.get(5));
                            case 5:
                                mBinding.editFive.setText(wordList.get(4));
                            case 4:
                                mBinding.editFour.setText(wordList.get(3));
                            case 3:
                                mBinding.editThree.setText(wordList.get(2));
                            case 2:
                                mBinding.editTwo.setText(wordList.get(1));
                            case 1:
                                mBinding.editOne.setText(wordList.get(0));
                                break;
                            default:
                                break;
                        }
                    } else {
                        showToast(getString(R.string.empty_help_words));
                    }
                }
            }
        }
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                if (mOnFinishViewCallBack != null) {
                    mOnFinishViewCallBack.onFinishView();
                }
                break;
            case R.id.btn_recovery:
                String strone = mBinding.editOne.getText().toString();
                String strtwo = mBinding.editTwo.getText().toString();
                String strthree = mBinding.editThree.getText().toString();
                String strfour = mBinding.editFour.getText().toString();
                String strfive = mBinding.editFive.getText().toString();
                String strsix = mBinding.editSix.getText().toString();
                String strseven = mBinding.editSeven.getText().toString();
                String streight = mBinding.editEight.getText().toString();
                String strnine = mBinding.editNine.getText().toString();
                String strten = mBinding.editTen.getText().toString();
                String streleven = mBinding.editEleven.getText().toString();
                String strtwelve = mBinding.editTwelve.getText().toString();
                String strNewseed =
                        strone + " " + strtwo + " " + strthree + " " + strfour + " " + strfive + " "
                                + strsix + " " + strseven + " " + streight + " " + strnine + " "
                                + strten + " " + streleven + " " + strtwelve;
                isSeed(strNewseed);
                break;
            case R.id.img_coin_type:
                if (BuildConfig.DEBUG) {
                    past();
                }
                break;
            default:
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
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            searchWordList.clear();
            if ((mBinding.editOne.length() > 0
                            && mBinding.editTwo.length() > 0
                            && mBinding.editThree.length() > 0
                            && mBinding.editFour.length() > 0)
                    && mBinding.editFive.length() > 0
                    && mBinding.editSix.length() > 0
                    && mBinding.editSeven.length() > 0
                    && mBinding.editEight.length() > 0
                    && mBinding.editNine.length() > 0
                    && mBinding.editTen.length() > 0
                    && mBinding.editEleven.length() > 0
                    && mBinding.editTwelve.length() > 0) {
                mBinding.btnRecovery.setEnabled(true);
                mBinding.btnRecovery.setBackground(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.btn_checked, null));
            } else {
                mBinding.btnRecovery.setEnabled(false);
                mBinding.btnRecovery.setBackground(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.btn_no_check, null));
            }
            if (!TextUtils.isEmpty(editable.toString())) {
                for (int i = 0; i < seedList.size(); i++) {
                    if (seedList.get(i).replaceAll(" ", "").startsWith(editable.toString())) {
                        searchWordList.add(seedList.get(i));
                    }
                }
            } else {
                mBinding.reclSearchMnemonic.setVisibility(View.GONE);
                mBinding.viewShow.setVisibility(View.GONE);
            }
            if (searchWordList != null && searchWordList.size() != 0) {
                mBinding.reclSearchMnemonic.setVisibility(View.VISIBLE);
                mBinding.viewShow.setVisibility(View.VISIBLE);
            }
            searchMnemonicAdapter.notifyDataSetChanged();
        }
    }

    public interface OnImportMnemonicCallback {
        void onImportMnemonic(String mnemonic);
    }
}
