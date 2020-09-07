package org.haobtc.keymanager.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.event.RefreshEvent;


public class BluetoothFragment extends Fragment implements OnRefreshListener {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    private BleDeviceRecyclerViewAdapter bleDeviceRecyclerViewAdapter;

    public BluetoothFragment(BleDeviceRecyclerViewAdapter adapter) {
        this.bleDeviceRecyclerViewAdapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bluetooth_device_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.device_list);
        SmartRefreshLayout refreshLayout = view.findViewById(R.id.smart_RefreshLayout);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bleDeviceRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        EventBus.getDefault().post(new RefreshEvent());
        new Handler().postDelayed(refreshLayout::finishRefresh, 300);
    }
}
