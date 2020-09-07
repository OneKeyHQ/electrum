package org.haobtc.keymanager.activities.settings.recovery_set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.common.base.Strings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.haobtc.keymanager.MainActivity;
import org.haobtc.keymanager.R;
import org.haobtc.keymanager.activities.BackupWaySelector;
import org.haobtc.keymanager.activities.base.BaseActivity;
import org.haobtc.keymanager.activities.service.CommunicationModeSelector;
import org.haobtc.keymanager.activities.service.NfcNotifyHelper;
import org.haobtc.keymanager.adapter.BixinkeyBackupAdapter;
import org.haobtc.keymanager.aop.SingleClick;
import org.haobtc.keymanager.event.ButtonRequestEvent;
import org.haobtc.keymanager.event.FinishEvent;
import org.haobtc.keymanager.event.HandlerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;

import static org.haobtc.keymanager.activities.service.CommunicationModeSelector.isNFC;


public class BackupRecoveryActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tet_keyName)
    TextView tetKeyName;
    @BindView(R.id.reclCheckKey)
    RecyclerView reclCheckKey;
    public final static String TAG = BackupRecoveryActivity.class.getSimpleName();
    private List<String> deviceValue;
    private SharedPreferences.Editor edit;
    private String homeUnbackup;
    private String bleName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_backup_recovery;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        homeUnbackup = getIntent().getStringExtra("home_un_backup");
        String contrastDeviceId = getIntent().getStringExtra("contrastDeviceId");
        EventBus.getDefault().register(this);
        if ("home_un_backup".equals(homeUnbackup) || "create_to_backup".equals(homeUnbackup)) {
            if (Ble.getInstance().getConnetedDevices().size() != 0) {
                if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
                    EventBus.getDefault().postSticky(new HandlerEvent());
                }
            }
            Intent intent = new Intent(this, CommunicationModeSelector.class);
            intent.putExtra("tag", TAG);
            intent.putExtra("contrastDeviceId",contrastDeviceId);
            startActivity(intent);
        }
    }

    @Override
    public void initData() {
        bleName = getIntent().getStringExtra("ble_name");
    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceValue = new ArrayList<>();
        SharedPreferences backup = getSharedPreferences("backup", MODE_PRIVATE);
        edit = backup.edit();
        Map<String, ?> backAll = backup.getAll();
        //key
        for (Map.Entry<String, ?> entry : backAll.entrySet()) {
            String mapValue = (String) entry.getValue();
            if (!TextUtils.isEmpty(mapValue)) {
                deviceValue.add(entry.getKey() + ":" + mapValue);
            }
        }
        if (deviceValue != null) {
            BixinkeyBackupAdapter backupAdapter = new BixinkeyBackupAdapter(deviceValue);
            reclCheckKey.setAdapter(backupAdapter);
            backupAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @SingleClick
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    switch (view.getId()) {
                        case R.id.relativeLayout_bixinkey:
                            Intent intent = new Intent(BackupRecoveryActivity.this, BackupMessageActivity.class);
                            intent.putExtra("label", deviceValue.get(position).split(":", 3)[1]);
                            intent.putExtra("message", deviceValue.get(position).split(":", 3)[2]);
                            intent.putExtra("ble_name", bleName);
                            intent.putExtra("tag", "recovery");
                            startActivity(intent);
                            break;
                        case R.id.linear_delete:
                            String deviceID = deviceValue.get(position).split(":", 3)[0];
                            edit.remove(deviceID).apply();
                            deviceValue.remove(position);
                            backupAdapter.notifyItemChanged(position);
                            backupAdapter.notifyDataSetChanged();
                            mToast(getString(R.string.delete_succse));
                            break;
                        default:
                    }
                }
            });
        }
    }

    @SingleClick(value = 2000)
    @OnClick({R.id.img_back, R.id.tet_keyName})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                CommunicationModeSelector.backupTip = false;
                if ("create_to_backup".equals(homeUnbackup)) {
                    mIntent(MainActivity.class);
                } else {
                    finish();
                }
                break;
            case R.id.tet_keyName:
//                if (Ble.getInstance().getConnetedDevices().size() != 0) {
//                    if (Ble.getInstance().getConnetedDevices().get(0).getBleName().equals(bleName)) {
//                        EventBus.getDefault().postSticky(new HandlerEvent());
//                    }
//                }
//                CommunicationModeSelector.backupTip = true;
//                Intent intent = new Intent(this, CommunicationModeSelector.class);
//                intent.putExtra("tag", TAG);
//                startActivity(intent);
                Intent intent = new Intent(this, BackupWaySelector.class);
                intent.putExtra("get_feature", true);
                startActivity(intent);
                break;
            default:
        }
    }
    @Subscribe
    public void onFinish(FinishEvent event) {
        if ("home_un_backup".equals(homeUnbackup)) {
            finish();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onButtonRequest(ButtonRequestEvent event) {
        if (isNFC) {
            EventBus.getDefault().removeStickyEvent(event);
            Intent intent = new Intent(this, NfcNotifyHelper.class);
            intent.putExtra("is_button_request", true);
            startActivity(intent);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
