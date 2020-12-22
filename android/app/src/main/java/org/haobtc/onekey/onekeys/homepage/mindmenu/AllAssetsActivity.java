package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AllAssetsActivity extends BaseActivity implements TextWatcher {

    @BindView(R.id.test_all_assets)
    TextView testAllAssets;
    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.recl_assets)
    RecyclerView reclAssets;
    @BindView(R.id.tet_None)
    TextView tetNone;
    private List<HdWalletAllAssetBean.WalletInfoBean> walletInfo;
    private ArrayList<HdWalletAllAssetBean.WalletInfoBean> searchList;


    @Override
    public int getLayoutId() {
        return R.layout.activity_all_assets;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        editSearch.addTextChangedListener(this);
    }

    @Override
    public void initData() {
        searchList = new ArrayList<>();
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
                float f = Float.parseFloat(fiat);
                DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                String money = decimalFormat.format(f);
                if (allBalance.contains("CNY")) {
                    testAllAssets.setText(String.format("￥ %s", money));
                } else if (allBalance.contains("USD")) {
                    testAllAssets.setText(String.format("$ %s", money));
                } else {
                    testAllAssets.setText(allBalance);
                }
                walletInfo = hdWalletAllAssetBean.getWalletInfo();
                HdWalletAssetAdapter hdWalletAssetAdapter = new HdWalletAssetAdapter(walletInfo);
                reclAssets.setAdapter(hdWalletAssetAdapter);
            } else {
                tetNone.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            tetNone.setVisibility(View.VISIBLE);
            mToast(e.getMessage());
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        searchList.clear();
        if (!TextUtils.isEmpty(s)) {
            for (int i = 0; i < walletInfo.size(); i++) {
                if (walletInfo.get(i).getName().startsWith(s.toString())) {
                    searchList.add(walletInfo.get(i));
                }
            }
            HdWalletAssetAdapter hdWalletAssetAdapter = new HdWalletAssetAdapter(searchList);
            reclAssets.setAdapter(hdWalletAssetAdapter);
        } else {
            HdWalletAssetAdapter hdWalletAssetAdapter = new HdWalletAssetAdapter(walletInfo);
            reclAssets.setAdapter(hdWalletAssetAdapter);
        }
    }
}