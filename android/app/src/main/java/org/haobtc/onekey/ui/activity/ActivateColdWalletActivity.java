package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.data.prefs.PreferencesManager;
import org.haobtc.onekey.event.BackupFinishEvent;
import org.haobtc.onekey.event.ButtonRequestConfirmedEvent;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.InitDeviceEvent;
import org.haobtc.onekey.event.NameSettedEvent;
import org.haobtc.onekey.event.NextFragmentEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.mvp.base.BaseActivity;
import org.haobtc.onekey.ui.fragment.AddAssetFragment;
import org.haobtc.onekey.ui.fragment.ConfirmOnHardwareFragment;
import org.haobtc.onekey.ui.fragment.DeviceNameSettingFragment;
import org.haobtc.onekey.ui.fragment.DevicePINFragment;
import org.haobtc.onekey.ui.fragment.ImportMnemonicToDeviceFragment;
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

    @Override
    public void init() {
        updateTitle(R.string.active_hardware);
        startFragment(new DeviceNameSettingFragment());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }


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
        int mode = getIntent().getIntExtra(Constant.ACTIVE_MODE, 0);
        switch (mode) {
            case Constant.ACTIVE_MODE_NEW:
                startFragment(new PickMnemonicSizeFragment());
                break;
            case Constant.ACTIVE_MODE_IMPORT:
                startFragment(new ImportMnemonicToDeviceFragment());
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
        } else {
            activeByImportMnemonic(event.getMnemonics());
        }
    }
    /**
     * 导入助记词作为备份(没有任何功能)
     * */
    private void activeByImportMnemonic(String mnemonics) {
        String language = PreferencesManager.get(this, "Preferences", "language", "").toString();
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.IMPORT_MNEMONIC,
                MyApplication.getInstance().getDeviceWay(),
                mnemonics,
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
            PyEnv.setPin(event.getPinNew());
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
        int mode = getIntent().getIntExtra(Constant.ACTIVE_MODE, 0);
        switch (mode) {
            case Constant.ACTIVE_MODE_NEW:
                finish();
                break;
            case Constant.ACTIVE_MODE_IMPORT:
                updateTitle(R.string.add_asset);
                startFragment(new AddAssetFragment());
                break;
            case Constant.ACTIVE_MODE_LOCAL_BACKUP:
                showToast("暂不支持此方式");
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNext(NextFragmentEvent event) {
        switch (event.getLayoutId()) {
            case R.layout.active_successful_fragment:
                startFragment(new NormalActiveSuccessfulFragment(name));
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
