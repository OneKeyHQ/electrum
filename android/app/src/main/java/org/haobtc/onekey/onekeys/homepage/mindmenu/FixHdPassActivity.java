package org.haobtc.onekey.onekeys.homepage.mindmenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.onekeys.dialog.SetHDWalletPassActivity;
import org.haobtc.onekey.onekeys.dialog.SetLongPassActivity;
import org.haobtc.onekey.utils.NavUtils;

import butterknife.OnClick;

import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE;
import static org.haobtc.onekey.constant.Constant.SOFT_HD_PASS_TYPE_SHORT;

public class FixHdPassActivity extends BaseActivity {
    private SharedPreferences preferences;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fix_hd_pass;
    }

    @Override
    public void initView() {
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
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
                if (SOFT_HD_PASS_TYPE_SHORT.equals(preferences.getString(SOFT_HD_PASS_TYPE, SOFT_HD_PASS_TYPE_SHORT))) {
                    intent = new Intent(this, SetHDWalletPassActivity.class);
                } else {
                    intent = new Intent(this, SetLongPassActivity.class);
                }
                intent.putExtra("importHdword", "fixHdPass");
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
