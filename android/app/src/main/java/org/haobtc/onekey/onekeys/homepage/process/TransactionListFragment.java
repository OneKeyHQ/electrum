package org.haobtc.onekey.onekeys.homepage.process;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zy.multistatepage.MultiStateContainer;
import com.zy.multistatepage.MultiStatePage;
import com.zy.multistatepage.state.SuccessState;
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
import org.haobtc.onekey.databinding.FragmentTransactionListBinding;
import org.haobtc.onekey.ui.base.BaseLazyFragment;
import org.haobtc.onekey.ui.status.LoadErrorGoBrowserState;
import org.haobtc.onekey.ui.status.LoadProgressState;
import org.haobtc.onekey.ui.status.NoRecordState;
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

    View mFooterMore;
    private boolean isFirstLoad = true;

    private final List<TransactionSummaryVo> listBeans = new ArrayList<>(PAGE_SIZE);
    private OnekeyTxListAdapter onekeyTxListAdapter;
    private Disposable mLoadTxListDisposable;
    @TransactionListType private String mType = TransactionListType.ALL;
    private SchedulerProvide mSchedulerProvide = null;
    private CoinTypeProvider mCoinTypeProvider = null;

    private BitcoinService mBitcoinService = null;
    private EthService mEthService = null;

    private Vm.CoinType mCoinType;
    private int mTotalCount = 0;

    private AppWalletViewModel mAppWalletViewModel;
    private FragmentTransactionListBinding mBinding;
    private MultiStateContainer mMultiStateContainer;

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
    public boolean enableViewBinding() {
        return true;
    }

    @Nullable
    @Override
    public View getLayoutView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTransactionListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void init(View view) {
        mAppWalletViewModel = getApplicationViewModel(AppWalletViewModel.class);
        mType = getArguments().getString(EXT_TYPE, TransactionListType.ALL);
        onekeyTxListAdapter = new OnekeyTxListAdapter(listBeans, mType);
        mBinding.reclTransactionList.setAdapter(onekeyTxListAdapter);
        mMultiStateContainer =
                MultiStatePage.bindMultiState(
                        mBinding.smartRefreshLayout,
                        multiStateContainer -> {
                            onRefresh(mBinding.smartRefreshLayout);
                        });
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
            mBinding.smartRefreshLayout.setOnRefreshListener(this);
            mBinding.smartRefreshLayout.setEnableLoadMore(false);
        } else {
            mBinding.smartRefreshLayout.setEnableLoadMore(true);
            mBinding.smartRefreshLayout.setOnRefreshListener(this);
            mBinding.smartRefreshLayout.setOnLoadMoreListener(this);
        }
        mFooterMore = View.inflate(getContext(), R.layout.view_transaction_list_footer, null);
        mFooterMore.setVisibility(View.GONE);
        onekeyTxListAdapter.addFooterView(mFooterMore);
        mFooterMore.setOnClickListener(
                v -> {
                    startBrowser();
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
                                    if (mTotalCount > 0 || historyTx.size() > 0) {
                                        if (mTotalCount == 0) {
                                            mMultiStateContainer.show(SuccessState.class);
                                        }
                                    } else {
                                        mMultiStateContainer.show(NoRecordState.class);
                                    }
                                    return historyTx;
                                })
                        .map(
                                historyTx -> {
                                    mBinding.smartRefreshLayout.finishRefresh();
                                    if (mTotalCount == 0) {
                                        listBeans.clear();
                                    }
                                    listBeans.addAll(historyTx);
                                    // 没有数据可以加载
                                    if (mCoinType == Vm.CoinType.ETH
                                            || (mTotalCount != 0 && mTotalCount == listBeans.size())
                                            || (mTotalCount == 0 && listBeans.size() < PAGE_SIZE)) {
                                        mFooterMore.setVisibility(View.VISIBLE);
                                        mBinding.smartRefreshLayout.finishLoadMore();
                                        mBinding.smartRefreshLayout.setEnableLoadMore(false);
                                    } else {
                                        mBinding.smartRefreshLayout.finishLoadMore();
                                    }
                                    mTotalCount = listBeans.size();
                                    return historyTx;
                                })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(getLoadWorkScheduler())
                        .doOnSubscribe(s -> showProgress())
                        .subscribe(
                                o -> {
                                    if (null != onekeyTxListAdapter) {
                                        onekeyTxListAdapter.notifyDataSetChanged();
                                    }
                                },
                                e -> {
                                    if (mTotalCount == 0) {
                                        isFirstLoad = true;
                                        mMultiStateContainer.show(
                                                LoadErrorGoBrowserState.class,
                                                multiState -> {
                                                    multiState.setOnGotoBrowserListener(
                                                            v -> {
                                                                startBrowser();
                                                            });
                                                });
                                    }
                                });
    }

    private void startBrowser() {
        LocalWalletInfo value = mAppWalletViewModel.currentWalletInfo.getValue();
        if (value != null) {
            CheckChainDetailWebActivity.startWebUrl(
                    getContext(),
                    getString(R.string.check_trsaction),
                    BlockBrowserManager.INSTANCE.browseAddressUrl(mCoinType, value.getAddr()));
        }
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
                    if (isFirstLoad) {
                        isFirstLoad = false;
                        mMultiStateContainer.show(LoadProgressState.class);
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
