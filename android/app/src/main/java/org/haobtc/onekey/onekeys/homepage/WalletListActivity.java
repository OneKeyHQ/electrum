package org.haobtc.onekey.onekeys.homepage;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.CreateWalletActivity;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.activities.personalwallet.CreatAppWalletActivity;
import org.haobtc.onekey.activities.personalwallet.mnemonic_word.MnemonicWordActivity;
import org.haobtc.onekey.adapter.WalletListAdapter;
import org.haobtc.onekey.utils.Daemon;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.onekey.activities.service.CommunicationModeSelector.executorService;

public class WalletListActivity extends BaseActivity {

    @BindView(R.id.sj_radiogroup)
    RadioGroup sjRadiogroup;
    @BindView(R.id.recl_wallet_detail)
    RelativeLayout reclWalletDetail;
    @BindView(R.id.img_add_wallet)
    ImageView imgAddWallet;
    @BindView(R.id.recl_wallet_list)
    RecyclerView reclWalletList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        getWalletListData();
    }

    private void getWalletListData() {
        ArrayList<String> listData = new ArrayList<>();
        listData.add("BTC");
        listData.add("EOS");
        listData.add("ETH");
        WalletListAdapter walletListAdapter = new WalletListAdapter(listData);
        reclWalletList.setAdapter(walletListAdapter);

    }

    @OnClick({R.id.img_close, R.id.img_add_wallet, R.id.recl_wallet, R.id.lin_pair_wallet, R.id.lin_add_wallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.img_add_wallet:
                break;
            case R.id.recl_wallet:
                break;
            case R.id.lin_pair_wallet:
                break;
            case R.id.lin_add_wallet:
                createWalletChooseDialog(WalletListActivity.this, R.layout.add_wallet);
                break;
        }
    }

    private void getHomeWalletList() {
        executorService.execute(new Runnable() {
            private PyObject getWalletsListInfo;

            @Override
            public void run() {
                //wallet list
                try {
                    getWalletsListInfo = Daemon.commands.callAttr("list_wallets");

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (getWalletsListInfo != null && getWalletsListInfo.size() != 0) {
                    String toStrings = getWalletsListInfo.toString();
                    Log.i("mWheelplanting", "toStrings: " + toStrings);

                }

            }
        });
    }

    private void createWalletChooseDialog(Context context, @LayoutRes int resource) {
        //set see view
        View view = View.inflate(context, resource, null);
        Dialog dialogBtoms = new Dialog(context, R.style.dialog);
        view.findViewById(R.id.img_cancel).setOnClickListener(v -> {
            dialogBtoms.dismiss();
        });

        dialogBtoms.setContentView(view);
        Window window = dialogBtoms.getWindow();
        //set pop_up size
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //set locate
        window.setGravity(Gravity.BOTTOM);
        //set animal
        window.setWindowAnimations(R.style.AnimBottom);
        dialogBtoms.setCanceledOnTouchOutside(true);
        dialogBtoms.show();

    }
}