package org.haobtc.onekey.onekeys.walletprocess.importsoft;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.haobtc.onekey.R;
import org.haobtc.onekey.databinding.FragmentImportKeystoreBinding;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.ui.base.BaseFragment;
import org.haobtc.onekey.utils.Daemon;
import org.haobtc.onekey.utils.MyDialog;

/**
 * 使用 Keystore 导入钱包
 *
 * @author Onekey@QuincySx
 * @create 2021-01-17 12:13 PM
 */
@Keep
public class ImportKeystoreFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_CODE = 0;

    private FragmentImportKeystoreBinding mBinding;

    private ImportSoftWalletProvider mImportSoftWalletProvider;
    private OnFinishViewCallBack mOnFinishViewCallBack;
    private OnImportKeystoreCallback mOnImportKeystoreCallback;
    private RxPermissions rxPermissions;
    private Disposable subscriber;
    private MyDialog mLoadingMyDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnImportKeystoreCallback) {
            mOnImportKeystoreCallback = (OnImportKeystoreCallback) context;
        }
        if (context instanceof OnFinishViewCallBack) {
            mOnFinishViewCallBack = (OnFinishViewCallBack) context;
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
        mBinding = FragmentImportKeystoreBinding.inflate(inflater, container, false);
        init(mBinding.getRoot());
        return mBinding.getRoot();
    }

    @Override
    public void init(View view) {
        mBinding.imgBack.setOnClickListener(this);
        mBinding.imgScan.setOnClickListener(this);
        mBinding.imgEyeYes.setOnClickListener(this);
        mBinding.imgEyeNo.setOnClickListener(this);
        mBinding.btnImport.setOnClickListener(this);

        if (mImportSoftWalletProvider != null) {
            switch (mImportSoftWalletProvider.currentCoinType()) {
                case BTC:
                    mBinding.imgCoinType.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                    getResources(), R.drawable.token_btc, null));
                    break;
                case ETH:
                    mBinding.imgCoinType.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                    getResources(), R.drawable.token_eth, null));
                    break;
            }
        }
        handleSlidingConflict();
    }

    @Override
    public int getContentViewId() {
        return 0;
    }

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
                subscriber =
                        rxPermissions
                                .request(Manifest.permission.CAMERA)
                                .subscribe(
                                        granted -> {
                                            if (granted) {
                                                // If you have already authorized it, you can
                                                // directly jump to the QR code scanning interface
                                                Intent intent2 =
                                                        new Intent(
                                                                getContext(),
                                                                CaptureActivity.class);
                                                ZxingConfig config = new ZxingConfig();
                                                config.setPlayBeep(true);
                                                config.setShake(true);
                                                config.setDecodeBarCode(false);
                                                config.setFullScreenScan(true);
                                                config.setShowAlbum(false);
                                                config.setShowbottomLayout(false);
                                                intent2.putExtra(
                                                        Constant.INTENT_ZXING_CONFIG, config);
                                                startActivityForResult(intent2, REQUEST_CODE);
                                            } else {
                                                // Oups permission denied
                                                Toast.makeText(
                                                                getContext(),
                                                                R.string.photopersion,
                                                                Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                break;
            case R.id.img_eye_yes:
                mBinding.imgEyeYes.setVisibility(View.GONE);
                mBinding.imgEyeNo.setVisibility(View.VISIBLE);
                mBinding.editKeystorePass.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance());
                break;
            case R.id.img_eye_no:
                mBinding.imgEyeYes.setVisibility(View.VISIBLE);
                mBinding.imgEyeNo.setVisibility(View.GONE);
                mBinding.editKeystorePass.setTransformationMethod(
                        PasswordTransformationMethod.getInstance());
                break;
            case R.id.btn_import:
                String keystoreContent = mBinding.editKeystoreContent.getText().toString().trim();
                String keystorePass = mBinding.editKeystorePass.getText().toString().trim();
                Single.create(
                                (SingleOnSubscribe<String>)
                                        emitter -> {
                                            List<Kwarg> argList = new ArrayList<>();
                                            if (mImportSoftWalletProvider != null
                                                    && mImportSoftWalletProvider.currentCoinType()
                                                            != null) {
                                                argList.add(
                                                        new Kwarg(
                                                                "coin",
                                                                mImportSoftWalletProvider
                                                                        .currentCoinType()
                                                                        .coinName));
                                            }
                                            argList.add(new Kwarg("data", keystoreContent));
                                            argList.add(new Kwarg("password", keystorePass));
                                            argList.add(new Kwarg("flag", "keystore"));
                                            Daemon.commands.callAttr(
                                                    "verify_legality",
                                                    argList.toArray(new Object[0]));
                                            emitter.onSuccess("success");
                                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> showProgress())
                        .doFinally(this::dismissProgress)
                        .subscribe(
                                new SingleObserver<String>() {
                                    @Override
                                    public void onSubscribe(
                                            io.reactivex.rxjava3.disposables.@io.reactivex.rxjava3
                                                            .annotations.NonNull
                                                    Disposable
                                                    d) {}

                                    @Override
                                    public void onSuccess(
                                            @io.reactivex.rxjava3.annotations.NonNull String s) {
                                        if (mOnImportKeystoreCallback != null) {
                                            mOnImportKeystoreCallback.onImportKeystore(
                                                    keystoreContent, keystorePass);
                                        }
                                    }

                                    @Override
                                    public void onError(
                                            @io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                        if (e.getMessage() != null) {
                                            showToast(HardWareExceptions.getThrowString(e));
                                        }
                                        e.printStackTrace();
                                    }
                                });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                mBinding.editKeystoreContent.setText(content);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleSlidingConflict() {
        mBinding.nestedScrollView.setOnTouchListener(
                (v, event) -> {
                    mBinding.editKeystoreContent
                            .getParent()
                            .requestDisallowInterceptTouchEvent(false);
                    return false;
                });

        mBinding.editKeystoreContent.setOnTouchListener(
                (v, event) -> {
                    mBinding.editKeystoreContent
                            .getParent()
                            .requestDisallowInterceptTouchEvent(true);
                    return false;
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Optional.ofNullable(subscriber).ifPresent(Disposable::dispose);
    }

    private void showProgress() {
        runOnUiThread(
                () -> {
                    if (mLoadingMyDialog == null) {
                        mLoadingMyDialog = MyDialog.showDialog(getContext());
                        mLoadingMyDialog.onTouchOutside(false);
                    }
                    mLoadingMyDialog.show();
                });
    }

    private void dismissProgress() {
        runOnUiThread(
                () -> {
                    if (mLoadingMyDialog != null) {
                        mLoadingMyDialog.dismiss();
                    }
                });
    }

    public interface OnImportKeystoreCallback {

        void onImportKeystore(String keystore, String password);
    }
}
