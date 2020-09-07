package org.haobtc.keymanager.activities.settings;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.aop.SingleClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivateBackupSuccessActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.text_finish)
    TextView textFinish;
    @BindView(R.id.recovery2Key)
    Button btnBackup2Key;
    @BindView(R.id.btn_finish)
    Button btnFinish;
    @Override
    public int getLayoutId() {
        return R.layout.activity_activite_backup_success;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.btn_finish, R.id.recovery2Key, R.id.text_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.btn_finish:
            case R.id.text_finish:
                finish();
                break;
            case R.id.recovery2Key:
                Intent intent = new Intent(this, CommunicationModeSelector.class);
                intent.putExtra("extras", getIntent().getStringExtra("message"));
                intent.setAction("recovery");
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}
