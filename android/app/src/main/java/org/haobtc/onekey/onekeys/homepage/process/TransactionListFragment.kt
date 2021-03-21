package org.haobtc.onekey.onekeys.homepage.process

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringDef
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage.bindMultiState
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.SuccessState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.haobtc.onekey.R
import org.haobtc.onekey.activities.transaction.CheckChainDetailWebActivity
import org.haobtc.onekey.adapter.OnekeyTxListAdapter
import org.haobtc.onekey.bean.Assets
import org.haobtc.onekey.bean.ERC20Assets
import org.haobtc.onekey.bean.TransactionSummaryVo
import org.haobtc.onekey.business.blockBrowser.BlockBrowserManager.browseAddressUrl
import org.haobtc.onekey.business.chain.bitcoin.BitcoinService
import org.haobtc.onekey.business.chain.ethereum.EthService
import org.haobtc.onekey.constant.Vm.CoinType
import org.haobtc.onekey.databinding.FragmentTransactionListBinding
import org.haobtc.onekey.ui.base.BaseLazyFragment
import org.haobtc.onekey.ui.status.LoadErrorGoBrowserState
import org.haobtc.onekey.ui.status.LoadProgressState
import org.haobtc.onekey.ui.status.NoRecordState
import org.haobtc.onekey.utils.NavUtils
import org.haobtc.onekey.viewmodel.AppWalletViewModel
import java.util.*
import java.util.concurrent.Callable

/**
 * 交易列表
 *
 * @author Onekey@QuincySx
 * @create 2021-01-14 1:52 PM
 */
class TransactionListFragment : BaseLazyFragment(), OnRefreshListener, OnLoadMoreListener {

  @StringDef(ALL, RECEIVE, SEND)
  annotation class TransactionListType

  private var mFooterMore: View? = null
  private var isFirstLoad = true
  private val listBeans: MutableList<TransactionSummaryVo> = ArrayList(PAGE_SIZE)
  private var onekeyTxListAdapter: OnekeyTxListAdapter? = null
  private var mLoadTxListDisposable: Disposable? = null

