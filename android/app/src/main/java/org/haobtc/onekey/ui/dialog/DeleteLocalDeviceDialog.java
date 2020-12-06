package org.haobtc.onekey.ui.dialog;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/26/20
 */

public class DeleteLocalDeviceDialog extends BaseDialogFragment {
    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private String deviceId;
    private AppCompatActivity activity;

    public DeleteLocalDeviceDialog(AppCompatActivity activity, String deviceId) {
        this.activity = activity;
        this.deviceId = deviceId;
    }
    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.delete_device_dialog;
    }
    @SingleClick
    @OnClick({R.id.img_cancel, R.id.btn_cancel, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_cancel:
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                PreferencesManager.remove(activity, Constant.DEVICES, deviceId);
                dismiss();
                activity.finish();
                activity = null;
                break;
        }
    }
}
