package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.TextView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.ConnectedEvent;
import org.haobtc.onekey.event.GotVerifyInfoEvent;
import org.haobtc.onekey.event.PostVerifyInfoEvent;
import org.haobtc.onekey.event.VerifySuccessEvent;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.custom.LoadingTextView;

import butterknife.BindView;

/**
 * @author liyan
 */
public class VerifyConnFragment extends BaseFragment {


    @BindView(R.id.label)
    TextView label;
    @BindView(R.id.connecting_device)
    LoadingTextView connectingDevice;
    @BindView(R.id.retrieve_verify_info)
    LoadingTextView retrieveVerifyInfo;
    @BindView(R.id.commit_verify_info)
    LoadingTextView commitVerifyInfo;
    private String name;
    public VerifyConnFragment(String name) {
        this.name = name;
    }
    @Override
    public void init(View view) {
       label.setText(name);
    }

    @Override
    public int getContentViewId() {
        return R.layout.vervify_hardware_fragment;
    }

    @Override
    public boolean needEvents() {
        return true;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnected(ConnectedEvent event) {
        connectingDevice.completeLoading();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotVerifyInfo(GotVerifyInfoEvent event) {
        retrieveVerifyInfo.completeLoading();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostVerifyInfo(PostVerifyInfoEvent event) {
        commitVerifyInfo.completeLoading();
        EventBus.getDefault().post(new VerifySuccessEvent());
    }
}
