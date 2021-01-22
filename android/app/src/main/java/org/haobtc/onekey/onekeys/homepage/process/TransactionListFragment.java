package org.haobtc.onekey.onekeys.homepage.process;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.haobtc.onekey.R;
import org.haobtc.onekey.adapter.OnekeyTxListAdapter;
import org.haobtc.onekey.bean.MaintrsactionlistEvent;
import org.haobtc.onekey.bean.TransactionSummaryVo;
import org.haobtc.onekey.business.chain.bitcoin.BitcoinService;
import org.haobtc.onekey.business.chain.ethereum.EthService;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.ui.base.BaseLazyFragment;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.MyDialog;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 交易列表
 *
 * @author Onekey@QuincySx
 * @create 2021-01-14 1:52 PM
 */
public class TransactionListFragment extends BaseLazyFragment implements OnRefreshListener, OnLoadMoreListener {

    @StringDef({TransactionListType.ALL, TransactionListType.RECEIVE, TransactionListType.SEND})
    @interface TransactionListType {
        String ALL = "all";
        String RECEIVE = "receive";
        String SEND = "send";
    }

    private static final String EXT_TYPE = "ext_type";

    public static TransactionListFragment getInstance(@TransactionListType String type) {
        TransactionListFragment transactionListFragment = new TransactionListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXT_TYPE, type);
        transactionListFragment.setArguments(bundle);
        return transactionListFragment;
    }

    @BindView(R.id.recl_transaction_list)
    RecyclerView reclTransactionList;
    @BindView(R.id.tet_None)
    TextView tetNone;
    @BindView(R.id.smart_RefreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.loadProgress)
    View mLoadProgress;
    @BindView(R.id.ivProgress)
    View ivProgress;

    private List<TransactionSummaryVo> listBeans;
    private OnekeyTxListAdapter onekeyTxListAdapter;
    private Disposable mLoadTxListDisposable;
    @TransactionListType
    private String mType = TransactionListType.ALL;
    private SchedulerProvide mSchedulerProvide = null;
    private CoinTypeProvider mCoinTypeProvider = null;

    private BitcoinService mBitcoinService = null;
    private EthService mEthService = null;

    private Animation mAnimation;
    private Vm.CoinType mCoinType;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof SchedulerProvide) {
            mSchedulerProvide = ((SchedulerProvide) context);
        }
        if (context instanceof CoinTypeProvider) {
            mCoinTypeProvider = ((CoinTypeProvider) context);
        }
    }

    @Override
    public void init(View view) {
        mAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.dialog_progress_anim);
        mType = getArguments().getString(EXT_TYPE, TransactionListType.ALL);
        listBeans = new ArrayList<>();
        onekeyTxListAdapter = new OnekeyTxListAdapter(listBeans);
        reclTransactionList.setAdapter(onekeyTxListAdapter);
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_transaction_list;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mCoinTypeProvider != null) {
            mCoinType = mCoinTypeProvider.getCurrentCoinType();
        } else {
            mCoinType = Vm.CoinType.BTC;
        }
    }

    @Override
    protected void onLazy() {
        super.onLazy();
        getTxList(mType);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getTxList(mType);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
    }

    private void getTxList(String status) {
        if (mLoadTxListDisposable != null && !mLoadTxListDisposable.isDisposed()) {
            mLoadTxListDisposable.dispose();
        }
        mLoadTxListDisposable = Observable
                .create((ObservableOnSubscribe<List<TransactionSummaryVo>>) emitter -> {
                    try {
                        List<TransactionSummaryVo> summaryVoList;
                        switch (mCoinType) {
                            default:
                            case BTC:
                                if (mBitcoinService == null) {
                                    mBitcoinService = new BitcoinService();
                                }
                                summaryVoList = mBitcoinService.getTxList(status);
                                break;
                            case ETH:
                                if (mEthService == null) {
                                    mEthService = new EthService();
                                }
                                summaryVoList = mEthService.getTxList(status);
                                break;
                        }
                        emitter.onNext(summaryVoList);
                        emitter.onComplete();
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(historyTx -> {
                    refreshLayout.finishRefresh();
                    if (historyTx.size() > 0) {
                        reclTransactionList.setVisibility(View.VISIBLE);
                        tetNone.setVisibility(View.GONE);
                    } else {
                        reclTransactionList.setVisibility(View.GONE);
                        tetNone.setVisibility(View.VISIBLE);
                    }
                    return historyTx;
                })
                .observeOn(Schedulers.io())
                .map(historyTx -> {
                    listBeans.addAll(historyTx);
                    return historyTx;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(getLoadWorkScheduler())
                .doOnSubscribe(s -> showProgress())
                .doFinally(this::dismissProgress)
                .subscribe(o -> {
                    if (null != onekeyTxListAdapter) {
                        onekeyTxListAdapter.notifyDataSetChanged();
                        onekeyTxListAdapter.setOnItemClickListener((adapter, view, position) -> {
                            Intent intent = new Intent(requireContext(), DetailTransactionActivity.class);
                            intent.putExtra("hashDetail", listBeans.get(position).getTxId());
                            intent.putExtra("txTime", listBeans.get(position).getDate());
                            startActivity(intent);
                        });
                    }
                }, e -> {
                    e.printStackTrace();
                    String message = e.getMessage();
                    if (message != null && !message.contains("SQLite")) {
                        showToast(e.getMessage());
                    }
                    if (null != reclTransactionList) {
                        reclTransactionList.setVisibility(View.GONE);
                    }
                    if (null != tetNone) {
                        tetNone.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadTxListDisposable != null && !mLoadTxListDisposable.isDisposed()) {
            mLoadTxListDisposable.dispose();
        }
    }

    private void showProgress() {
        runOnUiThread(() -> {
            if (ivProgress != null && mLoadProgress != null) {
                ivProgress.startAnimation(mAnimation);
                mAnimation.startNow();
                mLoadProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void dismissProgress() {
        runOnUiThread(() -> {
            if (mLoadProgress != null) {
                mLoadProgress.setVisibility(View.GONE);
                mAnimation.cancel();
            }
        });
    }

    private Scheduler getLoadWorkScheduler() {
        if (mSchedulerProvide != null) {
            return mSchedulerProvide.getScheduler();
        } else {
            return Schedulers.io();
        }
    }

    interface SchedulerProvide {
        @NonNull
        Scheduler getScheduler();
    }


    public interface CoinTypeProvider {
        Vm.CoinType getCurrentCoinType();
    }
}
