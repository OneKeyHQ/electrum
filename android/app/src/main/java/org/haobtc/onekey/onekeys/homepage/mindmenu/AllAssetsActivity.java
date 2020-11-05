package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;

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