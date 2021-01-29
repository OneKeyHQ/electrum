package org.haobtc.onekey.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.BalanceInfoDTO;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.SelectedEvent;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.walletprocess.OnWalletCheckListener;
import org.haobtc.onekey.ui.adapter.OnceWalletAdapter;
import org.haobtc.onekey.ui.adapter.RecoveryWalletDerivedInfoAdapter;
import org.haobtc.onekey.ui.adapter.RecoveryWalletInfoAdapter;
import org.haobtc.onekey.ui.base.BaseFragment;

/**
 * @author liyan
 * @date 11/27/20
 */
public class RecoveryWalletFromHdFragment extends BaseFragment implements OnWalletCheckListener {

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

    @BindView(R.id.find_result_promote)
    TextView findResultPromote;

    @BindView(R.id.wallet_group)
    Group walletGroup;

    private OnceWalletAdapter adapter;
    private List<String> nameList;
    private OnFindWalletInfoProvider mProvider;
    private RecoveryWalletInfoAdapter mRecoveryWalletInfoAdapter;
    private RecoveryWalletDerivedInfoAdapter mRecoveryWalletDerivedInfoAdapter;
    private boolean mHasExist;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFindWalletInfoProvider) {
            mProvider = (OnFindWalletInfoProvider) context;
        }
    }

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {
        nameList = new ArrayList<>();
    }

    public void setDate(CreateWalletBean createWalletBean) {
        loadedWallet.setVisibility(View.GONE);
        walletGroup.setVisibility(View.VISIBLE);
        if (createWalletBean == null) {
            recovery.setText(R.string.back);
            findResultPromote.setText(R.string.not_found_once_wallet);
        } else {
            if (createWalletBean.getDerivedInfo() == null
                    || createWalletBean.getDerivedInfo().size() == 0) {
                findResultPromote.setText(R.string.not_find_wallet);
                recovery.setText(R.string.create_);
                mRecoveryWalletInfoAdapter =
                        new RecoveryWalletInfoAdapter(
                                R.layout.choose_hd_wallet_item,
                                createWalletBean.getWalletInfo(),
                                this::onCheck);
                walletRec.setAdapter(mRecoveryWalletInfoAdapter);
                walletRec.setLayoutManager(new LinearLayoutManager(getContext()));
                packageWalletInfoNameList(createWalletBean.getWalletInfo());
            } else {
                findResultPromote.setText(R.string.find_wallet_account);
                recovery.setText(R.string.recovery);
                mRecoveryWalletDerivedInfoAdapter =
                        new RecoveryWalletDerivedInfoAdapter(
                                R.layout.choose_hd_wallet_item,
                                createWalletBean.getDerivedInfo(),
                                this::onCheck);
                packageDerivedNameList(createWalletBean.getDerivedInfo());
                walletRec.setAdapter(mRecoveryWalletDerivedInfoAdapter);
                walletRec.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }
    }

    private void packageWalletInfoNameList(List<CreateWalletBean.WalletInfoBean> walletInfo) {
        nameList.clear();
        if (walletInfo != null && walletInfo.size() > 0) {
            for (CreateWalletBean.WalletInfoBean walletInfoBean : walletInfo) {
                if (!Strings.isNullOrEmpty(walletInfoBean.getName())) {
                    nameList.add(walletInfoBean.getName());
                }
            }
        }
        if (nameList.size() == 0) {
            recovery.setEnabled(false);
        }
    }

    private void packageDerivedNameList(List<CreateWalletBean.DerivedInfoBean> derivedInfo) {
        nameList.clear();
        if (derivedInfo != null && derivedInfo.size() > 0) {
            for (CreateWalletBean.DerivedInfoBean derivedInfoBean : derivedInfo) {
                if (!Strings.isNullOrEmpty(derivedInfoBean.getName())) {
                    if (derivedInfoBean.getExist().equals("0")) {
                        nameList.add(derivedInfoBean.getName());
                    } else {
                        mHasExist = true;
                    }
                }
            }
        }
    }

    /**
     * * init layout
     *
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.looking_for_once_wallet;
    }

    private void refreshButton() {
        AtomicReference<Boolean> isChecked = new AtomicReference<>(false);
        adapter.getSelectMap()
                .entrySet()
                .forEach(
                        (entry) -> {
                            if (entry.getValue().isChecked()) {
                                isChecked.set(true);
                            }
                        });
        recovery.setEnabled(isChecked.get());
    }

    @OnClick(R.id.recovery)
    public void onViewClicked(View view) {
        if (recovery.getText().equals(getString(R.string.back))) {
            EventBus.getDefault().post(new ExitEvent());
        } else {
            if (nameList.isEmpty()) {
                if (mHasExist) {
                    startActivity(new Intent(getActivity(), HomeOneKeyActivity.class));
                } else {
                    showToast(R.string.recovery_wallet_select_promote);
                }
            } else {
                EventBus.getDefault().post(new SelectedEvent(nameList));
            }
        }
    }

    @Override
    public boolean needEvents() {
        return false;
    }

    @Override
    public void onCheck(String name, boolean isChecked) {
        if (isChecked) {
            if (!nameList.contains(name)) {
                nameList.add(name);
            }
        } else {
            if (nameList.contains(name)) {
                nameList.remove(name);
            }
        }
        if (nameList.size() == 0) {
            recovery.setEnabled(mHasExist);
        } else {
            recovery.setEnabled(true);
        }
    }

    public interface OnFindWalletInfoProvider {
        void setOnFindWalletCallback(OnFindWalletInfoCallback callback);
    }

    public interface OnFindWalletInfoCallback {
        void onFindWallet(List<BalanceInfoDTO> balanceInfos);
    }
}
