package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.common.base.Strings;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.HdWalletAssetAdapter;
import org.haobtc.onekey.bean.AllWalletBalanceBean;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.business.wallet.BalanceManager;
import org.haobtc.onekey.business.wallet.SystemConfigManager;
import org.haobtc.onekey.business.wallet.bean.WalletBalanceBean;
import org.haobtc.onekey.onekeys.homepage.process.HdWalletDetailActivity;

public class AllAssetsActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.test_all_assets)
    TextView testAllAssets;

    @BindView(R.id.edit_search)
    EditText editSearch;

    @BindView(R.id.recl_assets)
    RecyclerView reclAssets;

    @BindView(R.id.tet_None)
    TextView tetNone;

    private List<WalletBalanceBean> walletInfo;
    private ArrayList<WalletBalanceBean> searchList;
    private SystemConfigManager mSystemConfigManager;
    private BalanceManager mBalanceManager;
    private Disposable mLoadDisposable;

    @Override
    public int getLayoutId() {
        return R.layout.activity_all_assets;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        mSystemConfigManager = new SystemConfigManager(this);
        mBalanceManager = new BalanceManager();
        editSearch.addTextChangedListener(this);
    }

    @Override
    public void initData() {
        searchList = new ArrayList<>();
        reclAssets.setNestedScrollingEnabled(false);
        // get all wallet
        getAllWalletList();
    }

    private void getAllWalletList() {
        if (mLoadDisposable != null && !mLoadDisposable.isDisposed()) {
            mLoadDisposable.dispose();
        }
        mLoadDisposable =
                Observable.create(
                                (ObservableOnSubscribe<PyResponse<AllWalletBalanceBean>>)
                                        emitter -> {
                                            PyResponse<AllWalletBalanceBean> allWalletBalance =
                                                    mBalanceManager.getAllWalletBalances();
                                            emitter.onNext(allWalletBalance);
                                            emitter.onComplete();
                                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> showProgress())
                        .doFinally(this::dismissProgress)
                        .subscribe(
                                allWalletBalanceString -> {
                                    if (Strings.isNullOrEmpty(allWalletBalanceString.getErrors())) {
                                        AllWalletBalanceBean allWalletBalance =
                                                allWalletBalanceString.getResult();
                                        if (allWalletBalance != null
                                                && allWalletBalance.getWalletInfo().size() > 0) {
                                            tetNone.setVisibility(View.GONE);

                                            String allBalance = allWalletBalance.getAllBalance();
                                            String fiat =
                                                    allBalance.substring(
                                                            0, allBalance.indexOf(" "));
                                            float f = Float.parseFloat(fiat);
                                            DecimalFormat decimalFormat =
                                                    new DecimalFormat(
                                                            "0.00"); // 构造方法的字符格式这里如果小数不足2位,会以0补足.
                                            String money = decimalFormat.format(f);
                                            String currencySymbol =
                                                    mSystemConfigManager.getCurrentFiatSymbol();
                                            testAllAssets.setText(
                                                    String.format("%s %s", currencySymbol, money));
                                            walletInfo = allWalletBalance.getWalletInfo();
                                            HdWalletAssetAdapter hdWalletAssetAdapter =
                                                    new HdWalletAssetAdapter(
                                                            getBaseContext(), walletInfo);
                                            reclAssets.setAdapter(hdWalletAssetAdapter);
                                            hdWalletAssetAdapter.setOnItemClickListener(
                                                    (adapter, view, position) -> {
                                                        WalletBalanceBean walletBalanceBean =
                                                                (WalletBalanceBean)
                                                                        adapter.getData()
                                                                                .get(position);
                                                        String name = walletBalanceBean.getName();
                                                        HdWalletDetailActivity.start(
                                                                mContext, name);
                                                    });
                                        }
                                    } else {
                                        tetNone.setVisibility(View.VISIBLE);
                                        mToast(allWalletBalanceString.getErrors());
                                    }
                                },
                                e -> {
                                    tetNone.setVisibility(View.VISIBLE);
                                    mToast(e.getMessage().replace("BaseException:", ""));
                                    e.printStackTrace();
                                });
    }

    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        searchList.clear();
        if (!TextUtils.isEmpty(s.toString()) && walletInfo != null && walletInfo.size() > 0) {
            for (int i = 0; i < walletInfo.size(); i++) {
                if (walletInfo.get(i).getName().startsWith(s.toString())) {
                    searchList.add(walletInfo.get(i));
                }
            }
            HdWalletAssetAdapter hdWalletAssetAdapter =
                    new HdWalletAssetAdapter(getBaseContext(), searchList);
            reclAssets.setAdapter(hdWalletAssetAdapter);
        } else {
            HdWalletAssetAdapter hdWalletAssetAdapter =
                    new HdWalletAssetAdapter(getBaseContext(), walletInfo);
            reclAssets.setAdapter(hdWalletAssetAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadDisposable != null && !mLoadDisposable.isDisposed()) {
            mLoadDisposable.dispose();
        }
    }
}
