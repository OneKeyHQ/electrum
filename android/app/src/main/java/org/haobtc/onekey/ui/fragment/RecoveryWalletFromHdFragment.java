package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.BalanceInfo;
import org.haobtc.onekey.bean.FindOnceWalletEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.SelectedEvent;
import org.haobtc.onekey.ui.adapter.OnceWalletAdapter;
import org.haobtc.onekey.ui.base.BaseFragment;

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
    @BindView(R.id.promote)
    TextView promote;
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

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onFind(FindOnceWalletEvent<BalanceInfo> event) {
        // avoid eventBus' needless deliver
        if (loadedWallet == null) {
            return;
        }
        loadedWallet.setVisibility(View.GONE);
        recovery.setVisibility(View.VISIBLE);
        if (event.getWallets().isEmpty()) {
            promote.setText(R.string.no_use_wallet);
            recovery.setText(R.string.back);
        } else {
            walletCard.setVisibility(View.VISIBLE);
            adapter = new OnceWalletAdapter(getContext(), event.getWallets());
            walletRec.setAdapter(adapter);
            walletRec.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter.notifyDataSetChanged();
        }
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
    public void onViewClicked(View view) {
        if (recovery.getText().equals(getString(R.string.back))) {
            EventBus.getDefault().post(new ExitEvent());
        } else {
            adapter.getSelectMap().entrySet().forEach((entry) -> {
                if (entry.getValue().isChecked()) {
                    nameList.add(entry.getKey());
                }
            });
            if (nameList.isEmpty()) {
                showToast(R.string.recovery_wallet_select_promote);
            } else {
                EventBus.getDefault().post(new SelectedEvent(nameList));
            }
        }
    }

    @Override
    public boolean needEvents() {
        return true;
    }
}
