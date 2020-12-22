package org.haobtc.onekey.ui.dialog;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/19/20
 */

public class RequestLocationPermissionsDialog extends BaseDialogFragment {
    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.need_location_tip;
    }

    @OnClick({R.id.back, R.id.go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                dismiss();
                requireActivity().finish();
                break;
            case R.id.go:
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    requireActivity().startActivity(intent);
                    requireActivity().finish();
                }
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
