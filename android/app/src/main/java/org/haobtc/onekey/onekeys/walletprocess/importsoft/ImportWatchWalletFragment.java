package org.haobtc.onekey.onekeys.walletprocess.importsoft;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import com.chaquo.python.Kwarg;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.MainSweepcodeBean;
import org.haobtc.onekey.business.qrdecode.QRDecode;
import org.haobtc.onekey.databinding.FragmentImportWatchWalletBinding;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.ui.base.BaseFragment;
import org.haobtc.onekey.utils.Daemon;

/**
 * 导入观察钱包
 *
 * @author Onekey@QuincySx
 * @create 2021-01-17 11:23 AM
 */
@Keep
public class ImportWatchWalletFragment extends BaseFragment
        implements TextWatcher, View.OnClickListener {
    private static final int REQUEST_CODE = 0;

    private FragmentImportWatchWalletBinding mBinding;

    private ImportSoftWalletProvider mImportSoftWalletProvider;
    private OnFinishViewCallBack mOnFinishViewCallBack;
    private OnImportWatchAddressCallback mOnImportWatchAddressCallback;
    private BaseActivity mBaseActivity;

    private RxPermissions rxPermissions;
    private Disposable mHandlerScanCodeDisposable;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFinishViewCallBack) {
            mOnFinishViewCallBack = (OnFinishViewCallBack) context;
        }
        if (context instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) context;
        }
        if (context instanceof OnImportWatchAddressCallback) {
            mOnImportWatchAddressCallback = (OnImportWatchAddressCallback) context;
        }
        if (context instanceof ImportSoftWalletProvider) {
            mImportSoftWalletProvider = (ImportSoftWalletProvider) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mBinding = FragmentImportWatchWalletBinding.inflate(inflater, container, false);
        init(mBinding.getRoot());
        return mBinding.getRoot();
    }

    @Override
    public void init(View view) {
        mBinding.editAddress.addTextChangedListener(this);
        mBinding.imgBack.setOnClickListener(this);
        mBinding.imgScan.setOnClickListener(this);
        mBinding.btnImport.setOnClickListener(this);
        if (mImportSoftWalletProvider != null) {
            switch (mImportSoftWalletProvider.currentCoinType()) {
                case BTC:
                    mBinding.imgCoinType.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                    getResources(), R.drawable.token_btc, null));
                    mBinding.editAddress.setHint(R.string.watch_tip);
                    break;
                case ETH:
                    mBinding.imgCoinType.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                    getResources(), R.drawable.token_eth, null));
                    mBinding.editAddress.setHint(R.string.watch_eth_tip);
                    break;
            }
        }
    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                if (mOnFinishViewCallBack != null) {
                    mOnFinishViewCallBack.onFinishView();
                }
                break;
            case R.id.img_scan:
                if (rxPermissions == null) {
                    rxPermissions = new RxPermissions(this);
                }
                rxPermissions
                        .request(Manifest.permission.CAMERA)
                        .subscribe(
                                granted -> {
                                    if (granted) { // Always true pre-M
                                        // If you have already authorized it, you can directly jump
                                        // to the QR code scanning interface
                                        Intent intent2 =
                                                new Intent(getContext(), CaptureActivity.class);
                                        ZxingConfig config = new ZxingConfig();
                                        config.setPlayBeep(true);
                                        config.setShake(true);
                                        config.setDecodeBarCode(false);
                                        config.setFullScreenScan(true);
                                        config.setShowAlbum(false);
                                        config.setShowbottomLayout(false);
                                        intent2.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                        startActivityForResult(intent2, REQUEST_CODE);
                                    } else { // Oups permission denied
                                        Toast.makeText(
                                                        getContext(),
                                                        R.string.photopersion,
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                break;
            case R.id.btn_import:
                addressIsRight();
                break;
        }
    }

    private void addressIsRight() {
        String watchAddress = mBinding.editAddress.getText().toString().trim();
        try {
            List<Kwarg> argList = new ArrayList<>();
            if (mImportSoftWalletProvider != null
                    && mImportSoftWalletProvider.currentCoinType() != null) {
                argList.add(
                        new Kwarg("coin", mImportSoftWalletProvider.currentCoinType().coinName));
            }
            argList.add(new Kwarg("data", watchAddress));
            argList.add(new Kwarg("flag", "address"));
            Daemon.commands.callAttr("verify_legality", argList.toArray(new Object[0]));
        } catch (Exception e) {
            if (e.getMessage() != null) {
                showToast(HardWareExceptions.getExceptionString(e));
            }
            e.printStackTrace();
            return;
        }
        if (mOnImportWatchAddressCallback != null) {
            mOnImportWatchAddressCallback.onImportWatchAddress(watchAddress);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                handleScanCode(content);
            }
        }
    }

    /**
     * 处理并解析二维码中的比特币地址
     *
     * @param content 二维码内容
     */
    private void handleScanCode(String content) {
        if (mHandlerScanCodeDisposable != null && !mHandlerScanCodeDisposable.isDisposed()) {
            mHandlerScanCodeDisposable.dispose();
        }
        mHandlerScanCodeDisposable =
                Observable.create(
                                (ObservableOnSubscribe<String>)
                                        emitter -> {
                                            MainSweepcodeBean.DataBean dataBean =
                                                    new QRDecode().decodeAddress(content);
                                            if (null == dataBean) {
                                                emitter.onError(
                                                        new RuntimeException("Parse failure"));
                                            } else {
                                                emitter.onNext(dataBean.getAddress());
                                                emitter.onComplete();
                                            }
                                        })
                        .doOnSubscribe(
                                (s) -> {
                                    if (mBaseActivity != null) {
                                        mBaseActivity.showProgress();
                                    }
                                })
                        .doFinally(
                                () -> {
                                    if (mBaseActivity != null) {
                                        mBaseActivity.dismissProgress();
                                    }
                                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                address -> {
                                    if (mBinding.editAddress != null) {
                                        mBinding.editAddress.setText(address);
                                    }
                                },
                                throwable -> {
                                    if (mBinding.editAddress != null) {
                                        mBinding.editAddress.setText(content);
                                    }
                                });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 禁止EditText输入空格
        if (s.toString().contains(" ")) {
            String[] str = s.toString().split(" ");
            StringBuilder sb = new StringBuilder();
            for (String value : str) {
                sb.append(value);
            }
            mBinding.editAddress.setText(sb.toString());
            mBinding.editAddress.setSelection(start);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s.toString())) {
            mBinding.btnImport.setEnabled(true);
            mBinding.btnImport.setBackground(
                    ResourcesCompat.getDrawable(getResources(), R.drawable.btn_checked, null));
        } else {
            mBinding.btnImport.setEnabled(false);
            mBinding.btnImport.setBackground(
                    ResourcesCompat.getDrawable(getResources(), R.drawable.btn_no_check, null));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandlerScanCodeDisposable != null && !mHandlerScanCodeDisposable.isDisposed()) {
            mHandlerScanCodeDisposable.dispose();
        }
    }

    public interface OnImportWatchAddressCallback {
        void onImportWatchAddress(String watchAddress);
    }
}
