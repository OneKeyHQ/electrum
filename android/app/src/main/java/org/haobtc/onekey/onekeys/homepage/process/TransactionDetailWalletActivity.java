package org.haobtc.onekey.onekeys.homepage.process;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.event.BleConnectedEvent;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.onekeys.homepage.WalletFragment;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;
import org.haobtc.onekey.viewmodel.AppWalletViewModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.internal.schedulers.SingleScheduler;

import static org.haobtc.onekey.constant.Constant.WALLET_BALANCE;

public class TransactionDetailWalletActivity extends BaseActivity implements TransactionListFragment.SchedulerProvide, TransactionListFragment.CoinTypeProvider {
    private static final String EXT_BLE_HW = Constant.BLE_MAC;
    private static final String EXT_WALLET_BALANCE = "walletBalance";
    private static final String EXT_WALLET_DOLLAR = "walletDollar";
    private static final String EXT_HD_WALLET_NAME = "hdWalletName";
    private static final String EXT_WALLET_COIN_TYPE = "walletCoinType";

    public static void start(@NotNull Context context,
                             @NotNull String balance,
                             @NotNull String dollar,
                             @NotNull String walletName,
                             @NotNull String coinType,
                             @Nullable String bleMac) {
        Intent intent = new Intent(context, TransactionDetailWalletActivity.class);
        intent.putExtra(EXT_WALLET_BALANCE, balance);
        intent.putExtra(EXT_WALLET_DOLLAR, dollar);
        intent.putExtra(EXT_HD_WALLET_NAME, walletName);
        intent.putExtra(EXT_WALLET_COIN_TYPE, coinType);
        intent.putExtra(EXT_BLE_HW, bleMac);
        context.startActivity(intent);
    }

    // 防止多线程调用交易记录出现不可预估的问题
    private final Scheduler mTransactionDetailScheduler = new SingleScheduler();

    @BindView(R.id.text_wallet_amount)
    TextView textWalletAmount;
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

    private String hdWalletName;
    private String walletBalance;
    private String bleMac;
    private Vm.CoinType mCoinType;
    private int currentAction;
    private AppWalletViewModel mAppWalletViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_detail_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mAppWalletViewModel = getApplicationViewModel(AppWalletViewModel.class);
        hdWalletName = getIntent().getStringExtra("hdWalletName");
        bleMac = getIntent().getStringExtra(Constant.BLE_MAC);
        mCoinType = Vm.CoinType.convert(getIntent().getStringExtra(EXT_WALLET_COIN_TYPE));
        listenerViewModel();
        switch (mCoinType) {
            default:
            case BTC:
                mTvTokenName.setText(R.string.btc_c);
                mImgTokenLogo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.token_btc, null));
                break;
            case ETH:
                mTvTokenName.setText(R.string.eth);
                mImgTokenLogo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.token_eth, null));
                break;
        }
    }

    private void listenerViewModel() {
        mAppWalletViewModel.currentWalletBalance.observe(this, balance -> {
            walletBalance = balance.getBalance();
            textWalletAmount.setText(String.format("%s%s", balance.getBalance(), balance.getUnit()));
        });
        mAppWalletViewModel.currentWalletFiatBalance.observe(this, balance -> {
            textWalletDollar.setText(String.format("≈ %s %s", balance.getSymbol(), Strings.isNullOrEmpty(balance.getBalance()) ? getString(R.string.zero) : balance.getBalance()));
        });
    }

    @Override
    public void initData() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(TransactionListFragment.getInstance(TransactionListFragment.TransactionListType.ALL));
        fragments.add(TransactionListFragment.getInstance(TransactionListFragment.TransactionListType.RECEIVE));
        fragments.add(TransactionListFragment.getInstance(TransactionListFragment.TransactionListType.SEND));
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
    }

    @SingleClick
    @SuppressLint("UseCompatLoadingForDrawables")
    @OnClick({R.id.img_back, R.id.text_All, R.id.text_into, R.id.text_output, R.id.btn_forward, R.id.btn_collect})
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

    /**
     * 统一处理硬件连接
     */
    private void deal(@IdRes int id) {
        if (!Strings.isNullOrEmpty(bleMac)) {
            currentAction = id;
            if (Strings.isNullOrEmpty(bleMac)) {
                Toast.makeText(this, "未发现设备信息", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent2 = new Intent(this, SearchDevicesActivity.class);
                intent2.putExtra(org.haobtc.onekey.constant.Constant.SEARCH_DEVICE_MODE, org.haobtc.onekey.constant.Constant.SearchDeviceMode.MODE_PREPARE);
                startActivity(intent2);
                BleManager.getInstance(this).connDevByMac(bleMac);
            }
            return;
        }
        toNext(id);

    }

    /**
     * 处理具体业务
     */
    private void toNext(int id) {
        switch (id) {
            case R.id.btn_forward:
                Intent intent2 = new Intent(this, SendHdActivity.class);
                intent2.putExtra(WALLET_BALANCE, walletBalance);
                intent2.putExtra("hdWalletName", hdWalletName);
                startActivity(intent2);
                break;
            case R.id.btn_collect:
                Intent intent3 = new Intent(this, ReceiveHDActivity.class);
                if (!Strings.isNullOrEmpty(bleMac)) {
                    intent3.putExtra(org.haobtc.onekey.constant.Constant.WALLET_TYPE, org.haobtc.onekey.constant.Constant.WALLET_TYPE_HARDWARE_PERSONAL);
                }
                startActivity(intent3);
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
    public Vm.CoinType getCurrentCoinType() {
        return mCoinType;
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
