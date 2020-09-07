package org.haobtc.keymanager.adapter;

import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.event.SendMoreAddressEvent;

import java.util.List;

public class HardwareAdapter extends BaseQuickAdapter<SendMoreAddressEvent, BaseViewHolder> {
    public HardwareAdapter(@Nullable List<SendMoreAddressEvent> data) {
        super(R.layout.hardware_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SendMoreAddressEvent item) {
        helper.setText(R.id.tet_getAddress,item.getInputAddress()).setText(R.id.tet_getNum,item.getInputAmount());
        if (item.isIs_change()){
            helper.getView(R.id.test_change_address).setVisibility(View.VISIBLE);
        }else{
            helper.getView(R.id.test_change_address).setVisibility(View.GONE);
        }

    }
}
