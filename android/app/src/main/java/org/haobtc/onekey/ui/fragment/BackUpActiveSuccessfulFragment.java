package org.haobtc.onekey.ui.fragment;

import android.os.Bundle;
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

public class BackUpActiveSuccessfulFragment extends BaseFragment {
    private static final String EXT_NAME = "ext_name";
    private static final String EXT_DESCRIPTION_ID = "ext_descriptionId";
    private static final String EXT_TIPS_ID = "ext_tipsId";

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

    public static BackUpActiveSuccessfulFragment getInstance(String name, @StringRes int descriptionId, @StringRes int tipsId) {
        BackUpActiveSuccessfulFragment fragment = new BackUpActiveSuccessfulFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXT_NAME, name);
        bundle.putInt(EXT_DESCRIPTION_ID, descriptionId);
        bundle.putInt(EXT_TIPS_ID, tipsId);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {
        name = getArguments().getString(EXT_NAME);
        descriptionId = getArguments().getInt(EXT_DESCRIPTION_ID);
        tipsId = getArguments().getInt(EXT_TIPS_ID);

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
        return R.layout.fragment_backup_active_successful_fragment;
    }

    @OnClick({R.id.back_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_wallet:
                EventBus.getDefault().post(new ExitEvent());
                break;
        }
    }
}
