package org.haobtc.onekey.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.haobtc.onekey.R;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.mvp.base.BaseMvpActivity;
import org.haobtc.onekey.mvp.presenter.SearchDevicesPresenter;
import org.haobtc.onekey.mvp.view.ISearchDevicesView;
import org.haobtc.onekey.passageway.BlePassageway;
import org.haobtc.onekey.passageway.HandleCommands;
import org.haobtc.onekey.passageway.NfcPassageway;
import org.haobtc.onekey.ui.adapter.BleDeviceAdapter;
import org.haobtc.onekey.utils.NfcUtils;
import org.haobtc.onekey.utils.ValueAnimatorUtil;

import java.util.Objects;

import butterknife.BindView;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

import static cn.com.heaton.blelibrary.ble.Ble.REQUEST_ENABLE_BT;

public class SearchDevicesActivity extends BaseMvpActivity<SearchDevicesPresenter> implements ISearchDevicesView
        , BleDeviceAdapter.OnItemBleDeviceClick, BlePassageway.BleConnectCallBack, View.OnClickListener {


    @BindView(R.id.device_list)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.smart_RefreshLayout)
    protected SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.device_list_layout)
    protected LinearLayout mDeviceListLayout;
    @BindView(R.id.container)
    protected LinearLayout mContainer;
    private LinearLayout mLayoutView;
    private BleDeviceAdapter mBleAdapter;
    @BindView(R.id.load_device)
    protected RelativeLayout mLoadingLayout;
    @BindView(R.id.title)
    protected TextView mTitle;
    @BindView(R.id.open_wallet_hide)
    protected TextView mOpenWalletHide;

    private int mSearchMode;

    @Override
    protected SearchDevicesPresenter initPresenter() {
        return new SearchDevicesPresenter(this);
    }

    @Override
    public void init() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.relode).setOnClickListener(this);
        mSearchMode = getIntent().getIntExtra(Constant.SEARCH_DEVICE_MODE, Constant.SearchDeviceMode.MODE_PAIR_WALLET_TO_COLD);
        mPresenter.init();

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_search_devices;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            showToast("失败");
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            if (mPresenter != null) {
                mPresenter.refreshBleDeviceList();
            }
        }
    }

    @Override
    public void onBleScanDevice(BleDevice device) {
        if (mBleAdapter != null) {
            mBleAdapter.add(device);
        }
    }

    @Override
    public void onBleScanStop() {
        if (mLoadingLayout.getVisibility() != View.GONE) {
            mLoadingLayout.setVisibility(View.GONE);
        }
        if (mDeviceListLayout.getVisibility() != View.VISIBLE) {
            mDeviceListLayout.setVisibility(View.VISIBLE);
            mDeviceListLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout
                            .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    lp.setMargins(20, 20, 20, 20);
                    mDeviceListLayout.setLayoutParams(lp);
                }
            }, 505);
        }
        if (mOpenWalletHide.getVisibility() != View.VISIBLE) {
            mOpenWalletHide.postDelayed(() -> mOpenWalletHide.setVisibility(View.VISIBLE), 400);

        }
        mBleAdapter.notifyDataSetChanged();
        if (mLayoutView.getHeight() > 0) {
            mLayoutView.post(() -> ValueAnimatorUtil
                    .animatorHeightLayout(mLayoutView, mLayoutView.getHeight(), 0));
        }
    }

    @Override
    public void addBleView() {
        mLayoutView = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.layout_search_device_ble, null);

        TextView next = mLayoutView.findViewById(R.id.next_to_do);
        TextView hide = mLayoutView.findViewById(R.id.hind);
        TextView action = mLayoutView.findViewById(R.id.action);

        switch (mSearchMode) {
            case Constant.SearchDeviceMode.MODE_RECOVERY_WALLET_BY_COLD:
                mTitle.setText(R.string.recovery_hd_wallet);
                next.setText(R.string.ready_wallet);
                hide.setText(R.string.support_device);
                action.setText(R.string.open_cold_wallet);
                if (action.getVisibility() == View.GONE) {
                    action.setVisibility(View.VISIBLE);
                }
                break;
            case Constant.SearchDeviceMode.MODE_BACKUP_WALLET_TO_COLD:
                mTitle.setText(R.string.backup_wallet);
                next.setText(R.string.ready_wallet);
                hide.setText(R.string.support_device);
                action.setText(R.string.open_cold_wallet);
                if (action.getVisibility() == View.GONE) {
                    action.setVisibility(View.VISIBLE);
                }
                break;
            case Constant.SearchDeviceMode.MODE_CLONE_TO_OTHER_COLD:
                mTitle.setText(R.string.ready_unactive_wallet);
                next.setText(R.string.ready_wallet);
                hide.setText(R.string.support_device);
                action.setText(R.string.open_cold_wallet);
                if (action.getVisibility() == View.GONE) {
                    action.setVisibility(View.VISIBLE);
                }
                break;
            case Constant.SearchDeviceMode.MODE_BIND_ADMIN_PERSON:
                mTitle.setText(R.string.bind_admin_person);
                next.setText(R.string.ready_wallet);
                hide.setText(R.string.support_device);
                action.setText(R.string.open_cold_wallet);
                if (action.getVisibility() == View.GONE) {
                    action.setVisibility(View.VISIBLE);
                }
                break;
            default:
                mTitle.setText(R.string.pair);
                next.setText(R.string.open_cold_wallet);
                hide.setText(R.string.support_device);
                if (action.getVisibility() == View.VISIBLE) {
                    action.setVisibility(View.GONE);
                }
                break;
        }
        mContainer.addView(mLayoutView, 1);
        mBleAdapter = new BleDeviceAdapter(this, this);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            if (mPresenter != null) {
                mPresenter.refreshBleDeviceList();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mBleAdapter);
    }

    @Override
    public void addNfcView() {
        NfcUtils.nfc(getActivity(), true);
    }

    @Override
    public void addUsbView() {

    }

    @Override
    public void connectBle(BleDevice device) {
        BlePassageway.getInstance().connDev(device, this);
    }

    @Override
    public void connectSucceeded() {
        toNextActivity();
    }

    private void toNextActivity() {

        switch (mSearchMode) {
            case Constant.SearchDeviceMode.MODE_RECOVERY_WALLET_BY_COLD:

                break;
            case Constant.SearchDeviceMode.MODE_BACKUP_WALLET_TO_COLD:

                break;
            case Constant.SearchDeviceMode.MODE_CLONE_TO_OTHER_COLD:

                break;
            case Constant.SearchDeviceMode.MODE_BIND_ADMIN_PERSON:

                break;
            default:
                HandleCommands.getFeature(result -> {
                    if (!result.isInitialized()) {
                        toActivity(FindNewDeviceActivity.class);
                    } else {

                    }
                });

                break;
        }
    }

    @Override
    public void connectFailed() {
        //todo connect failed

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.relode:
                if (mPresenter == null) {
                    return;
                }
                if (mLoadingLayout.getVisibility() != View.VISIBLE) {
                    mLoadingLayout.setVisibility(View.VISIBLE);
                }
                if (mDeviceListLayout.getVisibility() != View.GONE) {
                    mDeviceListLayout.setVisibility(View.GONE);
                }
                mPresenter.refreshBleDeviceList();
                break;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction(); // get the action of the coming intent
        if (Objects.equals(action, NfcAdapter.ACTION_NDEF_DISCOVERED) // NDEF type
                || Objects.equals(action, NfcAdapter.ACTION_TECH_DISCOVERED)
                || Objects.requireNonNull(action).equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tags = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NfcPassageway.getInstance().initNfc(tags);
        }
    }
}
