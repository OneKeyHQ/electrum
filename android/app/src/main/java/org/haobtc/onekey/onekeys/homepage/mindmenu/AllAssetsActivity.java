package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;
import com.google.gson.Gson;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.HdWalletAssetAdapter;
import org.haobtc.onekey.bean.HdWalletAllAssetBean;
import org.haobtc.onekey.utils.Daemon;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AllAssetsActivity extends BaseActivity {

    @BindView(R.id.test_all_assets)
    TextView testAllAssets;
    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.recl_assets)
    RecyclerView reclAssets;
    @BindView(R.id.tet_None)
    TextView tetNone;

    @Override
    public int getLayoutId() {
        return R.layout.activity_all_assets;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        reclAssets.setNestedScrollingEnabled(false);
        //get all wallet
        getAllWalletList();
    }

    private void getAllWalletList() {
        //wallet list
        try {
            PyObject allWalletBalance = Daemon.commands.callAttr("get_all_wallet_balance");
            if (allWalletBalance.toString().length() > 2) {
                tetNone.setVisibility(View.GONE);
                Log.i("mWheelplanting", "toStrings: " + allWalletBalance.toString());
                Gson gson = new Gson();
                HdWalletAllAssetBean hdWalletAllAssetBean = gson.fromJson(allWalletBalance.toString(), HdWalletAllAssetBean.class);
                String allBalance = hdWalletAllAssetBean.getAllBalance();
                String fiat = allBalance.substring(0, allBalance.indexOf(" "));
                if (allBalance.contains("CNY")) {
                    testAllAssets.setText(String.format("￥ %s", fiat));
                } else if (allBalance.contains("USD")) {
                    testAllAssets.setText(String.format("$ %s", fiat));
                } else {
                    testAllAssets.setText(allBalance);
                }
                List<HdWalletAllAssetBean.WalletInfoBean> walletInfo = hdWalletAllAssetBean.getWalletInfo();
                HdWalletAssetAdapter hdWalletAssetAdapter = new HdWalletAssetAdapter(walletInfo);
                reclAssets.setAdapter(hdWalletAssetAdapter);
            } else {
                tetNone.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            tetNone.setVisibility(View.VISIBLE);
            e.printStackTrace();
            return;
        }
    }

    @OnClick({R.id.img_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }
}