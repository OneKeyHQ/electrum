package org.haobtc.keymanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.fragment.app.Fragment;

import org.haobtc.keymanager.R;


public class BluetoothConnectingFragment extends Fragment {

    private  View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bluetooth_device_connecting_or_bonding, container, false);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_progress_anim);
        view.findViewById(R.id.connecting).startAnimation(animation);
        return view;
    }

    @Override
    public View getView() {
        return this.view;
    }

}
