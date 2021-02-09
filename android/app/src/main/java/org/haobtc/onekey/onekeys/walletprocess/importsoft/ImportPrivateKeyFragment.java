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
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.databinding.FragmentImportPrivateKeyBinding;
import org.haobtc.onekey.exception.HardWareExceptions;
import org.haobtc.onekey.onekeys.walletprocess.OnFinishViewCallBack;
import org.haobtc.onekey.ui.base.BaseFragment;
import org.haobtc.onekey.utils.Daemon;

/**
 * 导入私钥
 *
 * @author Onekey@QuincySx
 * @create 2021-01-17 10:37 AM
 */
@Keep
public class ImportPrivateKeyFragment extends BaseFragment
        implements View.OnClickListener, TextWatcher {
    private static final int REQUEST_CODE = 0;

    private FragmentImportPrivateKeyBinding mBinding;

    private ImportSoftWalletProvider mImportSoftWalletProvider;
    private OnFinishViewCallBack mOnFinishViewCallBack;
    private OnImportPrivateKeyCallback mOnImportPrivateKeyCallback;
    private RxPermissions rxPermissions;
    private Disposable subscriber;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFinishViewCallBack) {
            mOnFinishViewCallBack = (OnFinishViewCallBack) context;
        }
        if (context instanceof OnImportPrivateKeyCallback) {
            mOnImportPrivateKeyCallback = (OnImportPrivateKeyCallback) context;
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
        mBinding = FragmentImportPrivateKeyBinding.inflate(inflater, container, false);
        init(mBinding.getRoot());
        return mBinding.getRoot();
    }

    @Override
    public void init(View view) {
        mBinding.editInputPrivate.addTextChangedListener(this);
        mBinding.imgBack.setOnClickListener(this);
        mBinding.imgScan.setOnClickListener(this);
        mBinding.btnImport.setOnClickListener(this);
        if (mImportSoftWalletProvider != null) {
            switch (mImportSoftWalletProvider.currentCoinType()) {
                case BTC:
                    mBinding.imgCoinType.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                    getResources(), R.drawable.token_btc, null));
                    mBinding.editInputPrivate.setHint(R.string.imput_private_tip);
                    break;
                case ETH:
                    mBinding.imgCoinType.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                    getResources(), R.drawable.token_eth, null));
                    mBinding.editInputPrivate.setHint(R.string.imput_private_eth_tip);
                    break;
            }
        }
    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    @SingleClick
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.btn_import:
                isRightPrivate();
                break;
        }
    }

    private void isRightPrivate() {
        String privateKey = mBinding.editInputPrivate.getText().toString().trim();
        try {
            List<Kwarg> argList = new ArrayList<>();
            if (mImportSoftWalletProvider != null
                    && mImportSoftWalletProvider.currentCoinType() != null) {
                argList.add(
                        new Kwarg("coin", mImportSoftWalletProvider.currentCoinType().coinName));
            }
            argList.add(new Kwarg("data", privateKey));
            argList.add(new Kwarg("flag", "private"));
            Daemon.commands.callAttr("verify_legality", argList.toArray(new Object[0]));
        } catch (Exception e) {
            if (e.getMessage() != null) {
                showToast(HardWareExceptions.getExceptionString(e));
            }
            e.printStackTrace();
            return;
        }
        if (mOnImportPrivateKeyCallback != null) {
            mOnImportPrivateKeyCallback.onImportPrivateKey(privateKey);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                mBinding.editInputPrivate.setText(content);
            }
        }
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
            mBinding.editInputPrivate.setText(sb.toString());
            mBinding.editInputPrivate.setSelection(start);
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
        Optional.ofNullable(subscriber).ifPresent(Disposable::dispose);
    }

    public interface OnImportPrivateKeyCallback {
        void onImportPrivateKey(String privateKey);
    }
}
