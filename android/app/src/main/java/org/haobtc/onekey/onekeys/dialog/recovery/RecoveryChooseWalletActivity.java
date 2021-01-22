package org.haobtc.onekey.onekeys.dialog.recovery;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.RecoveryWalletAdapter;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.BalanceInfoDTO;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RecoveryChooseWalletActivity extends BaseActivity {

    @BindView(R.id.recl_wallet_list)
    RecyclerView reclWalletList;
    @BindView(R.id.btn_recovery)
    Button btnRecovery;
    @BindView(R.id.loaded_wallet)
    RelativeLayout loadedWallet;
    @BindView(R.id.scroll_wallet)
    NestedScrollView scrollWallet;
    private RecoveryWalletAdapter recoveryWalletAdapter;
    private ArrayList<String> listDates;
    private String name;
    private List<BalanceInfoDTO> walletList;
    private Disposable mDisposable;

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
        //choose wallet data list
        listDates = new ArrayList<>();
        reclWalletList.setNestedScrollingEnabled(false);
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = Single.create((SingleOnSubscribe<List<BalanceInfoDTO>>) emitter -> {
            String password = getIntent().getStringExtra("password");
            String recoverySeed = getIntent().getStringExtra("recoverySeed");
            if (password == null || recoverySeed == null) {
                Log.e("RecoveryChooseWalletActivity", "启动 RecoveryChooseWalletActivity 需要 password recoverySeed 参数.");
                throw new RuntimeException("");
            }
            List<BalanceInfoDTO> balanceInfoDTOS = PyEnv.restoreLocalHDWallet(password, recoverySeed);
            emitter.onSuccess(balanceInfoDTOS);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(walletBalances -> {
            if (loadedWallet == null) {
                return;
            }
            loadedWallet.setVisibility(View.GONE);
            btnRecovery.setVisibility(View.VISIBLE);
            scrollWallet.setVisibility(View.VISIBLE);
            walletList = walletBalances;
            recoveryWalletAdapter = new RecoveryWalletAdapter(this, walletList);
            reclWalletList.setAdapter(recoveryWalletAdapter);
        }, throwable -> {
            if (!TextUtils.isEmpty(throwable.getMessage())) {
                mToast(throwable.getMessage());
            }
            finish();
        });
    }

    @SingleClick
    @OnClick({R.id.btn_recovery})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.btn_recovery) {
            if (btnRecovery.getText().equals(getString(R.string.no_use_wallet))) {
                finish();
            } else {
                listDates.clear();
                boolean hasSelectorWallet = false;
                Map<Integer, Boolean> map = recoveryWalletAdapter.getMap();
                for (int i = 0; i < walletList.size(); i++) {
                    if (map.get(i)) {
                        String name = walletList.get(i).getName();
                        if (!hasSelectorWallet) {
                            this.name = name;
                            hasSelectorWallet = true;
                        }
                        listDates.add(name);
                    }
                }
                String recoveryName = new Gson().toJson(listDates);
                Log.i("recoveryNamejxm", "onViewClicked: " + recoveryName);
                if (listDates != null) {
                    try {
                        Daemon.commands.callAttr("recovery_confirmed", recoveryName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mToast(e.getMessage().replace("BaseException:", ""));
                        return;
                    }
                    EventBus.getDefault().post(new CreateSuccessEvent(name));
                    mIntent(HomeOneKeyActivity.class);
                } else {
                    EventBus.getDefault().post(new CreateSuccessEvent(name));
                    mIntent(HomeOneKeyActivity.class);
                }
            }
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
}
