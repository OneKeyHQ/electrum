package org.haobtc.onekey.onekeys.homepage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.haobtc.onekey.BuildConfig;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.bean.DAppBrowserBean;
import org.haobtc.onekey.bean.JsBridgeRequestBean;
import org.haobtc.onekey.bean.JsBridgeResponseBean;
import org.haobtc.onekey.bean.WalletAccountInfo;
import org.haobtc.onekey.business.wallet.DappManager;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.onekeys.dappbrowser.ui.BaseAlertBottomDialog;
import org.haobtc.onekey.onekeys.dappbrowser.ui.DappBrowserActivity;
import org.haobtc.onekey.ui.base.BaseFragment;
import org.haobtc.onekey.ui.dialog.SelectAccountBottomSheetDialog;
import org.haobtc.onekey.viewmodel.AppWalletViewModel;

public class FindFragment extends BaseFragment {

    @BindView(R.id.webview_bridge)
    BridgeWebView webview;

    @BindView(R.id.btn_test)
    Button btn;

    private final DappManager mDappManager = new DappManager();
    private AppWalletViewModel mAppWalletViewModel;
    private Gson mGson = new Gson();

    @Override
    public int getContentViewId() {
        return R.layout.fragment_tab_find;
    }

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAppWalletViewModel =
                new ViewModelProvider(MyApplication.getInstance()).get(AppWalletViewModel.class);

        webview.loadUrl("https://dapp.onekey.so/");
        webview.registerHandler(
                "callNativeMethod",
                (data, function) -> {
                    JsBridgeRequestBean jsBridgeRequestBean =
                            mGson.fromJson(data, JsBridgeRequestBean.class);

                    switch (jsBridgeRequestBean.getMethod()) {
                        case "openDapp":
                            String params = jsBridgeRequestBean.getParams();
                            checkAccount(params);
                            break;
                    }
                    Logger.e("open dapp: " + data);
                    function.onCallBack(
                            mGson.toJson(
                                    new JsBridgeResponseBean(
                                            jsBridgeRequestBean.getId(), "success")));
                });
        btn.setOnClickListener(
                v -> {
                    webview.callHandler(
                            "callJavaScriptMethod",
                            "hello web!",
                            data -> {
                                showToast("Web Receive the success: " + data);
                            });
                });

        if (BuildConfig.DEBUG) {
            // btn.setVisibility(View.VISIBLE);
        }
    }

    private void checkAccount(String data) {
        Single.fromCallable(() -> mGson.fromJson(data, DAppBrowserBean.class))
                .map(
                        dAppBrowserBean -> {
                            if (dAppBrowserBean.getUrl() != null
                                    && dAppBrowserBean.getUrl().contains(":")) {
                                String substring =
                                        dAppBrowserBean
                                                .getUrl()
                                                .substring(
                                                        0, dAppBrowserBean.getUrl().indexOf(":"));
                                dAppBrowserBean.setProtocol(substring);
                            }
                            return dAppBrowserBean;
                        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        dAppBrowserBean -> {
                            if (dAppBrowserBean.getChain() == null) {
                                return;
                            }
                            Vm.CoinType coinType =
                                    Vm.CoinType.convertByCoinName(dAppBrowserBean.getChain());
                            // 判断不支持的币种
                            if (coinType == null) {
                                BaseAlertBottomDialog dialog =
                                        new BaseAlertBottomDialog(requireContext());
                                dialog.show();
                                dialog.setTitle(
                                        getString(
                                                R.string.title_does_not_support,
                                                dAppBrowserBean.getChain()));
                                dialog.setMessage(R.string.support_less_promote);
                                return;
                            }

                            MutableLiveData<WalletAccountInfo> currentWalletAccountInfo =
                                    mAppWalletViewModel.currentWalletAccountInfo;
                            if (currentWalletAccountInfo.getValue() != null
                                    && currentWalletAccountInfo.getValue().getCoinType()
                                            != coinType) {
                                BaseAlertBottomDialog dialog =
                                        new BaseAlertBottomDialog(requireContext());
                                dialog.show();
                                dialog.setIcon(dAppBrowserBean.getLogoImage());
                                dialog.setTitle(
                                        getString(
                                                R.string.title_account_unavailable,
                                                dAppBrowserBean.getChain()));
                                dialog.setMessage(
                                        getString(
                                                R.string.hint_account_unavailable_content,
                                                dAppBrowserBean.getChain()));
                                dialog.setPrimaryButtonListener(
                                        v -> {
                                            dialog.dismiss();
                                            SelectAccountBottomSheetDialog.newInstance(coinType)
                                                    .setOnSelectAccountCallback(
                                                            info -> {
                                                                openDapp(dAppBrowserBean);
                                                            })
                                                    .show(
                                                            getParentFragmentManager(),
                                                            "SelectAccount");
                                        });
                                dialog.setSecondaryButtonListener(
                                        v -> {
                                            dialog.dismiss();
                                        });
                            } else {
                                openDapp(dAppBrowserBean);
                            }
                        },
                        Throwable::printStackTrace)
                .isDisposed();
    }

    private void openDapp(DAppBrowserBean bean) {
        Single.fromCallable(
                        () -> {
                            bean.setFirstUse(mDappManager.firstUse(bean.getName()));
                            return bean;
                        })
                .map(
                        dAppBrowserBean -> {
                            if (!dAppBrowserBean.getFirstUse()) {
                                DappBrowserActivity.start(requireContext(), dAppBrowserBean);
                            }
                            return dAppBrowserBean;
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dAppBrowserBean -> {
                            if (dAppBrowserBean.getFirstUse()) {
                                BaseAlertBottomDialog dialog =
                                        new BaseAlertBottomDialog(requireContext());
                                dialog.show();
                                dialog.setIcon(dAppBrowserBean.getLogoImage());
                                dialog.setTitle(
                                        getString(
                                                R.string.title_frist_use_dapp,
                                                dAppBrowserBean.getName()));
                                dialog.setMessage(
                                        getString(
                                                R.string.title_frist_use_dapp_privacy,
                                                dAppBrowserBean.getName()));
                                dialog.setPrimaryButtonText(R.string.i_know_);
                                dialog.setPrimaryButtonListener(
                                        v -> {
                                            mDappManager.userDapp(dAppBrowserBean.getName());
                                            DappBrowserActivity.start(
                                                    requireContext(), dAppBrowserBean);
                                        });
                                dialog.setSecondaryButtonListener(
                                        v -> {
                                            dialog.dismiss();
                                        });
                            }
                        },
                        Throwable::printStackTrace)
                .isDisposed();
    }
}
