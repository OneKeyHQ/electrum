package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.event.InitDeviceEvent;
import org.haobtc.onekey.event.MnemonicSizeSelectedEvent;
import org.haobtc.onekey.mvp.base.BaseFragment;
import org.haobtc.onekey.ui.dialog.ChooseMnemonicSizeDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/19/20
 */
//

public class PickMnemonicSizeFragment extends BaseFragment {

    @BindView(R.id.ready_go)
    Button readyGo;
    @BindView(R.id.choose)
    TextView chooseText;
    @BindView(R.id.promote)
    TextView promoteText;
    private boolean isNormal = true;

    @Override
    public void init(View view) {
        EventBus.getDefault().register(this);
    }

    public PickMnemonicSizeFragment() {
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_choose_mnemonic_size;
    }


    @SingleClick
    @OnClick({R.id.ready_go, R.id.choose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ready_go:
                EventBus.getDefault().post(new InitDeviceEvent(isNormal, ""));
                break;
            case R.id.choose:
                showDialog();
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMnemonicSizeSelected(MnemonicSizeSelectedEvent event) {
        isNormal = event.getNormal();
        if (isNormal) {
            chooseText.setText("创建 12 位助记词钱包↑");
            promoteText.setText("硬件钱包将为你生成 12 位助记词");
        } else {
            chooseText.setText("创建 24 位助记词钱包↑");
            promoteText.setText("硬件钱包将为你生成 24 位助记词");
        }

    }
    private void showDialog() {
        ChooseMnemonicSizeDialog dialog = new ChooseMnemonicSizeDialog();
        dialog.show(getChildFragmentManager(), "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
