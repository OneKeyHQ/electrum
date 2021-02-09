package org.haobtc.onekey.onekeys.dialog.recovery;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.common.base.Strings;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.RecoveryWalletAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.BalanceInfoDTO;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.onekeys.walletprocess.OnWalletCheckListener;
import org.haobtc.onekey.ui.adapter.RecoveryWalletDerivedInfoAdapter;
import org.haobtc.onekey.ui.adapter.RecoveryWalletInfoAdapter;

public class RecoveryChooseWalletActivity extends BaseActivity implements OnWalletCheckListener {

    @BindView(R.id.recl_wallet_list)
    RecyclerView reclWalletList;

    @BindView(R.id.btn_recovery)
    Button btnRecovery;

    @BindView(R.id.loaded_wallet)
    RelativeLayout loadedWallet;

    @BindView(R.id.scroll_wallet)
    NestedScrollView scrollWallet;

    @BindView(R.id.text_show)
    TextView title;

    private RecoveryWalletAdapter recoveryWalletAdapter;
    private ArrayList<String> listDates;
    private String name;
    private List<BalanceInfoDTO> walletList;
    private Disposable mDisposable;
    private RecoveryWalletInfoAdapter mRecoveryWalletInfoAdapter;
    private RecoveryWalletDerivedInfoAdapter mRecoveryWalletDerivedInfoAdapter;
    private boolean mHasExist;
    private boolean mResponse = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recovery_choose_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        // choose wallet data list
        listDates = new ArrayList<>();
        reclWalletList.setNestedScrollingEnabled(false);
        mDisposable =
                Single.create(
                                (SingleOnSubscribe<PyResponse<CreateWalletBean>>)
                                        emitter -> {
                                            String password =
                                                    getIntent().getStringExtra("password");
                                            String recoverySeed =
                                                    getIntent().getStringExtra("recoverySeed");
                                            if (password == null || recoverySeed == null) {
                                                Log.e(
                                                        "RecoveryChooseWalletActivity",
                                                        "启动 RecoveryChooseWalletActivity 需要 password recoverySeed 参数.");
                                                throw new RuntimeException("");
                                            }
                                            PyResponse<CreateWalletBean> balanceInfoDTOS =
                                                    PyEnv.restoreLocalWallet(
                                                            password, recoverySeed);
                                            emitter.onSuccess(balanceInfoDTOS);
                                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                walletBalances -> {
                                    mResponse = true;
                                    if (loadedWallet == null) {
                                        return;
                                    }
                                    loadedWallet.setVisibility(View.GONE);
                                    btnRecovery.setVisibility(View.VISIBLE);
                                    scrollWallet.setVisibility(View.VISIBLE);
                                    if (Strings.isNullOrEmpty(walletBalances.getErrors())) {
                                        CreateWalletBean createWalletBean =
                                                walletBalances.getResult();
                                        if (createWalletBean.getDerivedInfo() == null
                                                || createWalletBean.getDerivedInfo().size() == 0) {
                                            title.setText(R.string.not_find_wallet);
                                            mRecoveryWalletInfoAdapter =
                                                    new RecoveryWalletInfoAdapter(
                                                            R.layout.choose_hd_wallet_item,
                                                            createWalletBean.getWalletInfo(),
                                                            this::onCheck);
                                            reclWalletList.setAdapter(mRecoveryWalletInfoAdapter);
                                            reclWalletList.setLayoutManager(
                                                    new LinearLayoutManager(mContext));
                                            packageWalletInfoNameList(
                                                    createWalletBean.getWalletInfo());
                                        } else {
                                            title.setText(R.string.find_wallet_account);
                                            mRecoveryWalletDerivedInfoAdapter =
                                                    new RecoveryWalletDerivedInfoAdapter(
                                                            R.layout.choose_hd_wallet_item,
                                                            createWalletBean.getDerivedInfo(),
                                                            this::onCheck);
                                            packageDerivedNameList(
                                                    createWalletBean.getDerivedInfo());
                                            reclWalletList.setAdapter(
                                                    mRecoveryWalletDerivedInfoAdapter);
                                            reclWalletList.setLayoutManager(
                                                    new LinearLayoutManager(mContext));
                                        }
                                    } else {
                                        mToast(walletBalances.getErrors());
                                    }
                                },
                                throwable -> {
                                    if (!TextUtils.isEmpty(throwable.getMessage())) {
                                        mToast(throwable.getMessage());
                                    }
                                    finish();
                                });
    }

    /**
     * 如果有存在的，可以不选择，返回主页
     *
     * @param derivedInfo
     */
    private void packageDerivedNameList(List<CreateWalletBean.DerivedInfoBean> derivedInfo) {
        listDates.clear();
        if (derivedInfo != null && derivedInfo.size() > 0) {
            for (CreateWalletBean.DerivedInfoBean derivedInfoBean : derivedInfo) {
                if (!Strings.isNullOrEmpty(derivedInfoBean.getName())) {
                    if (derivedInfoBean.getExist().equals("0")) {
                        listDates.add(derivedInfoBean.getName());
                    } else {
                        mHasExist = true;
                    }
                }
            }
        }
    }

    /**
     * 创建账户，必须选择一个
     *
     * @param walletInfo
     */
    private void packageWalletInfoNameList(List<CreateWalletBean.WalletInfoBean> walletInfo) {
        listDates.clear();
        if (walletInfo != null && walletInfo.size() > 0) {
            for (CreateWalletBean.WalletInfoBean walletInfoBean : walletInfo) {
                if (!Strings.isNullOrEmpty(walletInfoBean.getName())) {
                    listDates.add(walletInfoBean.getName());
                }
            }
        }
        if (listDates.size() == 0) {
            btnRecovery.setEnabled(false);
        }
    }

    @SingleClick
    @OnClick({R.id.btn_recovery, R.id.img_back})
    public void onViewClicked(View view) {

        if (view.getId() == R.id.btn_recovery) {
            if (btnRecovery.getText().equals(getString(R.string.no_use_wallet))) {
                finish();
            } else {
                if (listDates.size() > 0) {
                    try {
                        boolean success = PyEnv.recoveryConfirm(listDates, false);
                        if (success) {
                            EventBus.getDefault().post(new CreateSuccessEvent(listDates.get(0)));
                            mIntent(HomeOneKeyActivity.class);
                        }
                    } catch (Exception e) {
                        mToast(e.getMessage().replace("BaseException:", ""));
                    }
                } else {
                    if (mHasExist) {
                        mIntent(HomeOneKeyActivity.class);
                    } else {
                        mToast(getString(R.string.recovery_wallet_select_promote));
                    }
                }
            }
        } else if (view.getId() == R.id.img_back) {
            if (!mResponse) {
                PyEnv.cancelRecovery();
                PyEnv.cancelPinInput();
                PyEnv.cancelAll();
            }
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onCheck(String name, boolean isChecked) {
        if (isChecked) {
            if (!listDates.contains(name)) {
                listDates.add(name);
            }
        } else {
            if (listDates.contains(name)) {
                listDates.remove(name);
            }
        }
        if (listDates.size() == 0) {
            btnRecovery.setEnabled(mHasExist);
        } else {
            btnRecovery.setEnabled(true);
        }
    }
}
