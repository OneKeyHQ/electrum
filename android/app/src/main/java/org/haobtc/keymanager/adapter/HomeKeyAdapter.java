package org.haobtc.keymanager.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.bean.HardwareFeatures;

import java.util.List;

public class HomeKeyAdapter extends BaseQuickAdapter<HardwareFeatures, BaseViewHolder> {
    public HomeKeyAdapter(@Nullable List<HardwareFeatures> data) {
        super(R.layout.home_key_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HardwareFeatures item) {
        helper.setText(R.id.test_keyName,item.getLabel()).setText(R.id.test_blueName,item.getBleName());
    }
}
