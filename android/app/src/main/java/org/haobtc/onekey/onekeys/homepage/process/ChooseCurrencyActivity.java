package org.haobtc.onekey.onekeys.homepage.process;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.adapter.HomeHdAdapter;
import org.haobtc.onekey.aop.SingleClick;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseCurrencyActivity extends BaseActivity {

    @BindView(R.id.recl_hd_list)
    RecyclerView reclHdList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_currency;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        ArrayList<String> amountList = new ArrayList<>();
        amountList.add("13.5");
        amountList.add("19.1");
        HomeHdAdapter homeHdAdapter = new HomeHdAdapter(amountList);
        reclHdList.setAdapter(homeHdAdapter);
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.lin_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.lin_search:
                break;
        }
    }
}