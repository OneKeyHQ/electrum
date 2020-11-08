package org.haobtc.onekey.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.constant.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.com.heaton.blelibrary.ble.model.BleDevice;


public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.ViewHolder> {

    public static List<BleDevice> mValues = new ArrayList<>();
    private LayoutInflater mInflater;
    private OnItemBleDeviceClick mOnItemBleDeviceClick;
    private static final String PATTERN = "^K\\d{4}";


    public BleDeviceAdapter(Context context, OnItemBleDeviceClick click) {
        this.mOnItemBleDeviceClick = click;
        this.mInflater = LayoutInflater.from(context);
    }


    public void add(BleDevice device) {
        if (device == null) {
            return;
        }
//        String name = device.getBleName();
//        if (TextUtils.isEmpty(name) || !Pattern.matches(PATTERN, name)) {
//            return;
//        }

        mValues.add(device);
        mValues = mValues.stream().distinct().
                filter(bleDevice -> bleDevice.getBleName() != null && (bleDevice.getBleName().toLowerCase()
                        .startsWith(Constant.BLE_NAME_PREFIX.toLowerCase()))).collect(Collectors.toList());
        //notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater
                .inflate(R.layout.bluetooth_device_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.device = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getBleName() == null ?
                mValues.get(position).getBleAddress() : mValues.get(position).getBleName());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @SingleClick(value = 10000)
            @Override
            public void onClick(View v) {
                if (mOnItemBleDeviceClick != null) {
                    mOnItemBleDeviceClick.connectBle(holder.device);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public BleDevice device;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.device_alias);
        }

    }

    public interface OnItemBleDeviceClick {
        void connectBle(BleDevice device);
    }
}
