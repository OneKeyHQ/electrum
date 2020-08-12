package org.haobtc.wallet.activities;

import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.event.FinishEvent;

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

    @OnClick(R.id.img_back)
    public void onViewClicked() {
        EventBus.getDefault().post(new FinishEvent());
       finish();
    }
}
