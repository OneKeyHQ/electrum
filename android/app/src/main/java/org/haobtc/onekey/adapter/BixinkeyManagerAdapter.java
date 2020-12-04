package org.haobtc.onekey.adapter;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.HardwareFeatures;

import java.util.List;

public class BixinkeyManagerAdapter extends BaseQuickAdapter<HardwareFeatures, BaseViewHolder> {
    public BixinkeyManagerAdapter( @Nullable List<HardwareFeatures> data) {
        super(R.layout.key_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HardwareFeatures item) {
        if (!TextUtils.isEmpty(item.getLabel())) {
            helper.setText(R.id.tet_keyName,item.getLabel());
        } else {
            helper.setText(R.id.tet_keyName,"BiXinKEY");
        }

        helper.addOnClickListener(R.id.relativeLayout_bixinkey);
//        helper.addOnClickListener(R.id.linear_delete);
    }
}
