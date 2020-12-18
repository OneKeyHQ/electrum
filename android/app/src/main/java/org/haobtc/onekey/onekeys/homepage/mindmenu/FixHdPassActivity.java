package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.content.Intent;
import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.bean.LocalWalletInfo;
import org.haobtc.onekey.constant.Constant;
import org.haobtc.onekey.manager.PreferencesManager;
import org.haobtc.onekey.ui.activity.SoftPassActivity;
import org.haobtc.onekey.utils.NavUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FixHdPassActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_fix_hd_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.rel_fix_pass, R.id.reset_pass})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.rel_fix_pass:
               List<String> typeList = new ArrayList<>();
                Map<String, ?> jsonToMap = PreferencesManager.getAll(this, Constant.WALLETS);
                jsonToMap.entrySet().forEach(stringEntry -> {
                    LocalWalletInfo info = LocalWalletInfo.objectFromData(stringEntry.getValue().toString());
                    String type = info.getType();
                    String label = info.getLabel();
                    if (!type.contains("hw") && !"btc-watch-standard".equals(type)) {
                        typeList.add(label);
                    }
                });
                if (typeList.isEmpty()) {
                    mToast(getString(R.string.has_not_local_wallet_need_pass));
                    return;
                }
                intent = new Intent(this, SoftPassActivity.class);
                intent.putExtra(Constant.OPERATE_TYPE, SoftPassActivity.CHANGE);
                startActivity(intent);
                break;
            case R.id.reset_pass:
                NavUtils.gotoResetAppActivity(this);
                break;
            default:
                break;
        }
    }

}
