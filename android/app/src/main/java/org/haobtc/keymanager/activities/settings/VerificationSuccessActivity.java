package org.haobtc.keymanager.activities.settings;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.utils.DateUitls;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerificationSuccessActivity extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.imgStatus)
    ImageView imgStatus;
    @BindView(R.id.testStatus)
    TextView testStatus;
    @BindView(R.id.testContent)
    TextView testContent;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    @BindView(R.id.test_last_time)
    TextView testLastTime;
    private int status = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_verification_success;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String verificationFail = intent.getStringExtra("verification_fail");
        if (!TextUtils.isEmpty(verificationFail)) {
            status = 1;
        } else {
            String checkTime = intent.getStringExtra("last_check_time");
            if (!Strings.isNullOrEmpty(checkTime)){
                long aLong = Long.parseLong(checkTime);
                String lastCheckTime = DateUitls.getDateToStringX(aLong*1000);
                testLastTime.setText(String.format("%s%s", getString(R.string.last_check_time), lastCheckTime));
            }
        }
        datas();
    }

    private void datas() {
        switch (status) {
            case 0:
                imgStatus.setImageDrawable(getDrawable(R.drawable.tongguo));
                testStatus.setText(getString(R.string.verifying_pass));
                testContent.setText(getString(R.string.verifying_firmware));
                break;
            case 1:
                imgStatus.setImageDrawable(getDrawable(R.drawable.fail));
                testStatus.setText(getString(R.string.verifying_fail));
                testContent.setText(getString(R.string.contact_service));
                break;
            case 2:
                imgStatus.setImageDrawable(getDrawable(R.drawable.overtime));
                testStatus.setText(getString(R.string.verifying_overtime));
                testContent.setText(getString(R.string.verifying_retry));
                break;
            default:
        }
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.btn_finish:
                finishAffinity();
                break;
            default:
        }
    }
}
