package org.haobtc.keymanager.activities.personalwallet;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.aop.SingleClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.xpub;

public class ImportHistoryWalletActivity extends BaseActivity {

    public static final String TAG = ImportHistoryWalletActivity.class.getSimpleName();
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.create_trans_one2one)
    Button createTransOne2one;

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_history_wallet;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

    }

    @Override
    public void initData() {

    }

    @SingleClick
    @OnClick({R.id.img_back, R.id.create_trans_one2one})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.create_trans_one2one:
                // new version code
                CommunicationModeSelector.runnables.clear();
                CommunicationModeSelector.runnables.add(null);
                CommunicationModeSelector.runnables.add(runnable2);
                Intent intent = new Intent(this, CommunicationModeSelector.class);
                intent.putExtra("tag", TAG);
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private Runnable runnable2 = () -> showConfirmPubDialog(xpub);

    private void showConfirmPubDialog(String xpub) {
        Intent intent1 = new Intent(ImportHistoryWalletActivity.this, ChooseHistryWalletActivity.class);
        intent1.putExtra("history_xpub", xpub);
        startActivity(intent1);
        finish();
    }
}
