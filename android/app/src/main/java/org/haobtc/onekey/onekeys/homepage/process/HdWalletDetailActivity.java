package org.haobtc.onekey.onekeys.homepage.process;

import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME;
import static org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_TYPE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.common.base.Strings;
import com.lxj.xpopup.XPopup;
import com.orhanobut.logger.Logger;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.CurrentAddressDetail;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.Vm;
import org.haobtc.onekey.constant.Vm.WalletType;
import org.haobtc.onekey.event.FixWalletNameEvent;
import org.haobtc.onekey.event.LoadOtherWalletEvent;
import org.haobtc.onekey.event.SecondEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.backup.BackupGuideActivity;
import org.haobtc.onekey.onekeys.homepage.mindmenu.DeleteWalletActivity;
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.haobtc.onekey.ui.dialog.BackupRequireDialog;
import org.haobtc.onekey.ui.dialog.DeleteWalletTipsDialog;
import org.haobtc.onekey.ui.dialog.ExportTipsDialog;
import org.haobtc.onekey.ui.dialog.custom.CustomBackupDialog;
import org.haobtc.onekey.ui.dialog.custom.CustomReSetBottomPopup;
import org.haobtc.onekey.utils.ClipboardUtils;
import org.haobtc.onekey.utils.Daemon;

public class HdWalletDetailActivity extends BaseActivity {

    private static final int REQUEST_TAG = 0xF00;
    private static final int REQUEST_PASSWORD_TAG = 0x100;
    private static final int REQUEST_PASSWORD_EXPORT_WORD = 0x1 | REQUEST_PASSWORD_TAG;
    private static final int REQUEST_PASSWORD_EXPORT_PRIVATE_KEY = 0x2 | REQUEST_PASSWORD_TAG;
    private static final int REQUEST_PASSWORD_EXPORT_KEYSTORE = 0x3 | REQUEST_PASSWORD_TAG;
    private static final int REQUEST_PASSWORD_DELETE_HD_DERIVED = 0x4 | REQUEST_PASSWORD_TAG;
    private static final String WALLET_NAME = "hdWalletName";

    @BindView(R.id.img_token_logo)
    ImageView mImageTokenLogo;

    @BindView(R.id.text_wallet_name)
    TextView textWalletName;

    @BindView(R.id.text_address)
    TextView textAddress;

    @BindView(R.id.lin_hd_wallet_show)
    LinearLayout linHdWalletShow;

    @BindView(R.id.lin_single_show)
    LinearLayout linSingleShow;

    @BindView(R.id.text_hd_wallet)
    TextView textHdWallet;

    @BindView(R.id.lin_single)
    LinearLayout linSingle;

    @BindView(R.id.text_sign)
    TextView textSign;

    @BindView(R.id.lin_hardware)
    LinearLayout linHardware;

    @BindView(R.id.text_addr)
    TextView textAddr;

    @BindView(R.id.text_content_type)
    TextView textContentType;

    @BindView(R.id.rel_delete_wallet)
    RelativeLayout deleteLayout;

    @BindView(R.id.delete_tv)
    TextView deleteTV;

    @BindView(R.id.delete_ll)
    LinearLayout mDeleteLayout;

    @BindView(R.id.rel_export_private_key)
    RelativeLayout exportPrivateLayout;

    @BindView(R.id.rel_export_word)
    RelativeLayout exportWordLayout;

    @BindView(R.id.rel_export_keystore)
    View exportKeystoreLayout;

    private SharedPreferences preferences;
    private String showWalletType;
    private String qrData;
    private String deleteHdWalletName;

    private Disposable mDisposable;

    @Override
    public int getLayoutId() {
        return R.layout.activity_hd_wallet_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        inits();
    }

    public static void start(Context context, String name) {
        Intent intent = new Intent(context, HdWalletDetailActivity.class);
        intent.putExtra(WALLET_NAME, name);
        context.startActivity(intent);
    }

