package org.haobtc.onekey.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.GotPassEvent;
import org.haobtc.onekey.event.RecoveryLocalHDEvent;
import org.haobtc.onekey.event.SelectedEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.fragment.DevicePINFragment;
import org.haobtc.onekey.ui.fragment.FindBackupOnlyDeviceFragment;
import org.haobtc.onekey.ui.fragment.RecoveryWalletFromHdFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 *
 *BackupOnlyDevice: is a hardware which is inited only as an backup
 */
public class FindBackupOnlyDeviceActivity extends BaseActivity implements BusinessAsyncTask.Helper{

    @BindView(R.id.img_back)
    ImageView imgBack;
    private RecoveryWalletFromHdFragment fromHdFragment;
    private String mnemonics;
    @Override
    public void init() {
        updateTitle(R.string.pair);
        startFragment(new FindBackupOnlyDeviceFragment());
        fromHdFragment = new RecoveryWalletFromHdFragment();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
    }

    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        PyEnv.cancelPinInput();
        finish();
    }

    private void readMnemonicFromHardWare() {
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.READ_MNEMONIC_FROM_HARDWARE,
                MyApplication.getInstance().getDeviceWay());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecoveryLocalHd(RecoveryLocalHDEvent event) {
       updateTitle(R.string.recovery_wallet);
       readMnemonicFromHardWare();
       startFragment(fromHdFragment);
    }
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onGotPassEvent(GotPassEvent event) {
        PyEnv.createLocalHd(event.getPassword(), mnemonics);
    }
    @Subscribe
    public void onFinish(ExitEvent exitEvent) {
        if (hasWindowFocus()) {
            finish();
        }
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
        if (!hasWindowFocus()) {
            EventBus.getDefault().post(new ExitEvent());
        }
        mnemonics = s;
        startActivity(new Intent(this, SoftPassActivity.class));
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void currentMethod(String methodName) {

    }
    /**
     * 硬件按键确认响应
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        if (PyConstant.PIN_CURRENT == event.getType()) {
            startFragment(new DevicePINFragment(PyConstant.PIN_CURRENT));
        }
    }
    /**
     * 回写PIN码事件的响应
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePin(ChangePinEvent event) {
        startFragment(fromHdFragment);
        // 回写PIN码
        PyEnv.setPin(event.toString());
    }

    /**
     * 恢复指定钱包
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecovery(SelectedEvent event) {
        boolean success = PyEnv.recoveryConfirm(event.getNameList(), false);
        if (!success) {
            showToast(R.string.recovery_failed);
        } else {
            EventBus.getDefault().post(new CreateSuccessEvent(event.getNameList().get(0)));
            startActivity(new Intent(this, HomeOneKeyActivity.class));
        }
        finish();
    }
    @Override
    public boolean needEvents() {
        return true;
    }
}
