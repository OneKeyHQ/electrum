package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.BalanceInfo;
import org.haobtc.onekey.bean.FindOnceWalletEvent;
import org.haobtc.onekey.event.SelectedEvent;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.adapter.OnceWalletAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/27/20
 */

public class RecoveryWalletFromHdFragment extends BaseFragment {
    @BindView(R.id.loaded_wallet)
    RelativeLayout loadedWallet;
    @BindView(R.id.wallet_rec)
    RecyclerView walletRec;
    @BindView(R.id.wallet_card)
    CardView walletCard;
    @BindView(R.id.recovery)
    Button recovery;
    private OnceWalletAdapter adapter;
    private List<String> nameList;

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {
        nameList = new ArrayList<>();
    }
    @Subscribe
    public void onFind(FindOnceWalletEvent<BalanceInfo> event) {
        adapter = new OnceWalletAdapter(getContext(), event.getWallets());
        walletRec.setAdapter(adapter);
        walletRec.setLayoutManager(new LinearLayoutManager(getContext()));
        loadedWallet.setVisibility(View.GONE);
        walletCard.setVisibility(View.VISIBLE);
        recovery.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }
    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.looking_for_once_wallet;
    }

    @OnClick(R.id.recovery)
    public void onViewClicked() {
        adapter.getSelectMap().entrySet().forEach((entry) -> {
            if (entry.getValue().isChecked()) {
                nameList.add(entry.getKey());
            }
        });
        if (nameList.isEmpty()) {
            showToast("请至少选择一个你要恢复的钱包");
        } else {
            EventBus.getDefault().post(new SelectedEvent(nameList));
        }
    }

    @Override
    public boolean needEvents() {
        return true;
    }
}
