package org.haobtc.onekey.onekeys.homepage;

import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE;

import android.Manifest;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.common.base.Strings;
import com.scwang.smartrefresh.layout.util.SmartUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.sign.SignActivity;
import org.haobtc.onekey.activities.transaction.CheckChainDetailWebActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.bean.WalletAccountInfo;
import org.haobtc.onekey.business.assetsLogo.AssetsLogo;
import org.haobtc.onekey.business.wallet.AccountManager;
import org.haobtc.onekey.business.wallet.SystemConfigManager;
import org.haobtc.onekey.constant.StringConstant;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.BackupCompleteEvent;
import org.haobtc.onekey.event.BackupEvent;
import org.haobtc.onekey.event.BleConnectedEvent;
import org.haobtc.onekey.event.BleConnectionEx;
import org.haobtc.onekey.event.FixWalletNameEvent;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.event.RefreshEvent;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.TokenManagerActivity;
import org.haobtc.onekey.onekeys.backup.BackupGuideActivity;
import org.haobtc.onekey.onekeys.dialog.RecoverHdWalletActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateLocalMainWalletActivity;
import org.haobtc.onekey.onekeys.homepage.process.HdWalletDetailActivity;
import org.haobtc.onekey.onekeys.homepage.process.ReceiveHDActivity;
import org.haobtc.onekey.onekeys.homepage.process.SendEthActivity;
import org.haobtc.onekey.onekeys.homepage.process.SendHdActivity;
import org.haobtc.onekey.onekeys.homepage.process.TransactionDetailWalletActivity;
import org.haobtc.onekey.ui.activity.OnekeyScanQrActivity;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;
import org.haobtc.onekey.ui.adapter.WalletAssetsAdapter;
import org.haobtc.onekey.ui.base.BaseFragment;
import org.haobtc.onekey.ui.dialog.BackupDialog;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.NavUtils;
import org.haobtc.onekey.viewmodel.AppWalletViewModel;
import org.haobtc.onekey.viewmodel.NetworkViewModel;

/** @author jinxiaomin */
public class WalletFragment extends BaseFragment implements TextWatcher {

