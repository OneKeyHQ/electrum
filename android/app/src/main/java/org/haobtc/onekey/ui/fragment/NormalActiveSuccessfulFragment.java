package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.mvp.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/23/20
 */

public class NormalActiveSuccessfulFragment extends BaseFragment {
    @BindView(R.id.back_wallet)
    Button backWallet;
    @BindView(R.id.name)
    TextView nameTextView;
    private String name;
    public NormalActiveSuccessfulFragment(String name) {
        this.name = name;
    }
    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {
        nameTextView.setText(name);
    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.active_successful_fragment;
    }

    @OnClick(R.id.back_wallet)
    public void onViewClicked() {
        EventBus.getDefault().post(new ExitEvent());

    }
}
