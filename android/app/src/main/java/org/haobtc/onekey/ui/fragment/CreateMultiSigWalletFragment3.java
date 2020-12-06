package org.haobtc.onekey.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.onekey.R;
import org.haobtc.onekey.event.ExitEvent;
import org.haobtc.onekey.onekeys.HomeOneKeyActivity;
import org.haobtc.onekey.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author liyan
 * @date 11/24/20
 */

public class CreateMultiSigWalletFragment3 extends BaseFragment {


    @BindView(R.id.wallet_info_promote)
    TextView walletInfoPromote;
    @BindView(R.id.tet_many_key)
    TextView tetManyKey;
    @BindView(R.id.co_signer_info)
    RecyclerView coSignerInfo;
    @BindView(R.id.btn_finish)
    Button btnFinish;

    /**
     * init views
     *
     * @param view
     */
    @Override
    public void init(View view) {

    }

    /***
     * init layout
     * @return
     */
    @Override
    public int getContentViewId() {
        return R.layout.create_multi_sig_fragment_3;
    }


    @OnClick(R.id.btn_finish)
    public void onViewClicked(View view) {
        startActivity(new Intent(getContext(), HomeOneKeyActivity.class));
        EventBus.getDefault().post(new ExitEvent());
    }
}
