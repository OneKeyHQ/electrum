package org.haobtc.onekey.activities.sign;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.onekey.R;
import org.haobtc.onekey.activities.base.BaseActivity;
import org.haobtc.onekey.aop.SingleClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckSignResultActivity extends BaseActivity {

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

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_sign_result;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        boolean verify = intent.getBooleanExtra("verify", false);
        if (!verify) {
            imgStatus.setImageDrawable(getDrawable(R.drawable.fail));
            testStatus.setText(getString(R.string.sign_check_fail));
            btnFinish.setText(getString(R.string.confirm));
            testContent.setVisibility(View.GONE);
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
                finish();
                break;
            case R.id.btn_finish:
                finish();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}
