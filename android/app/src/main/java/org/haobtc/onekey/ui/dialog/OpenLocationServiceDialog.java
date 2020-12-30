package org.haobtc.onekey.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import com.lxj.xpopup.core.CenterPopupView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.manager.BleManager;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 12/19/20
 */

public class OpenLocationServiceDialog extends CenterPopupView {
    @BindView(R.id.back)
    TextView back;
    @BindView(R.id.go)
    TextView go;
    @BindView(R.id.promote)
    TextView promote;
    private FragmentActivity context;
    public OpenLocationServiceDialog(@NonNull Context context) {
        super(context);
        this.context = (FragmentActivity) context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.need_location_tip;
    }

    @Override
    public void onCreate() {
        ButterKnife.bind(this);
        promote.setText(R.string.promote_ble);
    }

    @OnClick({R.id.back, R.id.go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                BleManager manager = BleManager.getInstance(context);
                manager.getLocationManager().removeUpdates(manager.getLocationListener());
                Toast.makeText(context, context.getString(R.string.dont_use_bluetooth), Toast.LENGTH_SHORT).show();
                dismiss();
                context.finish();
                break;
            case R.id.go:
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
                break;
        }
    }
    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        dismiss();
    }
}
