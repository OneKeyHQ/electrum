package org.haobtc.onekey.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.utils.ClipboardUtils;

import java.util.List;

import static org.haobtc.onekey.constant.Vm.WalletType.HARDWARE;
import static org.haobtc.onekey.constant.Vm.WalletType.IMPORT_PRIVATE;
import static org.haobtc.onekey.constant.Vm.WalletType.IMPORT_WATCH;
import static org.haobtc.onekey.constant.Vm.WalletType.MAIN;
import static org.haobtc.onekey.constant.Vm.WalletType.STANDARD;

public class WalletListAdapter extends BaseQuickAdapter<LocalWalletInfo, BaseViewHolder> {
    public WalletListAdapter(@Nullable List<LocalWalletInfo> data) {
        super(R.layout.hd_wallet_item, data);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void convert(BaseViewHolder helper, LocalWalletInfo item) {
        helper.setText(R.id.text_name, item.getLabel());
        RelativeLayout view = helper.getView(R.id.rel_background);
        ImageView imgType = helper.getView(R.id.img_type);
        switch (item.getCoinType()) {
            case ETH:
                view.setBackground(mContext.getDrawable(R.drawable.eth_blue_back));
                imgType.setImageDrawable(mContext.getDrawable(R.drawable.token_trans_eth_list));
                break;
            default:
            case BTC:
                view.setBackground(mContext.getDrawable(R.drawable.orange_back));
                imgType.setImageDrawable(mContext.getDrawable(R.drawable.token_trans_btc_list));
                break;
        }

        switch (item.getWalletType()) {
            case MAIN:
                helper.getView(R.id.text_type).setVisibility(View.VISIBLE);
                helper.setText(R.id.text_type, "HD");
                break;
            case HARDWARE:
                String type = item.getType().substring(item.getType().indexOf("hw-") + 3);
                helper.setText(R.id.text_type, mContext.getString(R.string.hardwares));
                break;
            case IMPORT_WATCH:
                helper.getView(R.id.text_type).setVisibility(View.VISIBLE);
                helper.setText(R.id.text_type, mContext.getString(R.string.watch));
                break;
            case IMPORT_PRIVATE:
            case STANDARD:
                helper.getView(R.id.text_type).setVisibility(View.INVISIBLE);
                break;
        }

        TextView textAddr = helper.getView(R.id.text_addr);
        String address = item.getAddr();
        String front6 = address.substring(0, 6);
        String after6 = address.substring(address.length() - 6);
        textAddr.setText(address);
        helper.setText(R.id.text_address, String.format("%s…%s", front6, after6));
        ImageView copyAddr = helper.getView(R.id.img_copy_addr);
        SharedPreferences preferences = mContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String loadWalletName = preferences.getString(org.haobtc.onekey.constant.Constant.CURRENT_SELECTED_WALLET_NAME, "");
        ImageView chooseView = helper.getView(R.id.img_choose);
        if (loadWalletName.equals(item.getName())) {
            chooseView.setVisibility(View.VISIBLE);
        }
        copyAddr.setOnClickListener(v -> {
            //copy text
            ClipboardUtils.copyText(mContext, textAddr.getText().toString());
        });
    }
}
