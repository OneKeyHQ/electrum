package org.haobtc.onekey.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.ArrayList;
import org.haobtc.onekey.R;
import org.haobtc.onekey.business.blockBrowser.BlockBrowser;

public class BlockBrowserChooseAdapter
        extends BaseQuickAdapter<BlockBrowser, BlockBrowserChooseAdapter.myViewHolder> {
    private BlockBrowser mCurrentBlockBrowser;

    public BlockBrowserChooseAdapter(
            Context context,
            ArrayList<BlockBrowser> exchangeList,
            BlockBrowser currentBlockBrowser) {
        super(R.layout.price_quotation_item, exchangeList);
        this.mCurrentBlockBrowser = currentBlockBrowser;
    }

    public class myViewHolder extends BaseViewHolder {
        TextView tetWalletName;
        ImageView imgChoose;

        public myViewHolder(View view) {
            super(view);
            tetWalletName = view.findViewById(R.id.tet_WalletName);
            imgChoose = view.findViewById(R.id.img_choose);
        }
    }

    @Override
    protected void convert(myViewHolder holder, BlockBrowser item) {
        holder.tetWalletName.setText(item.url());
        holder.tetWalletName.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentBlockBrowser = item;
                        onListenerClick.itemClick(
                                holder.getAdapterPosition(), mCurrentBlockBrowser);
                        notifyDataSetChanged();
                    }
                });
        if (item.uniqueTag().equals(mCurrentBlockBrowser.uniqueTag())) {
            holder.imgChoose.setVisibility(View.VISIBLE);
        } else {
            holder.imgChoose.setVisibility(View.GONE);
        }
    }

    public interface OnListenerClick {
        void itemClick(int position, BlockBrowser item);
    }

    private OnListenerClick onListenerClick;

    public void setOnLisennorClick(OnListenerClick onLisennorClick) {
        this.onListenerClick = onLisennorClick;
    }
}
