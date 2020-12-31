package org.haobtc.onekey.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lxj.xpopup.core.BottomPopupView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author liyan
 * @date 11/26/20
 */

public class DeleteLocalDeviceDialog extends BottomPopupView {
    Context context;
    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private String deviceId;
    private Unbinder bind;
    private onClick onClick;

    public DeleteLocalDeviceDialog(@NonNull Context context, String deviceId, onClick onClick) {
        super(context);
        this.context = context;
        this.deviceId = deviceId;
        this.onClick = onClick;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        bind = ButterKnife.bind(this);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.delete_device_dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    public interface onClick {
        void onBack();
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
                PreferencesManager.remove(getContext(), Constant.DEVICES, deviceId);
                dismiss();
                onClick.onBack();
                break;
        }
    }
}
