package org.haobtc.onekey.bean;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.event.SendMoreAddressEvent;

import java.util.List;

public class CheckAddressAdapter extends BaseQuickAdapter<SendMoreAddressEvent, BaseViewHolder> {

    public CheckAddressAdapter(@Nullable List<SendMoreAddressEvent> data) {
        super(R.layout.moreaddress_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SendMoreAddressEvent item) {
        SharedPreferences preferences = mContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String baseUnit = preferences.getString("base_unit", "mBTC");
        helper.setText(R.id.tet_moreaddress, item.getInputAddress()).setText(R.id.tet_payNum, item.getInputAmount() + " " + baseUnit);

    }
}
