package org.haobtc.onekey.onekeys.homepage.process;

import static org.haobtc.onekey.constant.Constant.WALLET_BALANCE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.common.base.Strings;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.internal.schedulers.SingleScheduler;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.Assets;
import org.haobtc.onekey.bean.HardwareFeatures;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.bean.WalletAccountInfo;
import org.haobtc.onekey.business.wallet.AccountManager;
import org.haobtc.onekey.business.wallet.DeviceManager;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.BleConnectedEvent;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;
import org.haobtc.onekey.viewmodel.AppWalletViewModel;
import org.jetbrains.annotations.NotNull;

public class TransactionDetailWalletActivity extends BaseActivity
        implements TransactionListFragment.SchedulerProvide,
                TransactionListFragment.AssetsProvider {

    private static final String EXT_ASSETS_ID = "assets_id";
    private static final String EXT_WALLET_ID = "wallet_id";

    public static void start(@NotNull Context context, @NotNull String walletId) {
        start(context, walletId, -1);
    }

    public static void start(@NotNull Context context, @NotNull String walletId, int assetsId) {
        Intent intent = new Intent(context, TransactionDetailWalletActivity.class);
        intent.putExtra(EXT_WALLET_ID, walletId);
        intent.putExtra(EXT_ASSETS_ID, assetsId);
        context.startActivity(intent);
    }

    // 防止多线程调用交易记录出现不可预估的问题
    private final Scheduler mTransactionDetailScheduler = new SingleScheduler();

    @BindView(R.id.text_wallet_amount)
    TextView textWalletAmount;

    @BindView(R.id.text_wallet_amount_unit)
    TextView textWalletAmountUnit;

    @BindView(R.id.text_wallet_dollar)
    TextView textWalletDollar;

    @BindView(R.id.text_All)
    TextView textAll;

    @BindView(R.id.text_into)
    TextView textInto;

    @BindView(R.id.text_output)
    TextView textOutput;

    @BindView(R.id.viewpager_transaction)
    ViewPager mViewPager;

    @BindView(R.id.img_token_logo)
    ImageView mImgTokenLogo;

    @BindView(R.id.tv_token_name)
    TextView mTvTokenName;

    private String walletBalance;
    private int currentAction;
    private AppWalletViewModel mAppWalletViewModel;
    private AccountManager mAccountManager;
    private DeviceManager mDeviceManager;
    private String mWalletId;
    private int mAssetsId;
    private WalletAccountInfo mWalletAccountInfo;
    private Assets mCurrentAssets;

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_detail_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        mWalletId = getIntent().getStringExtra(EXT_WALLET_ID);
        if (mWalletId == null) {
            finish();
            return;
        }

        mAssetsId = getIntent().getIntExtra(EXT_ASSETS_ID, -1);
        mAccountManager = new AccountManager(this);
        mDeviceManager = new DeviceManager(this);

        mAppWalletViewModel =
                new ViewModelProvider(MyApplication.getInstance()).get(AppWalletViewModel.class);

        if (mAppWalletViewModel.currentWalletAssetsList.getValue() == null
                || mAppWalletViewModel.currentWalletAssetsList.getValue().size() == 0) {
            finish();
        }

        LocalWalletInfo localWalletByName = mAccountManager.getLocalWalletByName(mWalletId);
        mWalletAccountInfo = AppWalletViewModel.Companion.convert(localWalletByName);

        listenerViewModel();
    }

    private void listenerViewModel() {
        mAppWalletViewModel.currentWalletAccountInfo.observe(
                this,
                walletAccountInfo -> {
                    initTransactionList();
                });
        mAppWalletViewModel.currentWalletAssetsList.observe(
                this,
                assetsList -> {
                    mCurrentAssets = assetsList.getByUniqueIdOrZero(mAssetsId);
                    if (mCurrentAssets != null) {
                        walletBalance =
                                mCurrentAssets
                                        .getBalance()
                                        .getBalance()
                                        .stripTrailingZeros()
                                        .toPlainString();
                        String amount =
                                mCurrentAssets
                                        .getBalance()
                                        .getBalance()
                                        .stripTrailingZeros()
                                        .toPlainString();
                        textWalletAmount.setText(amount);
                        textWalletAmountUnit.setText(mCurrentAssets.getBalance().getUnit());

                        textWalletDollar.setText(
                                String.format(
                                        "≈ %s %s",
                                        mCurrentAssets.getBalanceFiat().getSymbol(),
                                        mCurrentAssets.getBalanceFiat().getBalanceFormat()));

                        mTvTokenName.setText(mCurrentAssets.getName());
                        mCurrentAssets.getLogo().intoTarget(mImgTokenLogo);
                    }
                });
    }

    @Override
    public void initData() {}

    private void initTransactionList() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(
                TransactionListFragment.getInstance(
                        TransactionListFragment.TransactionListType.ALL));
        fragments.add(
                TransactionListFragment.getInstance(
                        TransactionListFragment.TransactionListType.RECEIVE));
        fragments.add(
                TransactionListFragment.getInstance(
                        TransactionListFragment.TransactionListType.SEND));
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
    }

    @SingleClick
    @SuppressLint("UseCompatLoadingForDrawables")
    @OnClick({
        R.id.img_back,
        R.id.text_All,
        R.id.text_into,
        R.id.text_output,
        R.id.btn_forward,
        R.id.btn_collect
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.text_All:
                textAll.setBackground(getDrawable(R.drawable.back_white_6));
                textInto.setBackgroundColor(getColor(R.color.t_white));
                textOutput.setBackgroundColor(getColor(R.color.t_white));
                mViewPager.setCurrentItem(0);
                break;
            case R.id.text_into:
                textAll.setBackgroundColor(getColor(R.color.t_white));
                textInto.setBackground(getDrawable(R.drawable.back_white_6));
                textOutput.setBackgroundColor(getColor(R.color.t_white));
                mViewPager.setCurrentItem(1);
                break;
            case R.id.text_output:
                textAll.setBackgroundColor(getColor(R.color.t_white));
                textInto.setBackgroundColor(getColor(R.color.t_white));
                textOutput.setBackground(getDrawable(R.drawable.back_white_6));
                mViewPager.setCurrentItem(2);
                break;
            case R.id.btn_forward:
            case R.id.btn_collect:
                deal(view.getId());
                break;
        }
    }

    /** 统一处理硬件连接 */
    private void deal(@IdRes int id) {
        HardwareFeatures deviceInfo =
                mDeviceManager.getDeviceInfo(mWalletAccountInfo.getDeviceId());
        if (deviceInfo != null) {
            String deviceBleMacAddress =
                    mDeviceManager.getDeviceBleMacAddress(deviceInfo.getBleName());
            if (!Strings.isNullOrEmpty(deviceBleMacAddress)) {
                currentAction = id;
                if (Strings.isNullOrEmpty(deviceBleMacAddress)) {
                    Toast.makeText(this, "未发现设备信息", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent2 = new Intent(this, SearchDevicesActivity.class);
                    intent2.putExtra(
                            org.haobtc.onekey.constant.Constant.SEARCH_DEVICE_MODE,
                            org.haobtc.onekey.constant.Constant.SearchDeviceMode.MODE_PREPARE);
                    intent2.putExtra(Constant.DEVICE_ID, deviceInfo.getDeviceId());
                    startActivity(intent2);
                    BleManager.getInstance(this).connDevByMac(deviceBleMacAddress);
                }
                return;
            }
        }

        toNext(id);
    }

    /** 处理具体业务 */
    private void toNext(int id) {
        WalletAccountInfo value = mAppWalletViewModel.currentWalletAccountInfo.getValue();
        switch (id) {
            case R.id.btn_forward:
                switch (mCurrentAssets.getCoinType()) {
                    case BTC:
                        Intent intent2 = new Intent(this, SendHdActivity.class);
                        intent2.putExtra(WALLET_BALANCE, walletBalance);
                        intent2.putExtra("hdWalletName", mWalletAccountInfo.getName());
                        startActivity(intent2);
                        break;
                    case ETH:
                        if (value != null) {
                            SendEthActivity.start(
                                    mContext, value.getId(), mCurrentAssets.uniqueId());
                        }
                        break;
                }

                break;
            case R.id.btn_collect:
                if (value != null) {
                    ReceiveHDActivity.start(this, value.getId(), mCurrentAssets.uniqueId());
                }
                break;
            default:
        }
    }

    @NonNull
    @Override
    public Scheduler getScheduler() {
        return mTransactionDetailScheduler;
    }

    @Override
    public Assets getCurrentAssets() {
        return mCurrentAssets;
    }

    static class ViewPageAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public ViewPageAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnected(BleConnectedEvent event) {
        toNext(currentAction);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
