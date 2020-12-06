package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.StringRes;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.ui.base.BaseFragment;

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
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.tips)
    TextView tips;
    private String name;
    private int descriptionId;
    private int tipsId;

    public NormalActiveSuccessfulFragment(String name, @StringRes int descriptionId, @StringRes int tipsId) {
        this.name = name;
        this.descriptionId = descriptionId;
        this.tipsId = tipsId;
    }

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {
        nameTextView.setText(name);
        if (descriptionId != 0) {
            description.setText(descriptionId);
        }
        if (tipsId != 0) {
            tips.setText(tipsId);
        }
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
    public void onViewClicked(View view) {
        EventBus.getDefault().post(new ExitEvent());
    }
}
