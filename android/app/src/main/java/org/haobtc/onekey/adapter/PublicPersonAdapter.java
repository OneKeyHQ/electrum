package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.event.AddBixinKeyEvent;

import java.util.List;

public class PublicPersonAdapter extends BaseQuickAdapter<AddBixinKeyEvent, BaseViewHolder> {
    public PublicPersonAdapter(@Nullable List<AddBixinKeyEvent> data) {
        super(R.layout.public_key_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddBixinKeyEvent item) {
        helper.setText(R.id.tet_publicperson_item_id, item.getKeyname());
        int pos = helper.getLayoutPosition() + 1;
        helper.setText(R.id.text_pub_num, mContext.getString(R.string.publicer) + "-" + String.valueOf(pos));
    }
}
