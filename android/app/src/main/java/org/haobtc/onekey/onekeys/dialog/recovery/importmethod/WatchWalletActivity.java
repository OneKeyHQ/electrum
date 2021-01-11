package org.haobtc.onekey.onekeys.dialog.recovery.importmethod;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.chaquo.python.Kwarg;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.MainSweepcodeBean;
import org.haobtc.onekey.business.qrdecode.QRDecode;
import org.haobtc.onekey.event.ResultEvent;
import org.haobtc.onekey.utils.Daemon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WatchWalletActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.edit_address)
    EditText editAddress;
    @BindView(R.id.btn_import)
    Button btnImport;
    private RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;
    private Disposable mHandlerScanCodeDisposable;

    @Override
    public int getLayoutId() {
        return R.layout.activity_watch_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        rxPermissions = new RxPermissions(this);
        editAddress.addTextChangedListener(this);
    }

    @Override
    public void initData() {
    }

    @SingleClick(value = 1000)
    @OnClick({R.id.img_back, R.id.img_scan, R.id.btn_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_scan:
                rxPermissions
                        .request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                //If you have already authorized it, you can directly jump to the QR code scanning interface
                                Intent intent2 = new Intent(this, CaptureActivity.class);
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
                                Toast.makeText(this, R.string.photopersion, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.btn_import:
                addressIsRight();
                break;
        }
    }

    private void addressIsRight() {
        try {
            Daemon.commands.callAttr("verify_legality", editAddress.getText().toString(), new Kwarg("flag", "address"));
        } catch (Exception e) {
            if (e.getMessage() != null) {
                mToast(e.getMessage().replace("BaseException:", ""));
            }
            e.printStackTrace();
            return;
        }
        EventBus.getDefault().post(new ResultEvent(editAddress.getText().toString()));
        Intent intent = new Intent(WatchWalletActivity.this, ImportWalletSetNameActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
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
        mHandlerScanCodeDisposable = Observable
                .create((ObservableOnSubscribe<String>) emitter -> {
                    MainSweepcodeBean.DataBean dataBean = new QRDecode().decodeAddress(content);
                    if (null == dataBean) {
                        emitter.onError(new RuntimeException("Parse failure"));
                    } else {
                        emitter.onNext(dataBean.getAddress());
                        emitter.onComplete();
                    }
                })
                .doOnSubscribe((s) -> showProgress())
                .doFinally(this::dismissProgress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(address -> {
                    if (editAddress != null) {
                        editAddress.setText(address);
                    }
                }, throwable -> {
                    if (editAddress != null) {
                        editAddress.setText(content);
                    }
                });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 禁止EditText输入空格
        if (s.toString().contains(" ")) {
            String[] str = s.toString().split(" ");
            StringBuilder sb = new StringBuilder();
            for (String value : str) {
                sb.append(value);
            }
            editAddress.setText(sb.toString());
            editAddress.setSelection(start);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s.toString())) {
            btnImport.setEnabled(true);
            btnImport.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_checked, null));
        } else {
            btnImport.setEnabled(false);
            btnImport.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_no_check, null));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandlerScanCodeDisposable != null && !mHandlerScanCodeDisposable.isDisposed()) {
            mHandlerScanCodeDisposable.dispose();
        }
    }
}
