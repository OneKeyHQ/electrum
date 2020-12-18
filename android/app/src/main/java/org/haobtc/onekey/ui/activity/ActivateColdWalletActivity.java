package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.PyResponse;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.BackupFinishEvent;
import org.haobtc.onekey.event.ButtonRequestConfirmedEvent;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.InitDeviceEvent;
import org.haobtc.onekey.event.NameSettedEvent;
import org.haobtc.onekey.event.NextFragmentEvent;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.fragment.ConfirmOnHardwareFragment;
import org.haobtc.onekey.ui.fragment.DeviceNameSettingFragment;
import org.haobtc.onekey.ui.fragment.DevicePINFragment;
import org.haobtc.onekey.ui.fragment.NormalActiveSuccessfulFragment;
import org.haobtc.onekey.ui.fragment.PickMnemonicSizeFragment;
import org.haobtc.onekey.ui.fragment.UpdatePinConfirmFragment;
import org.haobtc.onekey.ui.fragment.WriteMnemonicOnPaper;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * activate wallet
 * @author liyan
 */
public class ActivateColdWalletActivity extends BaseActivity implements BusinessAsyncTask.Helper {

    @BindView(R.id.img_back)
    ImageView imgBack;
    private String name;
    private String currentMethodName;
    private int mode;
    @Override
    public void init() {
        mode = getIntent().getIntExtra(Constant.ACTIVE_MODE, 0);
        updateTitle(R.string.activate_cold_wallet);
        startFragment(new DeviceNameSettingFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }
    /**
     * 设备label设置完成事件响应
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNameSetted(NameSettedEvent event) {
        name = event.getName();
        switch (mode) {
            case Constant.ACTIVE_MODE_NEW:
                startFragment(new PickMnemonicSizeFragment());
                break;
            case Constant.ACTIVE_MODE_IMPORT:
                activeByImportMnemonic();
//                startFragment(new ChooseMnemonicSizeFragment());
                break;
            case Constant.ACTIVE_MODE_LOCAL_BACKUP:
                showToast("暂不支持此方式");
        }
    }
    /**
     * 激活设备事件响应
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInitDevice(InitDeviceEvent event) {
        if (Strings.isNullOrEmpty(event.getMnemonics())) {
            boolean isNormal = event.getIsNormal();
            activeByNormal(isNormal);
        }
//        else {
//            activeByImportMnemonic();
//        }
    }
    /**
     * 通过助记词恢复硬件
     * */
    private void activeByRecovery(boolean isNormal) {
        String language = PreferencesManager.get(this, "Preferences", Constant.LANGUAGE, "").toString();
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.RECOVER,
                MyApplication.getInstance().getDeviceWay(),
                name,
                language,
                isNormal ? null: "1");
    }
    /**
     * 导入助记词作为备份(没有任何功能)
     * */
    private void activeByImportMnemonic() {
        String language = PreferencesManager.get(this, "Preferences", Constant.LANGUAGE, "").toString();
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.IMPORT_MNEMONIC,
                MyApplication.getInstance().getDeviceWay(),
                getIntent().getStringExtra(Constant.MNEMONICS),
                language,
                name);
    }
    /**
     * 作为新设备激活
     * */
    private void activeByNormal(boolean isNormal) {
        startFragment(new WriteMnemonicOnPaper(isNormal));
        String language = PreferencesManager.get(this, "Preferences", "language", "").toString();
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.INIT_DEVICE,
                MyApplication.getInstance().getDeviceWay(),
                name,
                language,
                isNormal ? null: "1");
    }
    /**
     * 修改PIN码及回写PIN码事件的响应
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePin(ChangePinEvent event) {
        if (Strings.isNullOrEmpty(event.toString())) {
           changePin();
        } else {
            // 回写PIN码
            PyEnv.setPin(event.toString());
        }

    }
    /**
     * 修改PIN码
     * */
    private void changePin() {
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.CHANGE_PIN,
                MyApplication.getInstance().getDeviceWay()
        );
    }
    /**
     * 硬件按键确认响应
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        if (PyConstant.PIN_NEW_FIRST == event.getType()) {
            startFragment(new DevicePINFragment(PyConstant.PIN_NEW_FIRST));
        } else if (PyConstant.BUTTON_REQUEST_7 == event.getType()) {
            switch (currentMethodName) {
                case BusinessAsyncTask.IMPORT_MNEMONIC:
                    startFragment(new ConfirmOnHardwareFragment());
                    break;
                default:
                    startFragment(new UpdatePinConfirmFragment());
            }
        }
    }
   /**
    * 子fragment返回请求响应
    * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(ExitEvent exitEvent) {
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNext(NextFragmentEvent event) {
        switch (event.getLayoutId()) {
            case R.layout.active_successful_fragment:
                switch (mode) {
                    case Constant.ACTIVE_MODE_IMPORT:
                        updateTitle(R.string.backups_wallet);
                        String cname = PreferencesManager.get(this, "Preferences", Constant.CURRENT_SELECTED_WALLET_NAME, "").toString();
                        if (!Strings.isNullOrEmpty(cname)) {
                            PyResponse<Void> response = PyEnv.clearHdBackupFlags(cname);
                            String errors = response.getErrors();
                            if (!Strings.isNullOrEmpty(errors)) {
                                showToast(errors);
                            }
                        }
                        startFragment(new NormalActiveSuccessfulFragment(name, R.string.import_success_description, R.string.success_1));
                        break;
                    default:
                        startFragment(new NormalActiveSuccessfulFragment(name, 0, 0));
                }
                break;
            default:

        }
    }
    @Override
    public boolean needEvents() {
        return true;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onException(Exception e) {
        showToast(e.getMessage());
        finish();
    }

    @Override
    public void onResult(String s) {

        switch (currentMethodName) {
            case BusinessAsyncTask.INIT_DEVICE:
                EventBus.getDefault().post(new BackupFinishEvent(s));
                break;
            case BusinessAsyncTask.CHANGE_PIN:
            case BusinessAsyncTask.IMPORT_MNEMONIC:
               EventBus.getDefault().post(new ButtonRequestConfirmedEvent());
            default:
        }

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void currentMethod(String methodName) {
        currentMethodName = methodName;
    }
}
