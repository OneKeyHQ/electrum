package org.haobtc.onekey.ui.activity;

import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.VerifyFailedEvent;
import org.haobtc.onekey.event.VerifySuccessEvent;
import org.haobtc.onekey.ui.base.BaseActivity;
import org.haobtc.onekey.ui.fragment.VerifyConnFragment;
import org.haobtc.onekey.ui.fragment.VerifyResultFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/27/20
 */

public class VerifyHardwareActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    private String label;

    /**
     * init
     */
    @Override
    public void init() {
        updateTitle(R.string.auth_verify);
        label = getIntent().getStringExtra(Constant.BLE_INFO);
        startFragment(new VerifyConnFragment(label));
    }
    /**
     * 防伪认证成功事件响应
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVerifySuccess(VerifySuccessEvent event) {
        startFragment(new VerifyResultFragment(label, true,null));
    }
    /**
     * 防伪认证失败事件响应
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVerifyFailed(VerifyFailedEvent event) {
        startFragment(new VerifyResultFragment(label, false, event.getFailedReason()));
    }

    /**
     * 子fragment返回请求响应
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinish(ExitEvent exitEvent) {
       finish();
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
    @SingleClick
    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.img_back) {
            finish();
        }
    }
}
