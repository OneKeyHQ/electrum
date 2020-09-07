package org.haobtc.keymanager.activities.settings;

import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.event.FinishEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * @author liyan
 */
@Deprecated
public class Backup2LocalSuccess extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;

    @Override
    public int getLayoutId() {
        return R.layout.backup2local_success;
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
