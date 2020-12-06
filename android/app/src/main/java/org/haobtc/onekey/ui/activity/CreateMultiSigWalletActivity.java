package org.haobtc.onekey.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Strings;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.MyApplication;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.asynctask.BusinessAsyncTask;
import org.haobtc.onekey.bean.XpubItem;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.constant.PyConstant;
import org.haobtc.onekey.event.ButtonRequestEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.event.CreateMultiSigWalletEvent;
import org.haobtc.onekey.event.CreateSuccessEvent;
import org.haobtc.onekey.event.DeviceSearchEvent;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.GetXpubEvent;
import org.haobtc.onekey.event.NextFragmentEvent;
import org.haobtc.onekey.manager.PyEnv;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.dialog.ValidateXpubDialog;
import org.haobtc.onekey.ui.fragment.CreateMultiSigWalletFragment;
import org.haobtc.onekey.ui.fragment.CreateMultiSigWalletFragment2;
import org.haobtc.onekey.ui.fragment.CreateMultiSigWalletFragment3;
import org.haobtc.onekey.ui.fragment.DevicePINFragment;
import org.haobtc.onekey.ui.fragment.ReadingXpubFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/24/20
 */

public class CreateMultiSigWalletActivity extends BaseActivity implements BusinessAsyncTask.Helper {

    @BindView(R.id.img_back)
    ImageView imgBack;
    private int coSignerNum;
    private CreateMultiSigWalletFragment2 fragment2;
    private String walletName;
    private int sigNum;
    private String currentMethod;

    /**
     * init
     */
    @Override
    public void init() {
        updateTitle(R.string.creat_ggwallet);
        startFragment(new CreateMultiSigWalletFragment());
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.activity_title_container;
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
        switch (currentMethod) {
            case BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY:
                startFragment(fragment2);
                new ValidateXpubDialog(s).show(getSupportFragmentManager(), "");
                break;
            default:

        }

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void currentMethod(String methodName) {
        currentMethod = methodName;
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onSearchDeviceEvent(DeviceSearchEvent event) {
        Intent intent = new Intent(this, SearchDevicesActivity.class);
        intent.putExtra(Constant.SEARCH_DEVICE_MODE, Constant.SearchDeviceMode.MODE_BIND_ADMIN_PERSON);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetXpub(GetXpubEvent event) {
        getXpubP2wsh();
    }

    /**
     * 获取用于个人钱包的扩展公钥
     */
    private void getXpubP2wsh() {
        new BusinessAsyncTask().setHelper(this).execute(BusinessAsyncTask.GET_EXTEND_PUBLIC_KEY,
                MyApplication.getInstance().getDeviceWay());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNext(NextFragmentEvent event) {
        switch (event.getLayoutId()) {
            case R.layout.create_multi_sig_fragment_2:
                coSignerNum = (int) event.getArgs()[0];
                sigNum = (int) event.getArgs()[1];
                walletName = (String) event.getArgs()[2];
                if (fragment2 == null) {
                    fragment2 = new CreateMultiSigWalletFragment2(coSignerNum);
                }
                startFragment(fragment2);
                break;
            case R.layout.create_multi_sig_fragment_3:
                break;
            default:
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreateMultiSig(CreateMultiSigWalletEvent event) {
        List<XpubItem> xpubList = event.getXpubItems();
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (XpubItem item : xpubList) {
            builder.append("[\"").append(item.getXpub()).append("\", \"").append(FindNormalDeviceActivity.deviceId).append("\"],");
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append("]");
        String name = PyEnv.createWallet(this, walletName, sigNum, coSignerNum, builder.toString());
        if (!Strings.isNullOrEmpty(name)) {
            startFragment(new CreateMultiSigWalletFragment3());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePin(ChangePinEvent event) {
        startFragment(new ReadingXpubFragment());
        // 回写PIN码
        PyEnv.setPin(event.getPinNew());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        if (PyConstant.PIN_CURRENT == event.getType()) {
            startFragment(new DevicePINFragment(PyConstant.PIN_CURRENT));
        }

    }

    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked(View view) {
        finish();
    }

    /**
     * 子fragment返回请求响应
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(ExitEvent exitEvent) {
       finish();
    }
}