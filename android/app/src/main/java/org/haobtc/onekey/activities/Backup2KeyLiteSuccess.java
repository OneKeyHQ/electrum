package org.haobtc.onekey.activities;

import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.FinishEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 2020/7/21
 */
//
public class Backup2KeyLiteSuccess extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;

    @Override
    public int getLayoutId() {
        return R.layout.backup2lite_success;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

    }

    @Override
    public void initData() {

    }
    @SingleClick
    @OnClick(R.id.img_back)
    public void onViewClicked() {
        EventBus.getDefault().post(new FinishEvent());
       finish();
    }
}
