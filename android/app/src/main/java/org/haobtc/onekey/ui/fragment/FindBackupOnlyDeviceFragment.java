package org.haobtc.onekey.ui.fragment;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import org.haobtc.onekey.R;
import org.haobtc.onekey.ui.activity.FindBackupOnlyDeviceActivity;
import org.haobtc.onekey.ui.base.BaseFragment;

/**
 * @author liyan
 * @date 11/27/20
 */
public class FindBackupOnlyDeviceFragment extends BaseFragment {

    @BindView(R.id.recovery_hd_wallet)
    TextView recoveryHdWallet;

    @Override
    public int getContentViewId() {
        return R.layout.activity_find_device_and_backed_up;
    }

    @OnClick({R.id.recovery_hd_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recovery_hd_wallet:
                if (getActivity() instanceof FindBackupOnlyDeviceActivity) {
                    ((FindBackupOnlyDeviceActivity) getActivity()).onClickRecoveryLocalHD();
                }
                break;
        }
    }

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {}
}
