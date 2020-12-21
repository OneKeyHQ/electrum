package org.haobtc.onekey.ui.dialog;

import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.haobtc.onekey.R;
import org.haobtc.onekey.manager.BleManager;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/19/20
 */

public class OpenLocationServiceDialog extends BaseDialogFragment {
    @BindView(R.id.back)
    TextView back;
    @BindView(R.id.go)
    TextView go;
    @BindView(R.id.promote)
    TextView promote;

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.need_location_tip;
    }

    @Override
    public void init() {
        promote.setText(R.string.promote_ble);
    }

    @OnClick({R.id.back, R.id.go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                BleManager manager = BleManager.getInstance(requireActivity());
                manager.getLocationManager().removeUpdates(manager.getLocationListener());
                Toast.makeText(requireActivity(), requireActivity().getString(R.string.dont_use_bluetooth), Toast.LENGTH_SHORT).show();
                dismiss();
                requireActivity().finish();
                break;
            case R.id.go:
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                requireActivity().startActivity(intent);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public boolean requireGravityCenter() {
        return true;
    }
}