  @TransactionListType
  private var mType = ALL
  private var mSchedulerProvide: SchedulerProvide? = null
  private var mCoinTypeProvider: AssetsProvider? = null
  private val mBitcoinService by lazy {
    BitcoinService()
  }
  private val mEthService by lazy {
    EthService()
  }
  private lateinit var mAssets: Assets
  private var mTotalCount = 0
  private var mAppWalletViewModel: AppWalletViewModel? = null
  private lateinit var mBinding: FragmentTransactionListBinding
  private var mMultiStateContainer: MultiStateContainer? = null
  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is SchedulerProvide) {
      mSchedulerProvide = context
    }
    if (context is AssetsProvider) {
      mCoinTypeProvider = context
    }
  }

  override fun enableViewBinding(): Boolean {
    return true
  }

  override fun getLayoutView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?): View {
    mBinding = FragmentTransactionListBinding.inflate(inflater, container, false)
    return mBinding.root
  }

  override fun init(view: View) {
    mAppWalletViewModel = getApplicationViewModel(AppWalletViewModel::class.java)
    mType = arguments?.getString(EXT_TYPE, ALL) ?: ALL
    onekeyTxListAdapter = OnekeyTxListAdapter(listBeans, mType)
    mBinding.reclTransactionList.adapter = onekeyTxListAdapter
    mMultiStateContainer = bindMultiState(
        mBinding.smartRefreshLayout,
        OnRetryEventListener { onRefresh(mBinding.smartRefreshLayout) })
    onekeyTxListAdapter?.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _: BaseQuickAdapter<*, *>?, _: View?, position: Int ->
      val item = listBeans[position]
      when (item.coinType.chainType) {
        CoinType.BTC.chainType -> DetailTransactionActivity.start(
            requireContext(), item.txId, item.getDate())
        CoinType.ETH.chainType -> {
          val jsonStr = Gson().toJson(item)
          DetailETHTransactionActivity.start(requireContext(),item.coinType, jsonStr)
        }
      }
    }
  }

  override fun getContentViewId(): Int {
    return R.layout.fragment_transaction_list
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (mCoinTypeProvider != null) {
      mAssets = mCoinTypeProvider?.currentAssets
          ?: throw RuntimeException("Please implement AssetsProvider Interface")
    }
    if (mAssets.coinType.chainType == CoinType.ETH.chainType) {
      mBinding.smartRefreshLayout.setOnRefreshListener(this)
      mBinding.smartRefreshLayout.setEnableLoadMore(false)
    } else {
      mBinding.smartRefreshLayout.setEnableLoadMore(true)
      mBinding.smartRefreshLayout.setOnRefreshListener(this)
      mBinding.smartRefreshLayout.setOnLoadMoreListener(this)
    }
    mFooterMore = View.inflate(context, R.layout.view_transaction_list_footer, null)
    mFooterMore?.visibility = View.GONE
    onekeyTxListAdapter?.addFooterView(mFooterMore)
    mFooterMore?.setOnClickListener { startBrowser() }
  }

  override fun onLazy() {
    super.onLazy()
    getTxList(mType)
  }

  override fun onRefresh(refreshLayout: RefreshLayout) {
    mTotalCount = 0
    if (mAssets.coinType.chainType != CoinType.ETH.chainType) {
      refreshLayout.setEnableLoadMore(true)
    }
    getTxList(mType)
    mFooterMore?.visibility = View.GONE
  }

  override fun onLoadMore(refreshLayout: RefreshLayout) {
    getTxList(mType)
  }

  private fun getTxList(status: String) {
    if (mLoadTxListDisposable != null && !mLoadTxListDisposable!!.isDisposed) {
      mLoadTxListDisposable!!.dispose()
    }
    mLoadTxListDisposable = Single.fromCallable(
        Callable {
          when (mAssets.coinType.chainType) {
            CoinType.ETH.chainType -> {
              var contractAddress: String? = null
              if (mAssets is ERC20Assets) {
                contractAddress = (mAssets as ERC20Assets)
                    .contractAddress
              }
              mEthService.getTxList(
                  mAssets.coinType,
                  status,
                  contractAddress,
                  mTotalCount,
                  PAGE_SIZE)
            }
            CoinType.BTC.chainType -> {
              mBitcoinService.getTxList(
                  status,
                  mTotalCount,
                  PAGE_SIZE)
            }
            else -> {
              mBitcoinService.getTxList(
                  status,
                  mTotalCount,
                  PAGE_SIZE)
            }
          }
        } as Callable<List<TransactionSummaryVo>>)
        .observeOn(AndroidSchedulers.mainThread())
        .map { historyTx: List<TransactionSummaryVo> ->
          if (mTotalCount > 0 || historyTx.isNotEmpty()) {
            if (mTotalCount == 0) {
              mMultiStateContainer!!.show(SuccessState::class.java)
            }
          } else {
            mMultiStateContainer!!.show(NoRecordState::class.java)
          }
          historyTx
        }
        .map { historyTx: List<TransactionSummaryVo>? ->
          mBinding.smartRefreshLayout.finishRefresh()
          if (mTotalCount == 0) {
            listBeans.clear()
          }
          listBeans.addAll(historyTx!!)
          // 没有数据可以加载
          if (mAssets.coinType == CoinType.ETH || (mTotalCount != 0
                  && mTotalCount == listBeans.size)
              || (mTotalCount == 0
                  && listBeans.size < PAGE_SIZE)) {
            mFooterMore!!.visibility = View.VISIBLE
            mBinding.smartRefreshLayout.finishLoadMore()
            mBinding.smartRefreshLayout.setEnableLoadMore(
                false)
          } else {
            mBinding.smartRefreshLayout.finishLoadMore()
          }
          mTotalCount = listBeans.size
          historyTx
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(loadWorkScheduler)
        .doOnSubscribe { showProgress() }
        .subscribe(
            {
              if (null != onekeyTxListAdapter) {
                onekeyTxListAdapter!!.notifyDataSetChanged()
              }
            }
        ) {
          if (mTotalCount == 0) {
            isFirstLoad = true
            mMultiStateContainer!!.show(
                LoadErrorGoBrowserState::class.java
            ) { multiState: LoadErrorGoBrowserState -> multiState.setOnGotoBrowserListener { startBrowser() } }
          }
        } as Disposable
  }

  private fun startBrowser() {
    val value = mAppWalletViewModel!!.currentWalletAccountInfo.value
    if (value != null) {
      if (mAssets is ERC20Assets) {
        CheckChainDetailWebActivity.startWebUrl(
            context,
            getString(R.string.check_trsaction),
            browseAddressUrl(
                mAssets.coinType,
                (mAssets as ERC20Assets).contractAddress,
                value.address))
      } else {
        CheckChainDetailWebActivity.startWebUrl(
            context,
            getString(R.string.check_trsaction),
            browseAddressUrl(
                mAssets.coinType, value.address))
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (mLoadTxListDisposable != null && !mLoadTxListDisposable!!.isDisposed) {
      mLoadTxListDisposable!!.dispose()
    }
  }

  private fun showProgress() {
    runOnUiThread {
      if (isFirstLoad) {
        isFirstLoad = false
        mMultiStateContainer!!.show(LoadProgressState::class.java)
      }
    }
  }

  private val loadWorkScheduler: Scheduler
    get() = if (mSchedulerProvide != null) {
      mSchedulerProvide!!.scheduler
    } else {
      Schedulers.io()
    }

  internal interface SchedulerProvide {
    val scheduler: Scheduler
  }

  interface AssetsProvider {
    val currentAssets: Assets?
  }

  companion object {
    private const val EXT_TYPE = "ext_type"
    private const val PAGE_SIZE = 10

    const val ALL = "all"
    const val RECEIVE = "receive"
    const val SEND = "send"

    @JvmStatic
    fun getInstance(@TransactionListType type: String?): TransactionListFragment {
      val transactionListFragment = TransactionListFragment()
      val bundle = Bundle()
      bundle.putString(EXT_TYPE, type)
      transactionListFragment.arguments = bundle
      return transactionListFragment
    }
  }
}