    private static final int REQUEST_CODE = 0;
    private static int currentAction;

    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.layout_nested_scroll)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.view_slide_interval_line)
    View mViewSlideIntervalLine;

    @BindView(R.id.text_wallet_name)
    TextView textWalletName;

    @BindView(R.id.img_type)
    ImageView imgType;

    @BindView(R.id.rel_check_wallet)
    RelativeLayout relCheckWallet;

    @BindView(R.id.img_scan)
    ImageView imgScan;

    @BindView(R.id.img_Add)
    ImageView imgAdd;

    @BindView(R.id.img_add_token)
    ImageView imgAddToken;

    @BindView(R.id.rel_create_hd)
    RelativeLayout relCreateHd;

    @BindView(R.id.rel_recovery_hd)
    RelativeLayout relRecoveryHd;

    @BindView(R.id.rel_pair_hard)
    RelativeLayout relPairHard;

    @BindView(R.id.lin_no_wallet)
    LinearLayout linearNoWallet;

    @BindView(R.id.img_bottom)
    ImageView imgBottom;

    @BindView(R.id.text_hard)
    TextView textHard;

    @BindView(R.id.text_amount)
    TextView tetAmount;

    @BindView(R.id.text_amount_unit)
    TextView tetAmountUnit;

    @BindView(R.id.text_amount_star)
    TextView textStar;

    @BindView(R.id.img_check_money)
    ImageView imgCheckMoney;

    @BindView(R.id.layout_hidden_assets)
    View layouthHiddenAssets;

    @BindView(R.id.layout_total_amount)
    View layoutTotalAmount;

    @BindView(R.id.linear_send)
    LinearLayout linearSend;

    @BindView(R.id.linear_receive)
    LinearLayout linearReceive;

    @BindView(R.id.linear_sign)
    LinearLayout linearSign;

    @BindView(R.id.lin_have_wallet)
    LinearLayout linearHaveWallet;

    @BindView(R.id.money)
    ImageView money;

    @BindView(R.id.rel_now_back_up)
    android.widget.RelativeLayout relNowBackUp;

    @BindView(R.id.recl_hd_list)
    RecyclerView reclHdList;

    @BindView(R.id.lin_wallet_list)
    LinearLayout linearWalletList;

    private RxPermissions rxPermissions;
    private Disposable mPermissionsDisposable;

    @Deprecated private String nowType;
    private boolean isBackup;
    private String bleMac;
    private boolean isAddHd;
    private NetworkViewModel mNetworkViewModel;
    private AppWalletViewModel mAppWalletViewModel;
    private SystemConfigManager mSystemConfigManager;
    private BackupDialog mBackupDialog;
    private AccountManager mAccountManager;
    private AssetsLogo mAssetsLogo;
    private String currentDeviceId = "";
    private final WalletAssetsAdapter mWalletAssetsAdapter = new WalletAssetsAdapter();

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {
        mAssetsLogo = new AssetsLogo();
        mNetworkViewModel = getApplicationViewModel(NetworkViewModel.class);
        mAppWalletViewModel = getApplicationViewModel(AppWalletViewModel.class);
        mSystemConfigManager = new SystemConfigManager(requireContext());
        rxPermissions = new RxPermissions(this);
        mAccountManager = new AccountManager(getActivity());
        tetAmount.addTextChangedListener(this);
        initAdapter();
        initViewModelValue();
        initNestedScrollViewListener();
        initPrivacyMode();
        initRefresh();
    }

    private void initRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(
                () -> {
                    Single.fromCallable(
                                    () -> {
                                        mAppWalletViewModel.refreshWalletInfo();
                                        return true;
                                    })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    t -> {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    },
                                    t -> {})
                            .isDisposed();
                });
    }

    private void initPrivacyMode() {
        imgCheckMoney.setSelected(true);
        layouthHiddenAssets.setOnClickListener(
                v -> {
                    if (imgCheckMoney.isSelected()) {
                        layoutTotalAmount.setVisibility(View.GONE);
                        textStar.setVisibility(View.VISIBLE);
                        mWalletAssetsAdapter.setPrivacyMode(true);
                    } else {
                        layoutTotalAmount.setVisibility(View.VISIBLE);
                        textStar.setVisibility(View.GONE);
                        mWalletAssetsAdapter.setPrivacyMode(false);
                    }
                    imgCheckMoney.setSelected(!imgCheckMoney.isSelected());
                });
    }

    private void initAdapter() {
        HorizontalDividerItemDecoration itemDecoration =
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(
                                ResourcesCompat.getColor(
                                        getResources(), R.color.color_select_wallet_divider, null))
                        .sizeResId(R.dimen.line_hight)
                        .margin(SmartUtil.dp2px(12F), SmartUtil.dp2px(12F))
                        .showLastDivider()
                        .build();

        reclHdList.setAdapter(mWalletAssetsAdapter);
        reclHdList.setNestedScrollingEnabled(false);
        reclHdList.addItemDecoration(itemDecoration);
        mWalletAssetsAdapter.setOnItemClickListener(
                (position, assets) -> {
                    WalletAccountInfo accountInfo =
                            mAppWalletViewModel.currentWalletAccountInfo.getValue();
                    if (accountInfo == null) {
                        return;
                    }
                    TransactionDetailWalletActivity.start(
                            getContext(), accountInfo.getId(), assets.uniqueId());
                });
    }

    private void initNestedScrollViewListener() {
        mNestedScrollView.setOnScrollChangeListener(
                (View.OnScrollChangeListener)
                        (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                            if (scrollY == 0) {
                                // 滚动到顶，隐藏间隔线
                                mViewSlideIntervalLine.setVisibility(View.INVISIBLE);
                            } else {
                                mViewSlideIntervalLine.setVisibility(View.VISIBLE);
                            }
                        });
    }

    /**
     * * init layout
     *
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.fragment_wallet;
    }

    private void initViewModelValue() {
        mNetworkViewModel
                .haveNet()
                .observe(
                        this,
                        state -> {
                            if (state) {
                                mAppWalletViewModel.submit(mAppWalletViewModel::refresh);
                            }
                        });
        mAppWalletViewModel.existsWallet.observe(
                this,
                exists -> {
                    mSwipeRefreshLayout.setEnabled(exists);
                    if (exists) {
                        // have wallet
                        linearNoWallet.setVisibility(View.GONE);
                        imgBottom.setVisibility(View.GONE);
                        linearHaveWallet.setVisibility(View.VISIBLE);
                        linearWalletList.setVisibility(View.VISIBLE);
                        imgScan.setVisibility(View.VISIBLE);
                    } else {
                        // no wallet
                        try {
                            Daemon.commands.callAttr("set_currency", "CNY");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        imgType.setImageDrawable(
                                ContextCompat.getDrawable(requireContext(), R.drawable.loco_round));
                        linearNoWallet.setVisibility(View.VISIBLE);
                        imgBottom.setVisibility(View.VISIBLE);
                        linearHaveWallet.setVisibility(View.GONE);
                        linearWalletList.setVisibility(View.GONE);
                        imgScan.setVisibility(View.GONE);
                        relNowBackUp.setVisibility(View.GONE);
                        PreferencesManager.put(
                                getContext(),
                                "Preferences",
                                org.haobtc.onekey.constant.Constant.HAS_LOCAL_HD,
                                false);
                        textWalletName.setText(R.string.no_use_wallet);
                        mSystemConfigManager.setNeedPopBackUpDialog(true);
                    }
                });
        mAppWalletViewModel.currentWalletAccountInfo.observe(
                this,
                walletAccountInfo -> {
                    if (walletAccountInfo != null) {
                        textWalletName.setText(walletAccountInfo.getName());
                        showTypeInfo(walletAccountInfo);
                        imgAddToken.setVisibility(
                                walletAccountInfo.getCoinType().supportTokens
                                        ? View.VISIBLE
                                        : View.GONE);
                        whetherBackup();
                    }
                });
        mAppWalletViewModel.currentWalletTotalBalanceFiat.observe(
                this,
                totalBalance -> {
                    tetAmountUnit.setText(totalBalance.getSymbol());
                    tetAmount.setText(totalBalance.getBalanceFormat());
                });
        mAppWalletViewModel.currentWalletAssetsList.observe(
                this,
                assets -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mWalletAssetsAdapter.submitList(assets.toList());
                });
    }

    private void showTypeInfo(WalletAccountInfo localWalletInfo) {
        int walletType = localWalletInfo.getWalletType();
        Vm.CoinType coinType = localWalletInfo.getCoinType();

        nowType = localWalletInfo.getType();
        if (walletType == Vm.WalletType.MAIN) {
            PreferencesManager.put(
                    getContext(),
                    "Preferences",
                    org.haobtc.onekey.constant.Constant.HAS_LOCAL_HD,
                    true);
        }

        imgType.setImageDrawable(
                ContextCompat.getDrawable(
                        requireContext(), mAssetsLogo.getLogoResources(coinType)));

        if (walletType == Vm.WalletType.HARDWARE) {
            textHard.setVisibility(View.VISIBLE);
            if (coinType == Vm.CoinType.BTC) {
                linearSign.setVisibility(View.VISIBLE);
            } else {
                linearSign.setVisibility(View.GONE);
            }
            String deviceId = localWalletInfo.getDeviceId();
            currentDeviceId = deviceId;
            String deviceInfo =
                    PreferencesManager.get(
                                    getContext(),
                                    org.haobtc.onekey.constant.Constant.DEVICES,
                                    deviceId,
                                    "")
                            .toString();
            if (!Strings.isNullOrEmpty(deviceInfo)) {
                HardwareFeatures info = HardwareFeatures.objectFromData(deviceInfo);
                String bleName = info.getBleName();
                String label = info.getLabel();
                bleMac =
                        PreferencesManager.get(
                                        getContext(),
                                        org.haobtc.onekey.constant.Constant.BLE_INFO,
                                        bleName,
                                        "")
                                .toString();
                textHard.setText(Strings.isNullOrEmpty(label) ? bleName : label);
            } else {
                textHard.setText(deviceId);
            }
        } else {
            linearSign.setVisibility(View.GONE);
            textHard.setVisibility(View.GONE);
        }
    }

    /** 判断当前钱包是否需要备份 */
    private void whetherBackup() {
        try {
            isBackup = PyEnv.hasBackup(getActivity());
            if (isBackup) {
                relNowBackUp.setVisibility(View.GONE);
            } else {
                // no back up
                relNowBackUp.setVisibility(View.VISIBLE);
                whetherBackupDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
            relNowBackUp.setVisibility(View.GONE);
        }
    }

    /** 判断备份提示弹窗是否需要出现 */
    private void whetherBackupDialog() {
        // whether to pop backup dialog
        if (mSystemConfigManager.getNeedPopBackUpDialog()) {
            if (mBackupDialog != null && !mBackupDialog.isDetached()) {
                mBackupDialog.dismiss();
            }
            mBackupDialog = new BackupDialog();
            mBackupDialog.show(getChildFragmentManager(), "backup");
        }
    }

    /** 统一处理硬件连接 */
    private void deal(@IdRes int id) {
        if (nowType.contains(org.haobtc.onekey.constant.Constant.HW)) {
            currentAction = id;
            if (Strings.isNullOrEmpty(bleMac)) {
                Toast.makeText(
                                getContext(),
                                getString(R.string.not_found_device_msg),
                                Toast.LENGTH_SHORT)
                        .show();
            } else {
                Intent intent2 = new Intent(getActivity(), SearchDevicesActivity.class);
                intent2.putExtra(
                        org.haobtc.onekey.constant.Constant.SEARCH_DEVICE_MODE,
                        org.haobtc.onekey.constant.Constant.SearchDeviceMode.MODE_PREPARE);
                intent2.putExtra(org.haobtc.onekey.constant.Constant.DEVICE_ID, currentDeviceId);
                startActivity(intent2);
                EventBus.getDefault().removeAllStickyEvents();
                BleManager.getInstance(getActivity()).connDevByMac(bleMac);
            }
            return;
        }
        toNext(id);
    }

    /** 处理具体业务 */
    private void toNext(int id) {
        WalletAccountInfo value = mAppWalletViewModel.currentWalletAccountInfo.getValue();
        switch (id) {
            case R.id.linear_send:
                if (null != value) {
                    if (value.getCoinType().callFlag.equalsIgnoreCase(Vm.CoinType.BTC.callFlag)) {
                        Intent intent = new Intent(getActivity(), SendHdActivity.class);
                        intent.putExtra("hdWalletName", textWalletName.getText().toString());
                        startActivity(intent);
                    } else if (value.getCoinType()
                            .callFlag
                            .equalsIgnoreCase(Vm.CoinType.ETH.callFlag)) {
                        SendEthActivity.start(getActivity(), value.getId());
                    }
                }
                break;
            case R.id.linear_receive:
                if (value != null) {
                    ReceiveHDActivity.start(getActivity(), value.getId());
                }
                break;
            case R.id.linear_sign:
                Intent intent8 = new Intent(getActivity(), SignActivity.class);
                intent8.putExtra(
                        org.haobtc.onekey.constant.Constant.WALLET_LABEL,
                        textWalletName.getText().toString());
                if (nowType.contains(org.haobtc.onekey.constant.Constant.HW)) {
                    intent8.putExtra(
                            org.haobtc.onekey.constant.Constant.WALLET_TYPE,
                            org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE_PERSONAL);
                }
                startActivity(intent8);
                break;
            default:
        }
    }

    /** 备份钱包响应 */
    @Subscribe
    public void onBack(BackupEvent event) {
        Intent intent = new Intent(getActivity(), BackupGuideActivity.class);
        intent.putExtra(CURRENT_SELECTED_WALLET_TYPE, nowType);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnected(BleConnectedEvent event) {
        toNext(currentAction);
        currentAction = 0;
    }

    /** 连接硬件超时响应 */
    @Subscribe
    public void onConnectionTimeout(BleConnectionEx connectionEx) {
        if (connectionEx == BleConnectionEx.BLE_CONNECTION_EX_TIMEOUT) {
            Toast.makeText(getContext(), R.string.ble_connect_timeout, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fixName(FixWalletNameEvent event) {
        mAppWalletViewModel.submit(mAppWalletViewModel::refreshWalletInfo);
    }

    @SingleClick(value = 1000L)
    @OnClick({
        R.id.rel_check_wallet,
        R.id.img_scan,
        R.id.img_Add,
        R.id.rel_create_hd,
        R.id.rel_recovery_hd,
        R.id.rel_pair_hard,
        R.id.rel_wallet_detail,
        R.id.linear_send,
        R.id.linear_receive,
        R.id.linear_sign,
        R.id.rel_now_back_up,
        R.id.img_bottom,
        R.id.img_add_token
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rel_check_wallet:
                Intent intent1 = new Intent(getActivity(), WalletListActivity.class);
                startActivity(intent1);
                break;
            case R.id.img_scan:
                if (mPermissionsDisposable != null && !mPermissionsDisposable.isDisposed()) {
                    mPermissionsDisposable.dispose();
                }
                mPermissionsDisposable =
                        rxPermissions
                                .request(Manifest.permission.CAMERA)
                                .subscribe(
                                        granted -> {
                                            if (granted) {
                                                OnekeyScanQrActivity.start(
                                                        WalletFragment.this,
                                                        mAppWalletViewModel
                                                                .currentWalletAccountInfo
                                                                .getValue()
                                                                .getId(),
                                                        REQUEST_CODE);
                                            } else {
                                                Toast.makeText(
                                                                getActivity(),
                                                                R.string.photopersion,
                                                                Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                break;
            case R.id.rel_create_hd:
                isAddHd = true;
                NavUtils.gotoSoftPassActivity(getActivity(), 0);
                break;
            case R.id.rel_recovery_hd:
                Intent intent = new Intent(getActivity(), RecoverHdWalletActivity.class);
                startActivity(intent);
                break;
            case R.id.rel_pair_hard:
                EventBus.getDefault().removeAllStickyEvents();
                Intent pair = new Intent(getActivity(), SearchDevicesActivity.class);
                startActivity(pair);
                break;
            case R.id.rel_wallet_detail:
                Intent intent4 = new Intent(getActivity(), HdWalletDetailActivity.class);
                intent4.putExtra("hdWalletName", textWalletName.getText().toString());
                intent4.putExtra("isBackup", isBackup);
                startActivity(intent4);
                break;
            case R.id.linear_send:
            case R.id.linear_receive:
            case R.id.linear_sign:
                deal(view.getId());
                break;
            case R.id.rel_now_back_up:
                Intent intent6 = new Intent(getActivity(), BackupGuideActivity.class);
                intent6.putExtra(CURRENT_SELECTED_WALLET_TYPE, nowType);
                startActivity(intent6);
                break;
            case R.id.img_bottom:
                CheckChainDetailWebActivity.start(
                        getActivity(), StringConstant.NEW_GUIDE, StringConstant.NEW_GUIDE_URL);
                break;
            case R.id.img_add_token:
                TokenManagerActivity.start(getActivity());
                break;
            default:
                break;
        }
    }

    private boolean shouldResponsePassEvent() {
        return (linearNoWallet.getVisibility() == View.VISIBLE && isAddHd);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotPass(GotPassEvent event) {
        if (shouldResponsePassEvent()) {
            if (event.fromType == 0) {
                CreateLocalMainWalletActivity.start(requireContext(), event.getPassword());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(RefreshEvent refreshEvent) {
        if (shouldResponsePassEvent()) {
            mAppWalletViewModel.submit(mAppWalletViewModel::refresh);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackupCompleteEvent(BackupCompleteEvent event) {
        whetherBackup();
    }

    /** 注册EventBus */
    @Override
    public boolean needEvents() {
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 11) {
            tetAmount.setTextSize(26);
        } else {
            tetAmount.setTextSize(32);
        }
    }

    @Override
    public void onDestroy() {
        if (mPermissionsDisposable != null && !mPermissionsDisposable.isDisposed()) {
            mPermissionsDisposable.dispose();
        }
        super.onDestroy();
    }
}
