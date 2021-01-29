package org.haobtc.onekey.ui.adapter;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;
import org.haobtc.onekey.R;
import org.haobtc.onekey.bean.CreateWalletBean;
import org.haobtc.onekey.onekeys.walletprocess.OnWalletCheckListener;

/** @Description: 恢复钱包 DerivedInfo Adapter @Author: peter Qin */
public class RecoveryWalletDerivedInfoAdapter
        extends BaseQuickAdapter<CreateWalletBean.DerivedInfoBean, BaseViewHolder> {

    private OnWalletCheckListener mOnWalletCheckListener;

    public RecoveryWalletDerivedInfoAdapter(
            int layoutResId,
            @Nullable List<CreateWalletBean.DerivedInfoBean> data,
            OnWalletCheckListener mOnWalletCheckListener) {
        super(layoutResId, data);
        this.mOnWalletCheckListener = mOnWalletCheckListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, CreateWalletBean.DerivedInfoBean item) {
        CheckBox checkBox = helper.getView(R.id.check_wallet);
        ImageView coinImg = helper.getView(R.id.token_btc);
        helper.setText(R.id.text_wallet_name, item.getLabel());
        if (item.getCoin().contains("btc")) {

            coinImg.setBackgroundResource(R.drawable.token_btc);
        } else {
            coinImg.setBackgroundResource(R.drawable.token_eth);
        }
        if (item.getBlance().contains("(")) {
            helper.setText(
                    R.id.text_wallet_balance,
                    item.getBlance().substring(0, item.getBlance().indexOf("(")));
        }

        if (item.getExist().equals("0")) {
            helper.setGone(R.id.exist_tv, false);
        } else {
            helper.setGone(R.id.exist_tv, true);
            checkBox.setBackgroundResource(R.mipmap.check_not_selected);
        }
        checkBox.setOnClickListener(
                v -> {
                    if (item.getExist().equals("1")) {
                        Toast.makeText(mContext, R.string.not_cancel, Toast.LENGTH_SHORT).show();
                    }
                });
        checkBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if ((item.getExist().equals("0"))) {
                        mOnWalletCheckListener.onCheck(item.getName(), isChecked);
                    }
                });
    }
}
