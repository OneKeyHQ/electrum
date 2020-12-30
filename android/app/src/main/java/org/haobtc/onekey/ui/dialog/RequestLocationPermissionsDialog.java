package org.haobtc.onekey.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.lxj.xpopup.core.CenterPopupView;

import org.haobtc.onekey.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/19/20
 */

public class RequestLocationPermissionsDialog extends CenterPopupView {
    private Context context;
    public RequestLocationPermissionsDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.need_location_tip;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                dismiss();
                break;
            case R.id.go:
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                    ((FragmentActivity)context).finish();
                }
                break;
        }
    }
}
