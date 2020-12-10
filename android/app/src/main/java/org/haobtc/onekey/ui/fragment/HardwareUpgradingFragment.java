package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.UpdateSuccessEvent;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.event.UpdatingEvent;
import org.haobtc.onekey.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/3/20
 */

public class HardwareUpgradingFragment extends BaseFragment {
    @BindView(R.id.promote)
    TextView promote;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.progress_promote)
    TextView progressPromote;
    @BindView(R.id.complete)
    Button complete;

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {
        promote.setText(getArguments().getInt(Constant.TAG_HARDWARE_TYPE_PROMOTE_ID));
        progressBar.setIndeterminate(true);
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.hardware_upgrading_fragment;
    }

    @OnClick(R.id.complete)
    public void onViewClicked(View view) {
       EventBus.getDefault().post(new ExitEvent());
    }
    public ProgressBar getProgressBar() {
        return progressBar;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatedSuccess(UpdateSuccessEvent event) {
        progressPromote.setText(R.string.updated);
        complete.setEnabled(true);
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUpdating(UpdatingEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        progressPromote.setText(R.string.updating);
    }

    @Override
    public boolean needEvents() {
        return true;
    }
}
