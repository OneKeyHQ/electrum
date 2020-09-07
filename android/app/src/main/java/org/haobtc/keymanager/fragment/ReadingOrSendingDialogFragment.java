package org.haobtc.keymanager.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.event.ExitEvent;

import java.util.Objects;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.ble;
import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.nfc;


public class ReadingOrSendingDialogFragment extends DialogFragment {
    private int resId;
    public ReadingOrSendingDialogFragment(@StringRes int res) {
        this.resId = res;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_read_or_sending, container, false);
        view.findViewById(R.id.cancel_reading).setOnClickListener((v) -> {
            dismiss();
            ble.put("IS_CANCEL", true);
            nfc.put("IS_CANCEL", true);
            EventBus.getDefault().post(new ExitEvent());
        });
        TextView textPromote = view.findViewById(R.id.text_promote);
        textPromote.setText(resId);
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.color.transparent);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
        }
        return view;
    }
}
