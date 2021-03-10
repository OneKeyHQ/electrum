package org.haobtc.onekey.onekeys;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.orhanobut.logger.Logger;
import com.zy.multistatepage.MultiStateContainer;
import com.zy.multistatepage.MultiStatePage;
import com.zy.multistatepage.state.SuccessState;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.adapter.HotTokenAdapter;
import org.haobtc.onekey.adapter.MoreTokenAdapter;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.bean.TokenList;
import org.haobtc.onekey.business.wallet.TokenManager;
import org.haobtc.onekey.databinding.ActivityTokenManagerBinding;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.homepage.mindmenu.AddNewTokenActivity;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.status.NoSearchState;
import org.haobtc.onekey.viewmodel.AppWalletViewModel;

public class TokenManagerActivity extends BaseActivity
        implements HotTokenAdapter.onHotSwitchClick, MoreTokenAdapter.onMoreSwitchClick {

    private ActivityTokenManagerBinding mBinding;
    private HotTokenAdapter mHotTokenAdapter;
    private MoreTokenAdapter mMoreTokenAdapter;
    private View mHeaderView;
    private TextView mTokenNum;
    private RecyclerView mHotRecyclerView;
    private List<TokenList.ERCToken> mAllTokens;
    private List<TokenList.ERCToken> moreTokens;
    private List<TokenList.ERCToken> mHotTokens;
    private List<TokenList.ERCToken> mSearchTokens;
    private HotTokenAdapter mSearchAdapter;
    private LinearLayout headerLayout;
    private MultiStateContainer mMultiStateContainer;
    private AppWalletViewModel mAppWalletViewModel;
    private int mScrollState;
    private boolean mShouldScroll;
    private int mToPosition;

    public static void start(Context context) {
        context.startActivity(new Intent(context, TokenManagerActivity.class));
    }

    @Override
    public void init() {
        setLeftTitle(R.string.token_manager);
        mAppWalletViewModel =
                new ViewModelProvider(MyApplication.getInstance()).get(AppWalletViewModel.class);
        mAllTokens = new ArrayList<>();
        moreTokens = new ArrayList<>();
        mHotTokens = new ArrayList<>();
        mSearchTokens = new ArrayList<>();
        mMultiStateContainer = MultiStatePage.bindMultiState(mBinding.searchRecyclerview);
        getSelectTokenList(mAllTokens);
        mBinding.waveSlideBar.setOnTouchLetterChangeListener(
                letter -> {
                    if (moreTokens.size() > 0) {
                        for (int i = 0; i < moreTokens.size(); i++) {
                            if (moreTokens.get(i).symbol.startsWith(letter.toLowerCase())
                                    || moreTokens.get(i).symbol.startsWith(letter)) {
                                smoothMoveToPosition(mBinding.moreRecyclerview, i + 1);
                                break;
                            }
                        }
                    }
                });
        mBinding.moreRecyclerview.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(
                            @NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                            mShouldScroll = false;
                            smoothMoveToPosition(mBinding.moreRecyclerview, mToPosition);
                        }
                    }
                });
    }

    /** 滑动到指定位置 */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        int lastItem =
                mRecyclerView.getChildLayoutPosition(
                        mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    private void getSelectTokenList(List<TokenList.ERCToken> tokenList) {
        Disposable disposable =
                (Disposable)
                        Observable.create(
                                        (ObservableOnSubscribe<List<TokenList.ERCToken>>)
                                                emitter -> {
                                                    List<TokenList.ERCToken> tokenList1 =
                                                            new TokenManager().getTokenList();
                                                    emitter.onNext(tokenList1);
                                                    emitter.onComplete();
                                                })
                                .doOnSubscribe(show -> showProgress())
                                .doFinally(this::dismissProgress)
                                .subscribeOn(Schedulers.io())
                                .flatMap(
                                        (Function<
                                                        List<TokenList.ERCToken>,
                                                        ObservableSource<
                                                                PyResponse<
                                                                        List<TokenList.ERCToken>>>>)
                                                ercTokens -> {
                                                    mAllTokens.addAll(ercTokens);
                                                    moreTokens.addAll(mAllTokens.subList(0, 50));
                                                    return getCustomTokenList();
                                                })
                                .subscribeOn(Schedulers.io())
                                .flatMap(
                                        (Function<
                                                        PyResponse<List<TokenList.ERCToken>>,
                                                        ObservableSource<PyResponse<String>>>)
                                                listPyResponse -> {
                                                    moreTokens.addAll(listPyResponse.getResult());
                                                    mAllTokens.addAll(listPyResponse.getResult());
                                                    return getSelectTokenAddress();
                                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        response -> doSelectToken(response, moreTokens),
                                        e -> mToast(e.getMessage()));

        mCompositeDisposable.add(disposable);
    }

    private Observable<PyResponse<List<TokenList.ERCToken>>> getCustomTokenList() {
        return Observable.create(
                (ObservableOnSubscribe<PyResponse<List<TokenList.ERCToken>>>)
                        emitter -> {
                            PyResponse<List<TokenList.ERCToken>> response = PyEnv.getTokenList();
                            emitter.onNext(response);
                            emitter.onComplete();
                        });
    }

    private Observable<PyResponse<String>> getSelectTokenAddress() {
        return Observable.create(
                (ObservableOnSubscribe<PyResponse<String>>)
                        emitter -> {
                            PyResponse<String> response = PyEnv.getAddTokenAddress();
                            emitter.onNext(response);
                            emitter.onComplete();
                        });
    }

    private void doSelectToken(PyResponse<String> response, List<TokenList.ERCToken> tokenList) {
        if (Strings.isNullOrEmpty(response.getErrors())) {
            try {
                List<String> list = JSON.parseArray(response.getResult(), String.class);
                if (list.size() > 0) {
                    for (TokenList.ERCToken token : tokenList) {
                        for (String s : list) {
                            if (token.address.equalsIgnoreCase(s)) {
                                token.isAdd = true;
                                break;
                            }
                        }
                    }
                }
                mHotTokens.addAll(tokenList.subList(0, 10));
                sortByName();
                mMoreTokenAdapter = new MoreTokenAdapter(moreTokens, this);
                mSearchAdapter = new HotTokenAdapter(mSearchTokens, this);
                mBinding.searchRecyclerview.setAdapter(mSearchAdapter);
                mBinding.searchRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
                mBinding.moreRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
                mBinding.moreRecyclerview.setAdapter(mMoreTokenAdapter);
                initHeaderView(mHotTokens);
            } catch (Exception e) {
                e.toString();
            }
        } else {
            mToast(response.getErrors());
        }
    }

    private int getLetterIndex(List<TokenList.ERCToken> moreTokens, String letter) {
        for (int i = 0; i < moreTokens.size(); i++) {
            if (moreTokens.get(i).symbol.startsWith(letter)
                    || moreTokens.get(i).symbol.toUpperCase().startsWith(letter)) {
                return i;
            }
        }
        return -1;
    }

    private void initHeaderView(List<TokenList.ERCToken> hotTokens) {
        mHeaderView = getLayoutInflater().inflate(R.layout.ac_token_list_headerview, null);
        headerLayout = mHeaderView.findViewById(R.id.layout_header);
        mTokenNum = mHeaderView.findViewById(R.id.token_num);
        mHotRecyclerView = mHeaderView.findViewById(R.id.hot_recyclerView);
        mHotTokenAdapter = new HotTokenAdapter(hotTokens, this);
        mHotRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mHotRecyclerView.setAdapter(mHotTokenAdapter);
        mMoreTokenAdapter.setHeaderView(mHeaderView);
        mBinding.searchEt.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        String search = s.toString();
                        if (Strings.isNullOrEmpty(search)) {
                            mBinding.slideBarLayout.setVisibility(View.VISIBLE);
                            mHotTokenAdapter.setNewData(mHotTokens);
                            mHotRecyclerView.setAdapter(mHotTokenAdapter);
                            mBinding.searchRecyclerview.setVisibility(View.GONE);
                            mBinding.moreRecyclerview.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.slideBarLayout.setVisibility(View.GONE);
                            mBinding.searchRecyclerview.setVisibility(View.VISIBLE);
                            mBinding.moreRecyclerview.setVisibility(View.GONE);
                            packageSearchData(search);
                        }
                    }
                });
    }

    private void packageSearchData(String search) {
        mSearchTokens.clear();
        List<TokenList.ERCToken> tempData = new ArrayList<>();
        for (TokenList.ERCToken allToken : mAllTokens) {
            if (search.startsWith("0x")) {
                if (allToken.address.equalsIgnoreCase(search)) {
                    tempData.add(allToken);
                    break;
                }
            } else {
                if (allToken.symbol.startsWith(search.toLowerCase())
                        || allToken.symbol.startsWith(search.toUpperCase())
                        || allToken.name.startsWith(search.toLowerCase())
                        || allToken.name.startsWith(search.toUpperCase())) {
                    if (!isContainsToken(allToken, tempData)) {
                        tempData.add(allToken);
                    }
                }
            }
        }
        Logger.d("--->" + JSON.toJSONString(tempData));
        convertSearchData(tempData);
        if (tempData.size() == 0) {
            mMultiStateContainer.show(
                    NoSearchState.class,
                    noSearch -> {
                        noSearch.setOnAddTokenListener(
                                v -> {
                                    startActivityForResult(
                                            new Intent(mContext, AddNewTokenActivity.class), 100);
                                });
                    });
        } else {
            mMultiStateContainer.show(SuccessState.class);
            mSearchTokens.addAll(tempData);
        }
        mSearchAdapter.notifyDataSetChanged();
    }

    private void convertSearchData(List<TokenList.ERCToken> tempData) {
        if (moreTokens.size() > 0) {
            for (TokenList.ERCToken moreToken : moreTokens) {
                for (TokenList.ERCToken tempDatum : tempData) {
                    if (tempDatum.address.equalsIgnoreCase(moreToken.address)) {
                        tempDatum.isAdd = moreToken.isAdd;
                    }
                }
            }
        }
    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    public boolean enableViewBinding() {
        return true;
    }

    @Nullable
    @Override
    public View getLayoutView() {
        mBinding = ActivityTokenManagerBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected boolean showToolBar() {
        return true;
    }

    @Override
    public void onHotCheckedListener(TokenList.ERCToken item, boolean isChecked, int position) {
        if (isChecked) {
            addToken(item.symbol, item.address);
        } else {
            deleteToken(item.address);
        }
        if (!mBinding.moreRecyclerview.isComputingLayout()
                && RecyclerView.SCROLL_STATE_IDLE == mBinding.moreRecyclerview.getScrollState()) {
            if (isChecked) {
                if (!isContainsToken(item, moreTokens)) {
                    moreTokens.add(item);
                    sortByName();
                } else {
                    for (TokenList.ERCToken moreToken : moreTokens) {
                        if (item.address.equalsIgnoreCase(moreToken.address)) {
                            moreToken.isAdd = true;
                        }
                    }
                }
            } else {
                if (isContainsToken(item, moreTokens)) {
                    for (TokenList.ERCToken moreToken : moreTokens) {
                        if (item.address.equalsIgnoreCase(moreToken.address)) {
                            moreToken.isAdd = false;
                        }
                    }
                }
            }
            mMoreTokenAdapter.notifyDataSetChanged();
        }
    }

    private void sortByName() {
        moreTokens.sort(
                (o1, o2) -> {
                    String start = o1.symbol;
                    String end = o2.symbol;
                    return start.compareToIgnoreCase(end);
                });
    }

    @Override
    public void onMoreCheckedListener(TokenList.ERCToken item, boolean isChecked) {
        List<TokenList.ERCToken> data = mHotTokenAdapter.getData();
        for (TokenList.ERCToken datum : data) {
            if (item.address.equals(datum.address)) {
                datum.isAdd = isChecked;
                if (!mHotRecyclerView.isComputingLayout()
                        && RecyclerView.SCROLL_STATE_IDLE == mHotRecyclerView.getScrollState()) {
                    mHotTokenAdapter.notifyDataSetChanged();
                }
                break;
            }
        }
        if (isChecked) {
            addToken(item.symbol, item.address);
        } else {
            deleteToken(item.address);
        }
    }

    /**
     * 添加token
     *
     * @param symbol
     * @param address
     */
    private void addToken(String symbol, String address) {
        Disposable disposable =
                Observable.create(
                                (ObservableOnSubscribe<PyResponse<String>>)
                                        emitter -> {
                                            PyResponse<String> response =
                                                    PyEnv.addToken(symbol, address);
                                            emitter.onNext(response);
                                            emitter.onComplete();
                                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> {
                                    mAppWalletViewModel.submit(mAppWalletViewModel::refresh);
                                    if (!Strings.isNullOrEmpty(result.getErrors())) {
                                        mToast(result.getErrors());
                                    }
                                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * 删除token
     *
     * @param address
     */
    private void deleteToken(String address) {
        Disposable disposable =
                Observable.create(
                                (ObservableOnSubscribe<PyResponse<String>>)
                                        emitter -> {
                                            PyResponse<String> response =
                                                    PyEnv.deleteToken(address);
                                            emitter.onNext(response);
                                            emitter.onComplete();
                                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> {
                                    mAppWalletViewModel.submit(mAppWalletViewModel::refresh);
                                    if (!Strings.isNullOrEmpty(result.getErrors())) {
                                        mToast(result.getErrors());
                                    }
                                });
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 100) {
            if (data == null) {
                return;
            }
            mBinding.searchEt.setText("");
            String json = data.getStringExtra(AddNewTokenActivity.ASSET_JSON);
            TokenList.ERCToken token = JSON.parseObject(json, TokenList.ERCToken.class);
            token.isAdd = true;
            if (!isContainsToken(token, moreTokens)) {
                mAllTokens.add(token);
                moreTokens.add(token);
                sortByName();
            }
        }
    }

    private boolean isContainsToken(TokenList.ERCToken token, List<TokenList.ERCToken> tokens) {
        if (tokens.size() > 0) {
            for (TokenList.ERCToken moreToken : tokens) {
                if (token.address.equalsIgnoreCase(moreToken.address)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }
}
