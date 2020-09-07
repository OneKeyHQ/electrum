package org.haobtc.keymanager.activities.settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.aop.SingleClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckXpubResultActivity extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.label)
    TextView label;
    @BindView(R.id.xpub)
    TextView xpub;
    @BindView(R.id.copy_xpub)
    TextView copyXpub;

    @Override
    public int getLayoutId() {
        return R.layout.check_xpub;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String labels = intent.getStringExtra("label");
        String xpubs = intent.getStringExtra("xpub");
        label.setText(labels);
        xpub.setText(xpubs);
    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.copy_xpub})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
               // Ble.getInstance().disconnectAll();
                break;
            case R.id.copy_xpub:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                assert clipboardManager != null;
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, xpub.getText()));
                Toast.makeText(this, R.string.copysuccess, Toast.LENGTH_LONG).show();
                break;
            default:
        }
    }
}
