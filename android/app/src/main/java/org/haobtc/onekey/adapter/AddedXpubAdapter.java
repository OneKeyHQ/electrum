package org.haobtc.onekey.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.XpubItem;

import java.util.List;

/**
 * @author liyan
 */
public class AddedXpubAdapter extends BaseQuickAdapter<XpubItem, BaseViewHolder> {
    public AddedXpubAdapter(@Nullable List<XpubItem> data) {
        super(R.layout.public_wallet_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, XpubItem item) {
        helper.setText(R.id.tet_Walletname,item.getName()).setText(R.id.tet_AddBixinkey,item.getXpub());
        helper.addOnClickListener(R.id.img_deleteKey);
    }
}
