package org.haobtc.onekey.adapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.WalletInfo;
import org.haobtc.onekey.utils.ClipboardUtils;

import java.util.List;

/**
 * @Description: java类作用描述
 * @Author: peter Qin
 */
public class WalletListTypeAdapter extends BaseMultiItemQuickAdapter<WalletInfo, BaseViewHolder> {
    public static final int NoWallet = 0;
    public static final int WalletNorMal = 1;
    public static final int AddWallet = 2;

    public WalletListTypeAdapter (List<WalletInfo> data) {
        super(data);
        addItemType(NoWallet, R.layout.item_no_wallet);
        addItemType(WalletNorMal, R.layout.hd_wallet_item);
        addItemType(AddWallet, R.layout.item_add_wallet);
    }

    @Override
    protected void convert (BaseViewHolder helper, WalletInfo item) {
        switch (item.itemType) {
            case NoWallet:
                helper.addOnClickListener(R.id.recl_add_hd_wallet);
                helper.addOnClickListener(R.id.recl_recovery_wallet);
                break;
            case WalletNorMal:
                helper.setText(R.id.text_name, item.label);
                RelativeLayout view = helper.getView(R.id.rel_background);
                ImageView imgType = helper.getView(R.id.img_type);
                if (item.type.contains("btc")) {
                    view.setBackground(mContext.getDrawable(R.drawable.orange_back));
                    imgType.setImageDrawable(mContext.getDrawable(R.drawable.token_trans_btc_list));
                } else if (item.type.contains("eth")) {
                    view.setBackground(mContext.getDrawable(R.drawable.eth_blue_back));
                    imgType.setImageDrawable(mContext.getDrawable(R.drawable.token_trans_eth_list));
                }
                if ("btc-derived-standard".equals(item.type)) {
                    helper.getView(R.id.text_type).setVisibility(View.VISIBLE);
                    helper.setText(R.id.text_type, "HD");
                } else if (item.type.contains("hw")) {
                    String type = item.type.substring(item.type.indexOf("hw-") + 3);
                    helper.setText(R.id.text_type, mContext.getString(R.string.hardwares));
                } else if (item.type.contains("watch")) {
                    helper.getView(R.id.text_type).setVisibility(View.VISIBLE);
                    helper.setText(R.id.text_type, mContext.getString(R.string.watch));
                } else {
                    helper.getView(R.id.text_type).setVisibility(View.INVISIBLE);
                }
                TextView textAddr = helper.getView(R.id.text_addr);
                String address = item.addr;
                String front6 = address.substring(0, 6);
                String after6 = address.substring(address.length() - 6);
                textAddr.setText(address);
                helper.setText(R.id.text_address, String.format("%s…%s", front6, after6));
                ImageView copyAddr = helper.getView(R.id.img_copy_addr);
                SharedPreferences preferences = mContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                String loadWalletName = preferences.getString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME, "");
                ImageView chooseView = helper.getView(R.id.img_choose);
                if (loadWalletName.equals(item.name)) {
                    chooseView.setVisibility(View.VISIBLE);
                }else {
                    chooseView.setVisibility(View.GONE);
                }
                copyAddr.setOnClickListener(v -> {
                    //copy text
                    ClipboardUtils.copyText(mContext, textAddr.getText().toString());
                });
                helper.addOnClickListener(R.id.rel_background);
                break;
            case AddWallet:
                helper.addOnClickListener(R.id.recl_add_wallet);
                break;
        }
    }

}
