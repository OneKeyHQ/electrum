package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.BackupFinishEvent;
import org.haobtc.onekey.event.ChangePinEvent;
import org.haobtc.onekey.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/20/20
 */

public class WriteMnemonicOnPaper extends BaseFragment {

    @BindView(R.id.promote)
    TextView promote;
    @BindView(R.id.ready_go)
    Button readyGo;
    private boolean isNormal;

    public WriteMnemonicOnPaper(boolean isNormal) {
        this.isNormal = isNormal;
    }
    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {
        if (!isNormal) {
            promote.setText(R.string.write_mnemonic_24);
        }
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.write_mnemonic_on_paper_fragment;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWriteDone(BackupFinishEvent event) {
        readyGo.setEnabled(true);
    }
    @OnClick(R.id.ready_go)
    public void onViewClicked(View view) {
        EventBus.getDefault().post(new ChangePinEvent("", ""));
    }

    @Override
    public boolean needEvents() {
        return true;
    }
}
