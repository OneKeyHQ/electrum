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
import butterknife.BindView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.transaction.CheckChainDetailWebActivity;
import org.haobtc.onekey.adapter.OnekeyTxListAdapter;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.bean.TransactionSummaryVo;
import org.haobtc.onekey.business.blockBrowser.BlockBrowserManager;
import org.haobtc.onekey.business.chain.bitcoin.BitcoinService;
import org.haobtc.onekey.business.chain.ethereum.EthService;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.ui.base.BaseLazyFragment;
import org.haobtc.onekey.viewmodel.AppWalletViewModel;
import org.jetbrains.annotations.NotNull;

/**
 * 交易列表
 *
 * @author Onekey@QuincySx
 * @create 2021-01-14 1:52 PM
 */
public class TransactionListFragment extends BaseLazyFragment
        implements OnRefreshListener, OnLoadMoreListener {

    @StringDef({TransactionListType.ALL, TransactionListType.RECEIVE, TransactionListType.SEND})
    public @interface TransactionListType {
        String ALL = "all";
        String RECEIVE = "receive";
        String SEND = "send";
    }

    private static final String EXT_TYPE = "ext_type";
    private static final int PAGE_SIZE = 10;

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

    View mFooterMore;

    private List<TransactionSummaryVo> listBeans = new ArrayList<>(PAGE_SIZE);
    private OnekeyTxListAdapter onekeyTxListAdapter;
    private Disposable mLoadTxListDisposable;
    @TransactionListType private String mType = TransactionListType.ALL;
    private SchedulerProvide mSchedulerProvide = null;
    private CoinTypeProvider mCoinTypeProvider = null;

    private BitcoinService mBitcoinService = null;
    private EthService mEthService = null;

    private Animation mAnimation;
    private Vm.CoinType mCoinType;
    private int mTotalCount = 0;

    private AppWalletViewModel mAppWalletViewModel;

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
        mAppWalletViewModel = getApplicationViewModel(AppWalletViewModel.class);
        mAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.dialog_progress_anim);
        mType = getArguments().getString(EXT_TYPE, TransactionListType.ALL);
        onekeyTxListAdapter = new OnekeyTxListAdapter(listBeans, mType);
        reclTransactionList.setAdapter(onekeyTxListAdapter);
        onekeyTxListAdapter.setOnItemClickListener(
                (adapter, itemView, position) -> {
                    TransactionSummaryVo item = listBeans.get(position);
                    switch (item.getCoinType()) {
                        case BTC:
                            Intent intent =
                                    new Intent(requireContext(), DetailTransactionActivity.class);
                            intent.putExtra("hashDetail", item.getTxId());
                            intent.putExtra("txTime", item.getDate());
                            startActivity(intent);
                            break;
                        case ETH:
                            DetailETHTransactionActivity.start(
                                    requireContext(), item.getTxId(), item.getDate());
                            break;
                    }
                });
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

        if (mCoinType == Vm.CoinType.ETH) {
            refreshLayout.setOnRefreshListener(this);
            refreshLayout.setEnableLoadMore(false);
        } else {
            refreshLayout.setEnableLoadMore(true);
            refreshLayout.setOnRefreshListener(this);
            refreshLayout.setOnLoadMoreListener(this);
        }
        mFooterMore = View.inflate(getContext(), R.layout.view_transaction_list_footer, null);
        mFooterMore.setVisibility(View.GONE);
        onekeyTxListAdapter.addFooterView(mFooterMore);
        mFooterMore.setOnClickListener(
                v -> {
                    LocalWalletInfo value = mAppWalletViewModel.currentWalletInfo.getValue();
                    if (value != null) {
                        CheckChainDetailWebActivity.startWebUrl(
                                getContext(),
                                getString(R.string.check_trsaction),
                                BlockBrowserManager.INSTANCE.browseAddressUrl(
                                        mCoinType, value.getAddr()));
                    }
                });
    }

    @Override
    protected void onLazy() {
        super.onLazy();
        getTxList(mType);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mTotalCount = 0;
        if (mCoinType != Vm.CoinType.ETH) {
            refreshLayout.setEnableLoadMore(true);
        }
        getTxList(mType);
        mFooterMore.setVisibility(View.GONE);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getTxList(mType);
    }

    private void getTxList(String status) {
        if (mLoadTxListDisposable != null && !mLoadTxListDisposable.isDisposed()) {
            mLoadTxListDisposable.dispose();
        }
        mLoadTxListDisposable =
                Observable.create(
                                (ObservableOnSubscribe<List<TransactionSummaryVo>>)
                                        emitter -> {
                                            try {
                                                List<TransactionSummaryVo> summaryVoList;
                                                switch (mCoinType) {
                                                    default:
                                                    case BTC:
                                                        if (mBitcoinService == null) {
                                                            mBitcoinService = new BitcoinService();
                                                        }
                                                        summaryVoList =
                                                                mBitcoinService.getTxList(
                                                                        status,
                                                                        mTotalCount,
                                                                        PAGE_SIZE);
                                                        break;
                                                    case ETH:
                                                        if (mEthService == null) {
                                                            mEthService = new EthService();
                                                        }
                                                        summaryVoList =
                                                                mEthService.getTxList(
                                                                        status,
                                                                        mTotalCount,
                                                                        PAGE_SIZE);
                                                        break;
                                                }
                                                emitter.onNext(summaryVoList);
                                                emitter.onComplete();
                                            } catch (Exception e) {
                                                emitter.onError(e);
                                            }
                                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(
                                historyTx -> {
                                    refreshLayout.finishRefresh();
                                    if (mTotalCount > 0 || historyTx.size() > 0) {
                                        reclTransactionList.setVisibility(View.VISIBLE);
                                        tetNone.setVisibility(View.GONE);
                                    } else {
                                        reclTransactionList.setVisibility(View.GONE);
                                        tetNone.setVisibility(View.VISIBLE);
                                    }
                                    return historyTx;
                                })
                        .map(
                                historyTx -> {
                                    if (mTotalCount == 0) {
                                        listBeans.clear();
                                    }
                                    listBeans.addAll(historyTx);
                                    // 没有数据可以加载
                                    if (mCoinType == Vm.CoinType.ETH
                                            || (mTotalCount != 0 && mTotalCount == listBeans.size())
                                            || (mTotalCount == 0 && listBeans.size() < PAGE_SIZE)) {
                                        mFooterMore.setVisibility(View.VISIBLE);
                                        refreshLayout.finishLoadMore();
                                        refreshLayout.setEnableLoadMore(false);
                                    } else {
                                        refreshLayout.finishLoadMore();
                                    }
                                    mTotalCount = listBeans.size();
                                    return historyTx;
                                })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(getLoadWorkScheduler())
                        .doOnSubscribe(s -> showProgress())
                        .doFinally(this::dismissProgress)
                        .subscribe(
                                o -> {
                                    if (null != onekeyTxListAdapter) {
                                        onekeyTxListAdapter.notifyDataSetChanged();
                                    }
                                },
                                e -> {
                                    e.printStackTrace();
                                    String message = e.getMessage();
                                    if (message != null && !message.contains("SQLite")) {
                                        showToast(e.getMessage());
                                    }
                                    if (mTotalCount == 0 && null != reclTransactionList) {
                                        reclTransactionList.setVisibility(View.GONE);
                                    }
                                    if (mTotalCount == 0 && null != tetNone) {
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
        runOnUiThread(
                () -> {
                    if (ivProgress != null && mLoadProgress != null) {
                        ivProgress.startAnimation(mAnimation);
                        mAnimation.startNow();
                        mLoadProgress.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void dismissProgress() {
        runOnUiThread(
                () -> {
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
