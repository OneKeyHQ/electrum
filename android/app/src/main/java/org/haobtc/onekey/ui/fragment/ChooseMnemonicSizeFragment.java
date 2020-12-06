package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.InitDeviceEvent;
import org.haobtc.onekey.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择助记词强度页面（12、24）
 * */
/**
 * @author liyan
 * @date 12/1/20
 */
public class ChooseMnemonicSizeFragment extends BaseFragment {

    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;

    @Override
    public int getContentViewId() {
        return R.layout.choose_mnemonic_size_fragment;
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.tv1, R.id.tv2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv1:
                EventBus.getDefault().post(new InitDeviceEvent(true, ""));
                break;
            case R.id.tv2:
                EventBus.getDefault().post(new InitDeviceEvent(false, ""));
                break;
        }
    }

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {

    }
}
