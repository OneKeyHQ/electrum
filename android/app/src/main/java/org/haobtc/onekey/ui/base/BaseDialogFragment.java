package org.haobtc.onekey.ui.base;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.haobtc.onekey.R;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author liyan
 * @date 11/21/20
 */

public abstract class BaseDialogFragment extends DialogFragment {

    protected Unbinder unbinder;
    private int width;
    /***
     * init layout
     * @return
     */
    public abstract int getContentViewId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.color.transparent);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams wlp = window.getAttributes();
            if (requireGravityCenter()) {
                wlp.gravity = Gravity.CENTER_VERTICAL;
            } else {
                wlp.gravity = Gravity.BOTTOM;
            }
            if (width == 0) {
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            } else {
                wlp.width = width;
            }
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
            window.setWindowAnimations(R.style.AnimBottom);
        }
        return view;
    }

    public void setWidth (int width) {
        this.width = width;
    }

    public void init () {
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
        unbinder.unbind();
    }

    public boolean requireGravityCenter () {
        return false;
    }
}
