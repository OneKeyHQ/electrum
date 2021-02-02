package org.haobtc.onekey.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.transaction.CheckChainDetailWebActivity;
import org.haobtc.onekey.aop.SingleClick;
import org.haobtc.onekey.business.update.AutoCheckUpdate;
import org.haobtc.onekey.constant.StringConstant;
import org.haobtc.onekey.ui.base.BaseActivity;

/** @author liyan */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;

    @BindView(R.id.attempt_update)
    RelativeLayout update;

    @BindView(R.id.update_version)
    TextView updateVersion;

    @BindView(R.id.tet_s5)
    TextView tetS5;

    private AutoCheckUpdate mAutoCheckUpdate;

    @Override
    public void init() {
        mAutoCheckUpdate = AutoCheckUpdate.getInstance(this);
        String versionName;
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            versionName = "1.0.0";
        }
        updateVersion.setText(String.format("V%s", versionName));
    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.attempt_update, R.id.tet_s5, R.id.service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.attempt_update:
                mAutoCheckUpdate.checkUpdate(getSupportFragmentManager(), true);
                break;
            case R.id.tet_s5:
                CheckChainDetailWebActivity.start(
                        mContext, "userAgreement", StringConstant.USER_URL);
                break;
            case R.id.service:
                SupportActivity.start(mContext);
                break;
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAutoCheckUpdate.onDestroy();
    }
}