    private void inits() {
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        showWalletType = preferences.getString(CURRENT_SELECTED_WALLET_TYPE, "");
        String hdWalletName = getIntent().getStringExtra("hdWalletName");

        Vm.CoinType coinType = Vm.convertCoinType(showWalletType);
        @WalletType int walletType = Vm.convertWalletType(showWalletType);

        switch (coinType) {
            default:
            case BTC:
                mImageTokenLogo.setImageDrawable(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.token_btc, null));
                break;
            case ETH:
                mImageTokenLogo.setImageDrawable(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.token_eth, null));
                break;
        }

        Logger.e(showWalletType);
        textWalletName.setText(hdWalletName);

        switch (walletType) {
            case WalletType.MAIN:
                textHdWallet.setText(getString(R.string.hd_wallet_account));
                // HD wallet detail and derive wallet
                linHdWalletShow.setVisibility(View.VISIBLE);
                linSingleShow.setVisibility(View.GONE);
                deleteTV.setText(R.string.delete_wallet_single);
                break;
            case WalletType.HARDWARE:
                textHdWallet.setText(getString(R.string.hardware_wallet));
                linSingleShow.setVisibility(View.VISIBLE);
                // 硬件 导出私钥和助记词不可见
                linSingle.setVisibility(View.GONE);
                deleteTV.setText(R.string.delete_wallet_single);
                break;
            case WalletType.IMPORT_WATCH:
                textHdWallet.setText(getString(R.string.watch_wallet));
                deleteTV.setText(R.string.delete_wallet_single);
                // 观察钱包 导出私钥和助记词不可见
                linSingle.setVisibility(View.GONE);
                break;
            case WalletType.IMPORT_PRIVATE:
                switch (coinType) {
                    case BTC:
                    case ETH:
                        exportWordLayout.setVisibility(View.GONE);
                        exportPrivateLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        exportPrivateLayout.setVisibility(View.GONE);
                        exportWordLayout.setVisibility(View.VISIBLE);
                        break;
                }
            case WalletType.STANDARD:
                linHdWalletShow.setVisibility(View.GONE);
                linSingleShow.setVisibility(View.VISIBLE);
                textHdWallet.setText(getString(R.string.single));

                switch (coinType) {
                    default:
                    case BTC:
                        exportKeystoreLayout.setVisibility(View.GONE);
                        break;
                    case ETH:
                        exportKeystoreLayout.setVisibility(View.VISIBLE);
                        break;
                }
                break;
        }
    }

    @Override
    public void initData() {
        // get receive address
        getAddressInfo();
    }

    private void getAddressInfo() {
        PyResponse<CurrentAddressDetail> response = PyEnv.getCurrentAddressInfo();
        String error = response.getErrors();
        if (Strings.isNullOrEmpty(error)) {
            qrData = response.getResult().getQrData();
            String addr = response.getResult().getAddr();
            String front6 = addr.substring(0, 6);
            String after6 = addr.substring(addr.length() - 6);
            textAddr.setText(addr);
            textAddress.setText(String.format("%s…%s", front6, after6));
        }
    }

    @SingleClick
    @OnClick({
        R.id.img_back,
        R.id.img_copy,
        R.id.rel_export_word,
        R.id.rel_export_private_key,
        R.id.rel_export_keystore,
        R.id.rel_delete_wallet,
        R.id.text_wallet_name,
        R.id.text_sign
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_copy:
                // copy text
                ClipboardUtils.copyText(this, textAddr.getText().toString());
                break;
            case R.id.text_wallet_name:
                // fix wallet name
                fixWalletNameDialog(HdWalletDetailActivity.this, R.layout.edit_wallet_name);
                break;
            case R.id.rel_export_word:
            case R.id.rel_export_private_key:
            case R.id.rel_export_keystore:
                export(view.getId());
                break;
            case R.id.rel_delete_wallet:
                boolean hasBackup = PyEnv.hasBackup(HdWalletDetailActivity.this);
                int walletType = Vm.convertWalletType(showWalletType);
                if (walletType == WalletType.MAIN) {
                    showDeleteDialog(hasBackup);
                } else {
                    if (walletType == WalletType.HARDWARE
                            || walletType == WalletType.IMPORT_WATCH) {
                        DeleteWalletTipsDialog dialog = new DeleteWalletTipsDialog();
                        if (walletType == WalletType.IMPORT_WATCH) {
                            Bundle bundle = new Bundle();
                            bundle.putInt(Constant.WALLET_TYPE, 1);
                            dialog.setArguments(bundle);
                        }
                        dialog.setConfirmClickListener(
                                dialogFragment -> {
                                    deleteWatchWallet(dialogFragment);
                                });
                        dialog.show(getSupportFragmentManager(), "");
                    } else {
                        Intent intent =
                                new Intent(HdWalletDetailActivity.this, DeleteWalletActivity.class);
                        intent.putExtra("importHdword", "deleteSingleWallet");
                        intent.putExtra("walletName", textWalletName.getText().toString());
                        intent.putExtra("isBackup", hasBackup);
                        intent.putExtra("delete_wallet_type", showWalletType);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.text_sign:
            default:
        }
    }

    private void deleteWatchWallet(DialogFragment dialogFragment) {
        final String keyName =
                PreferencesManager.get(
                                HdWalletDetailActivity.this,
                                "Preferences",
                                Constant.CURRENT_SELECTED_WALLET_NAME,
                                "")
                        .toString();
        Disposable subscribe =
                Single.create(
                                (SingleOnSubscribe<String>)
                                        emitter -> {
                                            PyResponse<Void> response =
                                                    PyEnv.deleteWallet("", keyName, false);
                                            if (Strings.isNullOrEmpty(response.getErrors())) {
                                                emitter.onSuccess("");
                                            } else {
                                                emitter.onError(
                                                        new RuntimeException(response.getErrors()));
                                            }
                                        })
                        .doOnSubscribe(disposable -> showProgress())
                        .doFinally(this::dismissProgress)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                error -> {
                                    if (dialogFragment != null) {
                                        dialogFragment.dismiss();
                                    }
                                    PreferencesManager.remove(this, Constant.WALLETS, keyName);
                                    EventBus.getDefault().post(new LoadOtherWalletEvent());
                                    finish();
                                    Toast.makeText(this, R.string.delete_succse, Toast.LENGTH_SHORT)
                                            .show();
                                },
                                throwable -> {
                                    if (dialogFragment != null) {
                                        dialogFragment.dismiss();
                                    }
                                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT)
                                            .show();
                                    throwable.printStackTrace();
                                });
        mCompositeDisposable.add(subscribe);
    }

    /**
     * 删除主钱包的警告弹窗
     *
     * @param hasBackup 是否备份
     */
    private void showDeleteDialog(boolean hasBackup) {
        new XPopup.Builder(mContext)
                .dismissOnTouchOutside(true)
                .isDestroyOnDismiss(true)
                .asCustom(
                        new CustomReSetBottomPopup(
                                mContext,
                                new CustomReSetBottomPopup.onClick() {
                                    @Override
                                    public void onConfirm() {
                                        if (!hasBackup) {
                                            showBackDialog();
                                        } else {
                                            doSelect();
                                        }
                                    }
                                },
                                CustomReSetBottomPopup.deleteHdChildren))
                .show();
    }

    private void doSelect() {
        mCompositeDisposable.add(
                Single.create(
                                (SingleOnSubscribe<Long>)
                                        emitter -> {
                                            long count = 0L;
                                            for (Vm.CoinType coinType : Vm.CoinType.values()) {
                                                if (!coinType.enable) {
                                                    continue;
                                                }

                                                try {
                                                    PyResponse<String> response =
                                                            PyEnv.getDerivedNum(coinType);
                                                    if (Strings.isNullOrEmpty(
                                                            response.getErrors())) {
                                                        count +=
                                                                Long.parseLong(
                                                                        response.getResult());
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            emitter.onSuccess(count);
                                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                coinNum -> {
                                    if (coinNum <= 1) {
                                        showDeleteHDRootWallet();
                                    } else {
                                        showDeleteHdDeriveWallet();
                                    }
                                }));
    }

    private void showDeleteHdDeriveWallet() {
        startActivityForResult(
                new Intent(this, SoftPassActivity.class), REQUEST_PASSWORD_DELETE_HD_DERIVED);
    }

    private void showDeleteHDRootWallet() {
        new XPopup.Builder(mContext)
                .dismissOnTouchOutside(true)
                .isDestroyOnDismiss(true)
                .asCustom(
                        new CustomReSetBottomPopup(
                                mContext,
                                new CustomReSetBottomPopup.onClick() {
                                    @Override
                                    public void onConfirm() {
                                        deleteHdRoot();
                                    }
                                },
                                CustomReSetBottomPopup.deleteHdRoot))
                .show();
    }

    private void deleteHdRoot() {
        Map<String, ?> wallets = PreferencesManager.getAll(this, Constant.WALLETS);
        if (!wallets.isEmpty()) {
            wallets.entrySet()
                    .forEach(
                            stringEntry -> {
                                LocalWalletInfo info =
                                        LocalWalletInfo.objectFromData(
                                                stringEntry.getValue().toString());
                                String type = info.getType();
                                String name = info.getName();
                                deleteHdWalletName = name;
                            });
        }
        hdWalletIsBackup();
    }

    private void hdWalletIsBackup() {
        Log.i("deleteHdWalletNamejxm", "hdWalletIsBackup: " + deleteHdWalletName);
        try {
            boolean isBackup = PyEnv.hasBackup(HdWalletDetailActivity.this);
            if (isBackup) {
                Intent intent = new Intent(mContext, DeleteWalletActivity.class);
                intent.putExtra("deleteHdWalletName", deleteHdWalletName);
                startActivity(intent);
                finish();
            } else {
                // 没备份提示备份
                new BackupRequireDialog(this).show(getSupportFragmentManager(), "");
            }
        } catch (Exception e) {
            mToast(e.getMessage());
            e.printStackTrace();
        }
    }

    private void showBackDialog() {
        new XPopup.Builder(mContext)
                .dismissOnTouchOutside(false)
                .isDestroyOnDismiss(true)
                .asCustom(
                        new CustomBackupDialog(
                                mContext,
                                new CustomBackupDialog.onClick() {
                                    @Override
                                    public void onBack() {
                                        finish();
                                    }
                                }))
                .show();
    }

    private void export(int id) {
        int request = REQUEST_PASSWORD_TAG;
        switch (id) {
            case R.id.rel_export_word:
                request = REQUEST_PASSWORD_EXPORT_WORD;
                break;
            case R.id.rel_export_private_key:
                request = REQUEST_PASSWORD_EXPORT_PRIVATE_KEY;
                break;
            case R.id.rel_export_keystore:
                request = REQUEST_PASSWORD_EXPORT_KEYSTORE;
                break;
        }
        final int finalRequest = request;
        new ExportTipsDialog()
                .setOnConfirmListener(
                        v -> {
                            startActivityForResult(
                                    new Intent(this, SoftPassActivity.class), finalRequest);
                        })
                .show(getSupportFragmentManager(), "export");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode & REQUEST_TAG) == REQUEST_PASSWORD_TAG && resultCode == RESULT_OK) {
            SoftPassActivity.ResultDataBean resultDataBean =
                    SoftPassActivity.decodeResultData(data);
            onGotPass(requestCode, resultDataBean.password);
        }
    }

    private void onGotPass(int requestCode, String password) {
        Single<String> single = null;
        switch (requestCode) {
            case REQUEST_PASSWORD_EXPORT_WORD:
                single =
                        Single.create(
                                (SingleOnSubscribe<String>)
                                        emitter -> {
                                            PyResponse<String> response =
                                                    PyEnv.exportMnemonics(
                                                            password,
                                                            preferences.getString(
                                                                    CURRENT_SELECTED_WALLET_NAME,
                                                                    ""));
                                            String errors = response.getErrors();
                                            if (Strings.isNullOrEmpty(errors)) {
                                                Intent intent =
                                                        new Intent(
                                                                HdWalletDetailActivity.this,
                                                                BackupGuideActivity.class);
                                                intent.putExtra("exportWord", response.getResult());
                                                intent.putExtra("importHdword", "exportHdword");
                                                startActivity(intent);
                                                emitter.onSuccess("");
                                            } else {
                                                emitter.onError(new RuntimeException(errors));
                                            }
                                        });

                break;
            case REQUEST_PASSWORD_EXPORT_PRIVATE_KEY:
                single =
                        Single.create(
                                (SingleOnSubscribe<String>)
                                        emitter -> {
                                            PyResponse<String> response =
                                                    PyEnv.exportPrivateKey(password);
                                            String errors = response.getErrors();
                                            if (Strings.isNullOrEmpty(errors)) {
                                                Intent intent =
                                                        new Intent(
                                                                this, ExportPrivateActivity.class);
                                                intent.putExtra("privateKey", response.getResult());
                                                startActivity(intent);
                                                emitter.onSuccess("");
                                            } else {
                                                emitter.onError(new RuntimeException(errors));
                                            }
                                        });

                break;
            case REQUEST_PASSWORD_EXPORT_KEYSTORE:
                single =
                        Single.create(
                                (SingleOnSubscribe<String>)
                                        emitter -> {
                                            PyResponse<String> response =
                                                    PyEnv.exportKeystore(password);
                                            String errors = response.getErrors();
                                            if (Strings.isNullOrEmpty(errors)) {
                                                ExportKeystoreActivity.start(
                                                        this, response.getResult());
                                                emitter.onSuccess("");
                                            } else {
                                                emitter.onError(new RuntimeException(errors));
                                            }
                                        });
                break;
            case REQUEST_PASSWORD_DELETE_HD_DERIVED:
                deleteSingleWallet(password);
                break;
        }

        if (single != null) {
            if (mDisposable != null && !mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mDisposable =
                    single.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> showProgress())
                            .doFinally(this::dismissProgress)
                            .subscribe(
                                    success -> {
                                        finish();
                                    },
                                    throwable -> {
                                        mToast(throwable.getMessage());
                                    });
        }
    }

    private void deleteSingleWallet(String password) {
        final String keyName =
                PreferencesManager.get(
                                HdWalletDetailActivity.this,
                                "Preferences",
                                Constant.CURRENT_SELECTED_WALLET_NAME,
                                "")
                        .toString();
        Disposable subscribe =
                Single.create(
                                (SingleOnSubscribe<String>)
                                        emitter -> {
                                            PyResponse<Void> response =
                                                    PyEnv.deleteWallet(password, keyName, false);
                                            if (Strings.isNullOrEmpty(response.getErrors())) {
                                                emitter.onSuccess("");
                                            } else {
                                                emitter.onError(
                                                        new RuntimeException(response.getErrors()));
                                            }
                                        })
                        .doOnSubscribe(disposable -> showProgress())
                        .doFinally(this::dismissProgress)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                error -> {
                                    onDeleteSuccess(keyName);
                                },
                                throwable -> {
                                    mlToast(throwable.getMessage());
                                    throwable.printStackTrace();
                                });
        mCompositeDisposable.add(subscribe);
    }

    public void onDeleteSuccess(String walletName) {
        mToast(getString(R.string.delete_succse));
        PreferencesManager.remove(this, Constant.WALLETS, walletName);
        EventBus.getDefault().post(new LoadOtherWalletEvent());
        finish();
    }

    private void fixWalletNameDialog(Context context, @LayoutRes int resource) {
        // set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        EditText walletName = view.findViewById(R.id.edit_wallet_name);
        walletName.setText(textWalletName.getText().toString());
        walletName.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // 禁止EditText输入空格
                        if (s.toString().contains(" ")) {
                            String[] str = s.toString().split(" ");
                            StringBuilder sb = new StringBuilder();
                            for (String value : str) {
                                sb.append(value);
                            }
                            walletName.setText(sb.toString());
                            walletName.setSelection(start);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!TextUtils.isEmpty(s.toString())) {
                            if (s.length() > 14) {
                                mToast(getString(R.string.name_lenth));
                            }
                        }
                    }
                });
        view.findViewById(R.id.btn_import)
                .setOnClickListener(
                        v -> {
                            if (TextUtils.isEmpty(walletName.getText().toString())) {
                                mToast(getString(R.string.please_input_walletname));
                                return;
                            }
                            try {
                                String keyName =
                                        PreferencesManager.get(
                                                        this,
                                                        "Preferences",
                                                        CURRENT_SELECTED_WALLET_NAME,
                                                        "")
                                                .toString();
                                Daemon.commands.callAttr(
                                        "rename_wallet", keyName, walletName.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                mToast(e.getMessage());
                                return;
                            }
                            mToast(getString(R.string.fix_success));
                            PyEnv.loadLocalWalletInfo(MyApplication.getInstance());
                            textWalletName.setText(walletName.getText().toString());
                            EventBus.getDefault()
                                    .post(new FixWalletNameEvent(walletName.getText().toString()));
                            dialogBtoms.dismiss();
                        });
        view.findViewById(R.id.img_cancel)
                .setOnClickListener(
                        v -> {
                            dialogBtoms.dismiss();
                        });
        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        // set pop_up size
        window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // set locate
        window.setGravity(Gravity.BOTTOM);
        // set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.setCanceledOnTouchOutside(true);
        dialogBtoms.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(SecondEvent updataHint) {
        String msgVote = updataHint.getMsg();
        if ("finish".equals(msgVote)) {
            finish();
        }
    }
}
