package org.haobtc.wallet.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.SettingActivity;
import org.haobtc.wallet.activities.service.CommunicationModeSelector;

import java.util.Objects;

/**
 * @author liyan
 */
public class NeedNewVersion extends DialogFragment {
    private Activity activity;
    private int errorsId;
    private int title;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public NeedNewVersion(int errors, @StringRes int title) {
        this.errorsId = errors;
        this.title = title;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_exception, container, false);
        view.findViewById(R.id.cancel_sign_fail).setOnClickListener((v) -> {
            dismiss();
            activity.finish();
        });
        Button button = view.findViewById(R.id.retry);
        button.setText(R.string.goto_upgrade);
        button.setOnClickListener((v) -> {
            activity.startActivity(new Intent(activity, SettingActivity.class));
            dismiss();
        });
        TextView errorMessage = view.findViewById(R.id.error_message);
        errorMessage.setText(title);
        TextView errorPromote = view.findViewById(R.id.error_promote);
        if (errorsId == 0) {
            errorPromote.setText("");
        } else {
            errorPromote.setText(errorsId);
        }
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
