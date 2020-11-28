package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.XpubItem;
import org.haobtc.onekey.event.AddBixinKeyEvent;

import java.util.List;

public class PublicPersonAdapter extends BaseQuickAdapter<XpubItem, BaseViewHolder> {
    public PublicPersonAdapter(@Nullable List<XpubItem> data) {
        super(R.layout.public_key_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, XpubItem item) {
        helper.setText(R.id.tet_publicperson_item_id, item.getName());
        int pos = helper.getLayoutPosition() + 1;
        helper.setText(R.id.text_pub_num, mContext.getString(R.string.publicer) + "-" + String.valueOf(pos));
    }
}
