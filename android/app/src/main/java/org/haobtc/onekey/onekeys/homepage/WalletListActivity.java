package org.haobtc.onekey.onekeys.homepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.WalletListTypeAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.WalletInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.event.RefreshEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.dialog.RecoverHdWalletActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.HDWalletActivity;
import org.haobtc.onekey.onekeys.homepage.process.CreateWalletChooseTypeActivity;
import org.haobtc.onekey.ui.activity.SearchDevicesActivity;
import org.haobtc.onekey.ui.dialog.CreateWalletWaySelectorDialog;
import org.haobtc.onekey.ui.dialog.HdWalletIntroductionDialog;
import org.haobtc.onekey.utils.NavUtils;
import org.haobtc.onekey.viewmodel.AppWalletViewModel;
import org.haobtc.onekey.viewmodel.WalletListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author jinxiaomin
 */
public class WalletListActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.recl_wallet_detail)
    LinearLayout reclWalletDetail;
    @BindView(R.id.recl_wallet_list)
    RecyclerView reclWalletList;
    @BindView(R.id.text_wallet_num)
    TextView textWalletNum;
    @BindView(R.id.view_all)
    ImageView viewAll;
    @BindView(R.id.view_btc)
    ImageView viewBtc;
    @BindView(R.id.view_eth)
    ImageView viewEth;
    @BindView(R.id.tet_None)
    TextView tetNone;
    @BindView(R.id.img_add)
    ImageView imgAdd;
    @BindView(R.id.text_wallet_type)
    TextView textWalletType;
    @BindView(R.id.img_w)
    ImageView imgW;
    private boolean isAddHd;
    private AppWalletViewModel mAppWalletViewModel;
    private WalletListTypeAdapter mAdapter;
    private WalletListViewModel mWalletModel;
    // 所有钱包的集合
    private List<WalletInfo> mAllList;
    // 显示钱包
    private int mAllNum;
    private ArrayList<WalletInfo> btcList;
    private ArrayList<WalletInfo> ethList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_list;
    }

    @Override
    public void initData() {
        mWalletModel = new ViewModelProvider(this).get(WalletListViewModel.class);
        reclWalletList.setNestedScrollingEnabled(false);
        //btc wallet list
        btcList = new ArrayList<>();
        //btc wallet list
        ethList = new ArrayList<>();
        mAllList = new ArrayList<>();
        mAdapter = new WalletListTypeAdapter(mAllList);
        reclWalletList.setAdapter(mAdapter);
        mWalletModel.mAllWallets.observe(this, mAllData -> {
            mAllNum = mAllData.validShowNum;
            textWalletNum.setText(String.valueOf(mAllData.validShowNum));
            mAllList.addAll(mAllData.wallets);
            mAdapter.setNewData(mAllData.wallets);
        });
        mWalletModel.mBtcWallets.observe(this, mBtcList -> btcList.addAll(mBtcList));
        mWalletModel.mEthWallets.observe(this, mEthList -> ethList.addAll(mEthList));
        mAdapter.setOnItemChildClickListener(this::onItemChildClick);
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mAppWalletViewModel = getApplicationViewModel(AppWalletViewModel.class);
    }

    @SingleClick
    @SuppressLint("UseCompatLoadingForDrawables")
    @OnClick({R.id.img_close, R.id.recl_wallet_detail, R.id.lin_pair_wallet, R.id.lin_add_wallet, R.id.view_all, R.id.view_btc, R.id.view_eth, R.id.img_add, R.id.img_w})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.recl_wallet_detail:
                Intent intent4 = new Intent(WalletListActivity.this, HDWalletActivity.class);
                startActivity(intent4);
                finish();
                break;
            case R.id.lin_pair_wallet:
                Intent intent = new Intent(this, SearchDevicesActivity.class);
                intent.putExtra(Constant.SEARCH_DEVICE_MODE, Constant.SearchDeviceMode.MODE_PAIR_WALLET_TO_COLD);
                startActivity(intent);
                break;
            case R.id.lin_add_wallet:
                new CreateWalletWaySelectorDialog().show(getSupportFragmentManager(), "");
                break;
            case R.id.view_all:
                textWalletType.setText(getString(R.string.hd_wallet));
                viewAll.setImageDrawable(getDrawable(R.drawable.hd_wallet_1));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_trans_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.eth_icon_gray));
                textWalletNum.setText(String.valueOf(mAllNum));
                reclWalletDetail.setVisibility(View.VISIBLE);
                imgAdd.setVisibility(View.GONE);
                imgW.setVisibility(View.VISIBLE);
                mAdapter.setNewData(mAllList);
                break;
            case R.id.view_btc:
                textWalletType.setText(getString(R.string.btc_wallet));
                viewAll.setImageDrawable(getDrawable(R.drawable.id_wallet_icon));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.eth_icon_gray));
                textWalletNum.setText(String.valueOf(btcList.size()));
                reclWalletDetail.setVisibility(View.GONE);
                imgW.setVisibility(View.GONE);
                mAdapter.setNewData(btcList);
                break;
            case R.id.view_eth:
                textWalletType.setText(getString(R.string.eth_wallet));
                viewAll.setImageDrawable(getDrawable(R.drawable.id_wallet_icon));
                viewBtc.setImageDrawable(getDrawable(R.drawable.token_trans_btc));
                viewEth.setImageDrawable(getDrawable(R.drawable.token_eth));
                textWalletNum.setText(String.valueOf(ethList.size()));
                reclWalletDetail.setVisibility(View.GONE);
                imgAdd.setVisibility(View.VISIBLE);
                imgW.setVisibility(View.GONE);
                mAdapter.setNewData(ethList);
                break;
            case R.id.img_add:
                Intent intent00 = new Intent(this, CreateWalletChooseTypeActivity.class);
                intent00.putExtra("ifHaveHd", !mAllList.isEmpty());
                startActivity(intent00);
                break;
            case R.id.img_w:
                new HdWalletIntroductionDialog().show(getSupportFragmentManager(), "hd_introduction");
                break;
        }
    }

    private boolean shouldResponsePassEvent() {
        return (mAllNum == 0 && isAddHd);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotPass(GotPassEvent event) {
        if (shouldResponsePassEvent()) {
            if (event.fromType == 1) {
                PyEnv.createLocalHd(event.getPassword(), null);
            }
        }
    }

    @Subscribe
    public void onRefresh(RefreshEvent event) {
        if (shouldResponsePassEvent()) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        if (id == R.id.recl_add_hd_wallet) {
            isAddHd = true;
            NavUtils.gotoSoftPassActivity(mContext, 1);
        } else if (id == R.id.recl_recovery_wallet) {
            isAddHd = false;
            Intent intent2 = new Intent(mContext, RecoverHdWalletActivity.class);
            startActivity(intent2);
        } else if (id == R.id.recl_add_wallet) {
            NavUtils.gotoCreateDeriveChooseTypeActivity(mContext, false);
        } else if (id == R.id.rel_background) {
            WalletInfo data = (WalletInfo) adapter.getItem(position);
            String name = data.name;
            mAppWalletViewModel.changeCurrentWallet(name);
            mIntent(HomeOneKeyActivity.class);
        }
    }

}
